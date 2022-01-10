package integration.service;//package integration.service;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.springframework.stereotype.Service;
import org.apache.chemistry.opencmis.client.api.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AlfrescoService {

    private static Map<String, Session> connections = new ConcurrentHashMap<String, Session>();

    public AlfrescoService() {

    }

    public Session getSession(String connectionName, String username, String pwd) {
        Session session = connections.get(connectionName);
        if (session == null) {
            System.out.println("not connected, creating new connection to" +
                    "Alfresco with connection id(" + connectionName + ")");

            //creating new connection
            SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
            Map<String, String> params = new HashMap<String, String>();
            params.put(SessionParameter.USER, username);
            params.put(SessionParameter.PASSWORD, pwd);
            params.put(SessionParameter.ATOMPUB_URL, "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/atom");
            params.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
            params.put(SessionParameter.COMPRESSION, "true");
            params.put(SessionParameter.CACHE_TTL_OBJECTS, "0");

            List<Repository> repositories = sessionFactory.getRepositories(params);

            Repository alfrescoRepository = null;

            if (repositories != null && repositories.size() > 0) {
                System.out.println("found (" + repositories.size() + ") alfresco repositories");

                alfrescoRepository = repositories.get(0);

                System.out.println("info about the first alfresco repo [ID " + alfrescoRepository.getId());
            } else {
                throw new CmisConnectionException("could not connect, no repository found");
            }

            session = alfrescoRepository.createSession();

            connections.put(connectionName, session);

        } else {
            System.out.println("Already connected (" + connectionName + ")");
        }

        return session;
    }
}


