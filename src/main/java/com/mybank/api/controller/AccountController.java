package com.mybank.api.controller;

import com.mybank.api.domain.AccountDetails;
import com.mybank.api.domain.AccountTransaction;
import com.mybank.api.service.UploadService;
import com.mybank.api.service.AccountService;
import com.mybank.api.service.FileLocalUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountService accountService;


    @Autowired
    private UploadService service;

    /**
     * Post new transaction
     * @param accountTransaction
     * @param accountid
     * @return
     */
    @PostMapping("/restricted/account/{accountid}")
    public String accountstatement(@RequestBody AccountTransaction accountTransaction, @PathVariable("accountid") String accountid){
        return "Welcome to account service, here is your account statement";
    }


    @GetMapping("/restricted/account/{accountid}")
    public ResponseEntity<AccountDetails> accountDetail(@PathVariable("accountid") String accountid){
        String.format("Hello Account %s, Welcome to view Account statement",accountid);
        AccountDetails accoundDetails = accountService.getAccountDetails(accountid);
        return new ResponseEntity<AccountDetails>(accoundDetails, HttpStatus.OK);

    }

    @GetMapping("/restricted/statement/{accountid}")
    public ResponseEntity<List<AccountTransaction>> statement(@PathVariable("accountid") String accountid){

           String.format("Hello Account %s, Welcome to view Account statement", accountid);
           List<AccountTransaction> statement = accountService.getTransactionDetails(accountid);
           return new ResponseEntity<List<AccountTransaction>>(statement, HttpStatus.OK);
      }

    @GetMapping("/restricted/statement/{accountid}/{format}")
      public void downloadStatement(@PathVariable("accountid") String accountid, @PathVariable("format") String format, HttpServletResponse response ){
        try {
            accountService.downloadReport(accountid,format,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @PostMapping("/restricted/local/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("Upload request received for " + file.getOriginalFilename());
        String message = "";
        try {
            service.uploadFile(file);

            message = "File Uploaded successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "File Upload Failed: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }



    @PostMapping(value= "/restricted/upload")
    public ResponseEntity<String> uploadInS3(@RequestPart(value= "file") final MultipartFile multipartFile) {
        LOGGER.info("Request Received for {}",multipartFile.getName());
        service.uploadFile(multipartFile);
        final String response = "[" + multipartFile.getOriginalFilename() + "] uploaded successfully.";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
