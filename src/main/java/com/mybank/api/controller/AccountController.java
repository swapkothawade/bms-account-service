package com.mybank.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mybank.api.domain.AccountDetails;
import com.mybank.api.domain.AccountTransaction;
import com.mybank.api.service.AccountService;
import com.mybank.api.service.FileLocalUploadService;
import com.mybank.api.service.UploadService;

@RestController
@RequestMapping("/api")
public class AccountController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountService accountService;

	@Autowired
	private UploadService service;

	private List<String> ALLOWED_MIMPE_TYPE = Arrays.asList(
			"application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document", "image/jpeg",
			"image/png", "application/pdf");

	/**
	 * Post new transaction
	 * 
	 * @param accountTransaction
	 * @param accountid
	 * @return
	 */
	@PostMapping("/restricted/account/{accountid}")
	public String accountstatement(@RequestBody AccountTransaction accountTransaction,
			@PathVariable("accountid") String accountid) {
		LOGGER.info("Request Received for Account id " + accountid);
		return "Welcome to account service, here is your account statement";
	}

	@GetMapping("/restricted/account/{accountid}")
	public ResponseEntity<AccountDetails> accountDetail(@PathVariable("accountid") String accountid) {
		String.format("Hello Account %s, Welcome to view Account statement", accountid);
		AccountDetails accoundDetails = accountService.getAccountDetails(accountid);
		return new ResponseEntity<AccountDetails>(accoundDetails, HttpStatus.OK);

	}

	@GetMapping("/restricted/statement/{accountid}")
	public ResponseEntity<List<AccountTransaction>> statement(@PathVariable("accountid") String accountid) {

		String.format("Hello Account %s, Welcome to view Account statement", accountid);
		List<AccountTransaction> statement = accountService.getTransactionDetails(accountid);
		return new ResponseEntity<List<AccountTransaction>>(statement, HttpStatus.OK);
	}

	@GetMapping("/restricted/statement/{accountid}/{format}")
	public void downloadStatement(@PathVariable("accountid") String accountid, @PathVariable("format") String format,
			HttpServletResponse response) {
		try {
			accountService.downloadReport(accountid, format, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insert account details.
	 * 
	 * @param accountDetails
	 * @return
	 */

	@PostMapping("/restricted/account")
	public ResponseEntity<String> addAccount(@RequestBody AccountDetails accountDetails) {
		LOGGER.info("Adding Account Information for User {} ", accountDetails.getEmail());
		long accountId = accountService.addCustomerAccount(accountDetails);
		LOGGER.info("Account Number {} ", accountId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(String.format("Account id %s , Account type %s created for user %s", accountId,
						accountDetails.getAccountType(), accountDetails.getEmail()));

	}

	@PostMapping("/restricted/local/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		String contentType = file.getContentType();
		LOGGER.info("Upload Request Received for {} , Content type {} ", file.getName(), contentType);

		if (!ALLOWED_MIMPE_TYPE.contains(contentType)) {
			LOGGER.info("File not supported,Allowed types are {}",
					ALLOWED_MIMPE_TYPE.stream().collect(Collectors.joining(",")));

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(String.format("File not supported,Allowed types are %s",
							ALLOWED_MIMPE_TYPE.stream().collect(Collectors.joining(","))));
		}

		FileLocalUploadService localFileUploadService = new FileLocalUploadService();
		LOGGER.info("File will be uploaded on local drive");
		String message = "";
		try {
			localFileUploadService.uploadFile(file);

			message = "File Uploaded successfully: " + file.getOriginalFilename();
			LOGGER.info(message);
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} catch (Exception e) {
			message = "File Upload Failed: " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}

	@PostMapping(value = "/restricted/upload")
	public ResponseEntity<String> uploadInS3(@RequestPart(value = "file") final MultipartFile multipartFile) {
		String contentType = multipartFile.getContentType();
		LOGGER.info("Upload Request Received for {} , Content type {} ", multipartFile.getName(), contentType);

		if (!ALLOWED_MIMPE_TYPE.contains(contentType)) {
			LOGGER.info("File not supported,Allowed types are {}",
					ALLOWED_MIMPE_TYPE.stream().collect(Collectors.joining(",")));

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(String.format("File not supported,Allowed types are %s",
							ALLOWED_MIMPE_TYPE.stream().collect(Collectors.joining(","))));
		}
		service.uploadFile(multipartFile);
		final String response = "[" + multipartFile.getOriginalFilename() + "] uploaded successfully.";
		LOGGER.info(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

// Allowed formats - .pdf, Doc or JPG

// application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/jpeg,image/png,application/pdf
