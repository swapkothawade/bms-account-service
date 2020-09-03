package com.mybank.api.service;

import com.mybank.api.dao.AccountDao;
import com.mybank.api.domain.AccountDetails;
import com.mybank.api.domain.AccountTransaction;
import com.mybank.api.domain.TransactionType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class AccountService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AccountDao accountDao;
	
	public AccountDetails getAccountDetails(String accountid) {
		return accountDao.getAccountDetail(accountid);
	}

	public List<AccountTransaction> getTransactionDetails(String accountid) {
		return accountDao.getAccountStatment(accountid);
	}

	/**
	 * Download reports in requested format
	 * @param accountid
	 * @param reportformat
	 * @param response
	 * @throws Exception
	 */
	public void downloadReport(String accountid, String reportformat, HttpServletResponse response) throws Exception {
		log.info("Download request received for {} and Requested format {}",accountid,reportformat);
		List<AccountTransaction> transactions = accountDao.getAccountStatment(accountid);
		String filename = String.format("AccountStatement%s.%s", LocalDateTime.now(), reportformat);
		if (reportformat.equalsIgnoreCase("csv")) {
			downloadCsvReport(transactions, filename, response);
		} else if (reportformat.equalsIgnoreCase("xlsx") || reportformat.equalsIgnoreCase("xls")) {
			downloadXlsReport(transactions, filename, response);
		} else {
			throw new IllegalArgumentException("Invalid request type");

		}
		log.info("File Generated {} ",filename);
	}

	private ByteArrayInputStream downloadXlsReport(List<AccountTransaction> transactions, String filename,
			HttpServletResponse response) throws Exception {

		String[] HEADERS = { "Transaction Date", "Transaction Remarks", "Withdrawal Amount (INR )",
				"Deposit Amount (INR )", "Balance (INR )" };
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
			CreationHelper createHelper = workbook.getCreationHelper();

			Sheet sheet = workbook.createSheet("Account Statement");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.BLUE.getIndex());

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);

			// Row for Header
			Row headerRow = sheet.createRow(0);

			// Header
			for (int col = 0; col < HEADERS.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERS[col]);
				cell.setCellStyle(headerCellStyle);
			}

			// CellStyle for Age
			CellStyle dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));

			int rowIdx = 1;
			for (AccountTransaction transaction : transactions) {
				Row row = sheet.createRow(rowIdx++);

				row.createCell(0).setCellValue("" + transaction.getTransactionDateTime());
				row.createCell(1).setCellValue(transaction.getTransactionRemark());
				row.createCell(2).setCellValue(transaction.getTransactionType() == TransactionType.WITHDRAWAL
						? transaction.getTransactionAmount() : 0.0);
				row.createCell(3).setCellValue(transaction.getTransactionType() == TransactionType.DEPOSIT
						? transaction.getTransactionAmount() : 0.0);
				row.createCell(3).setCellValue(transaction.getBalance());
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}

	private void downloadCsvReport(List<AccountTransaction> transactions, String filename,
			HttpServletResponse response) {
		CSVPrinter csvPrinter = null;

		try {
			response.setContentType("text/csv");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
			csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader("Transaction Date",
					"Transaction Remarks", "Withdrawal Amount (INR )", "Deposit Amount (INR )", "Balance (INR )"));

			for (AccountTransaction transaction : transactions) {
				csvPrinter.printRecord(
						Arrays.asList(transaction.getTransactionDateTime(), transaction.getTransactionRemark(),
								transaction.getTransactionType() == TransactionType.WITHDRAWAL
										? transaction.getTransactionAmount() : 0.0,
						transaction.getTransactionType() == TransactionType.DEPOSIT ? transaction.getTransactionAmount()
								: 0.0, transaction.getBalance()));
			}
			if (csvPrinter != null)
				csvPrinter.close();

		} catch (IOException e) {
			csvPrinter = null;
			e.printStackTrace();
		}
	}

	/**
	 * Return account id
	 * 
	 * @param accountDetails
	 * @return
	 */
	public long addCustomerAccount(AccountDetails accountDetails) {
		log.info("New Account create request received for user {} ",accountDetails.getEmail());
		long accountId = accountDao.addAccountDetails(accountDetails);
		log.info("Account  ID {} assigned to user {}  ",accountId,accountDetails.getEmail());
		return accountId;

	}
}
