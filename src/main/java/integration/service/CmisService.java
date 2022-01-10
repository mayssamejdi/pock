package integration.service;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import javax.annotation.PostConstruct;

import integration.DTO.DocumentInfo;
import integration.utility.Constants;
import integration.utility.DirectoryTraverser;
import integration.utility.HttpUtil;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CmisService {


    // Set values from "application.properties" file
    @Value("${alfresco.repository.url}")
    String alfrescoUrl;
    @Value("${alfresco.repository.user}")
    String alfrescoUser;
    @Value("${alfresco.repository.pass}")
    String alfrescoPass;

    // CMIS living session
    private Session session;

    @PostConstruct
    public void init() {

        String alfrescoBrowserUrl = alfrescoUrl + "/api/-default-/public/cmis/versions/1.1/browser";

        Map<String, String> parameter = new HashMap<String, String>();

        parameter.put(SessionParameter.USER, alfrescoUser);
        parameter.put(SessionParameter.PASSWORD, alfrescoPass);

        parameter.put(SessionParameter.BROWSER_URL, alfrescoBrowserUrl);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());

        SessionFactory factory = SessionFactoryImpl.newInstance();
        session = factory.getRepositories(parameter).get(0).createSession();

    }

    public Folder getRootFolder() {
        return session.getRootFolder();
    }

    public Document createDocument(Folder folder, String documentName) {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, documentName);

        byte[] content = "Hello World!".getBytes();
        InputStream stream = new ByteArrayInputStream(content);
        ContentStream contentStream = new ContentStreamImpl(documentName, BigInteger.valueOf(content.length),
                "text/plain", stream);

        return folder.createDocument(properties, contentStream, VersioningState.MAJOR);
    }

    public ObjectId createRelationship(CmisObject sourceObject, CmisObject targetObject, String relationshipName) {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "a new relationship");
        properties.put(PropertyIds.OBJECT_TYPE_ID, relationshipName);
        properties.put(PropertyIds.SOURCE_ID, sourceObject.getId());
        properties.put(PropertyIds.TARGET_ID, targetObject.getId());

        return session.createRelationship(properties);
    }


    public void documentUpload() throws IOException {

        final String uploadURI = "http://localhost:8080/alfresco/service/api/upload";
        final String authURI = "http://localhost:8080/alfresco/service/api/login";
        final String username = "admin";
        final String password = "admin";
        final String inputUri = "C:\\Users\\khalil.beldi\\Desktop\\first_pipeline\\uploads"; // files to be uploaded from this directory
        final String siteID = "testpoc"; //id of the site for e.g if site name is TestPoc the id will be testpoc
        final String uploadDir = "testUpload"; //directory created under document library

        final HttpUtil httpUtil = new HttpUtil();
        String authTicket = Constants.EMPTY;
        try {

            // Get the authentication ticket from alfresco.
            //This authTicket will be used in all subsequent requests in order to get authenticated with alfresco.
            // e.g TICKET_4b36ecaxxxxx5cdc5d782xxxxxxxxxxxx

            authTicket = httpUtil.getAuthTicket(authURI, username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final StringBuffer responseBody = new StringBuffer();

        final File fileObject = new File(inputUri);
        //if uri is a directory the upload all files..
        if (fileObject.isDirectory()) {
            final Set<File> setOfFiles = DirectoryTraverser.getFileUris(fileObject);
            for (Iterator<File> iterator = setOfFiles.iterator(); iterator.hasNext(); ) {
                final File fileObj = iterator.next();
                //call document upload
                if (fileObj.isFile()) {
                    responseBody.append(httpUtil.documentUpload(
                            fileObj, authTicket, uploadURI, siteID,
                            uploadDir));
                    responseBody.append(Constants.BR);
                }
            }
        } else {
            responseBody.append(httpUtil.documentUpload(
                    fileObject, authTicket, uploadURI, siteID,
                    uploadDir));
        }

        System.out.println("Response of upload operation >>>: " + responseBody);
    }

    public void addAspect(CmisObject cmisObject, String aspect) {

        List<Object> aspects = cmisObject.getProperty("cmis:secondaryObjectTypeIds").getValues();
        if (!aspects.contains(aspect)) {
            aspects.add(aspect);
            Map<String, Object> aspectListProps = new HashMap<String, Object>();
            aspectListProps.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspects);
            cmisObject.updateProperties(aspectListProps);
        }

    }

    public void updateProperties(CmisObject cmisObject, Map<String, Object> properties) {
        cmisObject.updateProperties(properties);
    }

    public ItemIterable<Relationship> getRelationships(ObjectId objectId, String relationshipName) {

        ObjectType typeDefinition = session.getTypeDefinition(relationshipName);
        OperationContext operationContext = session.createOperationContext();
        return session.getRelationships(objectId, true, RelationshipDirection.EITHER, typeDefinition, operationContext);

    }

    public ItemIterable<QueryResult> query(String query) {
        return session.query(query, false);
    }

    public List<DocumentInfo> getSiteContent(){

        List<DocumentInfo> docs = new ArrayList<>();

        ItemIterable<QueryResult> results = query("SELECT cmis:name FROM cmis:document WHERE CONTAINS('PATH:\"/app:company_home/st:sites/cm:testpoc/cm:documentLibrary/cm:testUpload/*\"')");

        for(QueryResult hit: results) {
            for(PropertyData<?> property: hit.getProperties()) {

                String queryName = property.getQueryName();
                String value = (String) property.getFirstValue();

                docs.add(new DocumentInfo(value));

                System.out.println(queryName + ": " + value);
            }
            System.out.println("--------------------------------------");
        }

        return docs;

    }

    public void remove(CmisObject object) {

        if (BaseTypeId.CMIS_FOLDER.equals(object.getBaseTypeId())) {
            Folder folder = (Folder) object;
            ItemIterable<CmisObject> children = folder.getChildren();
            for (CmisObject child : children) {
                remove(child);
            }
        }
        session.delete(object);
    }

    public void downloadDocumentByPath(){

        String folder = session.getRootFolder().getPath();

        String path = "/Sites/testpoc/documentLibrary/testUpload/cv_khalil.pdf";

        Document doc = (Document) session.getObjectByPath(path);

        try{

            ContentStream cs = doc.getContentStream(null);
            BufferedInputStream in = new BufferedInputStream(cs.getStream());
            File file = new File("C:\\Users\\khalil.beldi\\Desktop\\" + doc.getName());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            byte[] buf = new byte[1024];

            int n = 0;

            while ((n = in.read(buf)) > 0){

                bos.write(buf,0,n);

            }

            bos.close();
            fos.close();
            in.close();


        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void downloadDocumentByPath(DocumentInfo document){

        String folder = session.getRootFolder().getPath();

        String path = "/Sites/testpoc/documentLibrary/testUpload/"+document.getName();

        System.out.println(path);

        Document doc = (Document) session.getObjectByPath(path);

        try{

            ContentStream cs = doc.getContentStream(null);
            BufferedInputStream in = new BufferedInputStream(cs.getStream());
            File file = new File("C:\\Users\\khalil.beldi\\Desktop\\" + doc.getName());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            byte[] buf = new byte[1024];

            int n = 0;

            while ((n = in.read(buf)) > 0){

                bos.write(buf,0,n);

            }

            bos.close();
            fos.close();
            in.close();


        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private Session getSession() {

        String serverUrl = alfrescoUrl + "/api/-default-/public/cmis/versions/1.0/atom";

        Map<String, String> parameter = new HashMap<String, String>();

        parameter.put(SessionParameter.USER, alfrescoUser);
        parameter.put(SessionParameter.PASSWORD, alfrescoPass);

        parameter.put(SessionParameter.BROWSER_URL, serverUrl);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());

        SessionFactory factory = SessionFactoryImpl.newInstance();
        session = factory.getRepositories(parameter).get(0).createSession();

        return session;
    }




}
