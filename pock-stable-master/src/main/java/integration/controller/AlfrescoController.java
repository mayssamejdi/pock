package integration.controller;

import integration.service.CmisService;
import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alfresco")
public class AlfrescoController {

    private static Logger log = LoggerFactory.getLogger(AlfrescoController.class);

    @Autowired
    CmisService cmisService;

    @PostMapping("/create")
    String createDocument(@RequestParam(required = true,defaultValue = "testDocument.txt") String docName){

        if(!docName.isEmpty()){

            Document docA = cmisService.createDocument(cmisService.getRootFolder(), docName);

            log.info("Removing created documents...");

            return "Document created succesfully";

        }

        return "Error creating document !";

    }





}
