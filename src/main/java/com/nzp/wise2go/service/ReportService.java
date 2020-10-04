package com.nzp.wise2go.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.nzp.wise2go.entities.BillingDetail;
import com.nzp.wise2go.entities.BillingSummary;
import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.entities.Expense;
import com.nzp.wise2go.entities.MonthReport;
import com.nzp.wise2go.entities.Receipt;
import com.nzp.wise2go.exception.ResourceNotFoundException;
import com.nzp.wise2go.repositories.BillingSummaryRepository;
import com.nzp.wise2go.repositories.CustomerRepository;
import com.nzp.wise2go.repositories.ReceiptRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ReportService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private BillingSummaryRepository billingSummaryRepository;

	@Autowired
	private ReceiptRepository receiptRepository;


	@Autowired
	private MonthlyReportService monthlyReportService;

	public void exportReportBilling(String reportFormat, Long customerId) throws FileNotFoundException, JRException {

		String path = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\Report";

		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

		List<BillingSummary> billingSummaries = billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(customer,
				false);

		if (!billingSummaries.isEmpty()) {
			BillingSummary currentCharges = billingSummaries.get(0);
			Double amountBalance = 0d;
			Double rebate = 0d;
			String fileName = "\\BILLING-" + customer.getLastName().toUpperCase() + "-" + currentCharges.getId();

			for (BillingDetail paymentDetail : currentCharges.getBillingDetails()) {
				if (paymentDetail.getPaymentDescription().equalsIgnoreCase("Rebate"))
					rebate += paymentDetail.getAmount();
				else
					amountBalance += paymentDetail.getAmount();
			}

			// More than one billing
			String prevDueDate = "Thank You";
			Double prevAmountBalance = 0d;

			for (int index = 1; index < billingSummaries.size(); index++) {
				prevDueDate = "DUE IMMEDIATELY";
				prevAmountBalance += billingSummaries.get(index).getTotalAmount();

			}

			// load file and compile it
			File file = ResourceUtils.getFile("classpath:billing.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(billingSummaries);
			Map<String, Object> parameters = new HashMap<>();

			parameters.put("billingTo", customer.getFullName());
			parameters.put("address", customer.getAddress());
			parameters.put("id", "Billing No. " + currentCharges.getId());
			parameters.put("amountBalance", String.format("%.2f", amountBalance));
			parameters.put("rebate", String.format("%.2f", rebate));
			parameters.put("prevAmountBalance", String.format("%.2f", prevAmountBalance));
			parameters.put("prevDueDate", prevDueDate);
			parameters.put("dueDate", currentCharges.getNextDueDateStr());
			parameters.put("totalCharges", String.format("%.2f", prevAmountBalance+currentCharges.getTotalAmount()));
			parameters.put("statementDate", currentCharges.getDateStr());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			if (reportFormat.equalsIgnoreCase("html")) {
				JasperExportManager.exportReportToHtmlFile(jasperPrint, path + fileName + ".html");
			}
			if (reportFormat.equalsIgnoreCase("pdf")) {
				JasperExportManager.exportReportToPdfFile(jasperPrint, path + fileName + ".pdf");
			}

		}
	}

	public String exportReceipt(String reportFormat, Long customerId, Long receiptId)
			throws FileNotFoundException, JRException {

		String path = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\Report";

		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

		Receipt receipt = receiptRepository.findById(receiptId)
				.orElseThrow(() -> new ResourceNotFoundException("Receipt", "id", receiptId));


		List<BillingSummary> billingSummaries = receipt.getBillingSummaries();

		if (billingSummaries.isEmpty())
			return "redirect:/billingsummaries/" + customer.getId() + "/list";

		String fileName = "\\RECEIPT-" + customer.getLastName().toUpperCase() +"-"+receipt.getId();

	
		
		
		// load file and compile it
		File file = ResourceUtils.getFile("classpath:receipt.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(billingSummaries);
		Map<String, Object> parameters = new HashMap<>();

		
		// CURRENT
		String curChargesDescription = "";
		String curChargesPlan = "";
		String amountBalance  = "";
		
		BillingSummary currentCharges = billingSummaries.get(0);
	
		int currentIndex = 0;
		for (BillingDetail paymentDetail : currentCharges.getBillingDetails()) {
		
			currentIndex++;
			
			if(currentIndex == 1) {
				parameters.put("curChargesDescription1", paymentDetail.getPaymentDescription());
				parameters.put("curChargesPlan1", currentCharges.getMbps());
				parameters.put("amountBalance1",  String.valueOf(paymentDetail.getAmount()));			
			}else {
				curChargesDescription += paymentDetail.getPaymentDescription()+" ";
				curChargesPlan += currentCharges.getMbps()+" ";
				amountBalance += paymentDetail.getAmount()+" ";
			}
	
		
		}
	
		parameters.put("curChargesDescription2", curChargesDescription);
		parameters.put("curChargesPlan2", curChargesPlan);
		parameters.put("amountBalance2",  amountBalance);
		// END CURRENT
		
		
		// PREV
		String prevChargesDescription = "";
		String prevChargesPlan = "";
		String prevAmountBalance = "";

		currentIndex = 0;
		for (int index = 1; index < billingSummaries.size(); index++) {
			for (BillingDetail paymentDetail : billingSummaries.get(index).getBillingDetails()) {

				currentIndex++;
				
				if(currentIndex < 6) {
					parameters.put("prevChargesDescription"+currentIndex, paymentDetail.getPaymentDescription());
					parameters.put("prevChargesPlan"+currentIndex, currentCharges.getMbps());
					parameters.put("prevAmountBalance"+currentIndex, String.valueOf(paymentDetail.getAmount()));		
				}else {
					
					prevChargesDescription += paymentDetail.getPaymentDescription()+" ";
					prevChargesPlan += currentCharges.getMbps()+" ";
					prevAmountBalance += paymentDetail.getAmount()+" ";
				}
			}
		}
		parameters.put("prevChargesDescription6", prevChargesDescription);
		parameters.put("prevChargesPlan6", prevChargesPlan);
		parameters.put("prevAmountBalance6", prevAmountBalance);	
		// END PREV
		
		
		parameters.put("billingTo", customer.getFullName());	
		parameters.put("id", String.valueOf(receipt.getId()));
	
		parameters.put("receiptDate", receipt.getDatePaidStr());
		parameters.put("receivedBy", receipt.getCreatedBy().getName());
		parameters.put("statementDate", receipt.getDatePaidStr());
		

		
		
		//parameters.put("rebate", String.format("%.2f", rebate));
		
		parameters.put("totalCharges", String.format("%.2f", receipt.getTotalAmount()));
		

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		if (reportFormat.equalsIgnoreCase("html")) {
			JasperExportManager.exportReportToHtmlFile(jasperPrint, path + fileName + ".html");
		}
		if (reportFormat.equalsIgnoreCase("pdf")) {
			JasperExportManager.exportReportToPdfFile(jasperPrint, path + fileName + ".pdf");
		}

		return "redirect:/receipts/" + customer.getId() + "/list";
	}
	
	public String exportMonthlyReport(String reportFormat, Integer month)
			throws FileNotFoundException, JRException {

		 List<String> months = Arrays.asList("January", "February", "March",
				"April", "May", "June", "July", "August", "September", "October", "November", "December");
		
		
		String path = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\Report";

		String fileName = "\\MONTHLY-REPORT-" + months.get(month-1);


		List<MonthReport> monthlyReports = monthlyReportService.getIncomeReport(month);
		
		// load file and compile it
		File file = ResourceUtils.getFile("classpath:report-receipt.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(monthlyReports);
		Map<String, Object> parameters = new HashMap<>();

		Double total = 0d;
		for(MonthReport m : monthlyReports) {
			total += Double.valueOf(m.getTotalAmount());
		}
		parameters.put("overAllTotal", String.format("%.2f",total));	
	

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		if (reportFormat.equalsIgnoreCase("html")) {
			JasperExportManager.exportReportToHtmlFile(jasperPrint, path + fileName + ".html");
		}
		if (reportFormat.equalsIgnoreCase("pdf")) {
			JasperExportManager.exportReportToPdfFile(jasperPrint, path + fileName + ".pdf");
		}

		return "redirect:/reports/list";
	}
	 
	public String exportMonthlyExpense(String reportFormat, Integer month)
			throws FileNotFoundException, JRException {

		 List<String> months = Arrays.asList("January", "February", "March",
				"April", "May", "June", "July", "August", "September", "October", "November", "December");
		
		
		String path = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\Report";

		String fileName = "\\MONTHLY-EXPENSE-" + months.get(month-1);


		List<Expense> monthlyReports = monthlyReportService.getExpense(month);
		
		// load file and compile it
		File file = ResourceUtils.getFile("classpath:report-expenses.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(monthlyReports);
		Map<String, Object> parameters = new HashMap<>();

		Double total = 0d;
		for(Expense m : monthlyReports) {
			total += Double.valueOf(m.getTotalAmount());
		}
		parameters.put("overAllTotal", String.format("%.2f",total));	
	

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		if (reportFormat.equalsIgnoreCase("html")) {
			JasperExportManager.exportReportToHtmlFile(jasperPrint, path + fileName + ".html");
		}
		if (reportFormat.equalsIgnoreCase("pdf")) {
			JasperExportManager.exportReportToPdfFile(jasperPrint, path + fileName + ".pdf");
		}

		return "redirect:/reports/list";
	}
}
