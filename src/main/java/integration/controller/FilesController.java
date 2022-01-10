package integration.controller;

import integration.DTO.DocumentInfo;
import integration.DTO.ResponseMessage;
import integration.entities.FileInfo;
import integration.service.CmisService;
import integration.service.FilesStorageService;
import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "http://localhost:4200")
public class FilesController {

    private static Logger log = LoggerFactory.getLogger(AlfrescoController.class);

    @Autowired
    FilesStorageService storageService;

    @Autowired
    CmisService cmisService;


    @GetMapping("/folderid")
    public String getFolderId(){
        return cmisService.getRootFolder().getId();
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            storageService.save(file);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            cmisService.documentUpload();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }

    }


    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    
     @GetMapping("/create")
    public boolean createDocument(@RequestParam(required = true,defaultValue = "testdocument.txt") String docName){

        String message;

        boolean test = false;

        if(!docName.isEmpty()){

            message = "Document created succesfully";

            Document docA = cmisService.createDocument(cmisService.getRootFolder(), docName);

            log.info(message);

            test = true;

        }else {
            message = "Error creating document !";

            log.info(message);

            test = false;
        }

        return test;

    }

    @PostMapping("/download")
    public void download(@RequestBody DocumentInfo document){
        cmisService.downloadDocumentByPath(document);
    }

    @GetMapping("/content")
    public ResponseEntity<List<DocumentInfo>> getContent(){
        List<DocumentInfo> listOfDocs = this.cmisService.getSiteContent();
        return new ResponseEntity<>(listOfDocs,HttpStatus.OK);
    }
}
