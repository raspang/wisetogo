package com.nzp.wise2go.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.nzp.wise2go.entities.BillingSummary;
import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.entities.Receipt;
import com.nzp.wise2go.entities.User;
import com.nzp.wise2go.entities.BillingDetail;
import com.nzp.wise2go.exception.ResourceNotFoundException;
import com.nzp.wise2go.repositories.BillingSummaryRepository;
import com.nzp.wise2go.repositories.CustomerRepository;
import com.nzp.wise2go.repositories.ReceiptRepository;
import com.nzp.wise2go.repositories.UserRepository;

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
	private UserRepository userRepository;

    public String exportReport(String reportFormat, Long customerId) throws FileNotFoundException, JRException {
    	
        String path = "C:\\Users\\"+System.getProperty("user.name")+"\\Desktop\\Report";
        
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        
        List<BillingSummary> billingSummaries = billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(customer, false);
        
        if(billingSummaries.isEmpty())
        	return "redirect:/billingsummaries/"+customer.getId()+"/list";
        
        
        BillingSummary currentCharges = billingSummaries.get(0);    
        Double amountBalance = 0d;
        Double rebate = 0d;
        
        
        for(BillingDetail paymentDetail : currentCharges.getBillingDetails() ) {
        	if(paymentDetail.getPaymentDescription().equalsIgnoreCase("Rebate"))
        		rebate += paymentDetail.getAmount();
        	else
        		amountBalance += paymentDetail.getAmount();
        }
        
        
        // More than one billing
        String prevDueDate = "Thank You";
        Double prevAmountBalance = 0d;
        String invId = String.valueOf(currentCharges.getId());
        for(int index = 1; index < billingSummaries.size(); index++) {
        	prevDueDate = "DUE IMMEDIATELY";
        	prevAmountBalance += billingSummaries.get(index).getTotalAmount();
        	
        }
        
        invId = "BIL"+currentCharges.getId();
        
        //load file and compile it
        File file = ResourceUtils.getFile("classpath:billing.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(billingSummaries);
        Map<String, Object> parameters = new HashMap<>();
        
        parameters.put("billingTo", customer.getFullName());
        parameters.put("address", customer.getAddress());
        parameters.put("id", invId); 
        parameters.put("amountBalance", String.valueOf(amountBalance));
        parameters.put("rebate", String.valueOf(rebate));
        parameters.put("prevAmountBalance", String.valueOf(prevAmountBalance));
        parameters.put("prevDueDate", prevDueDate);
        parameters.put("dueDate", currentCharges.getNextDueDateStr());
        parameters.put("totalCharges", String.valueOf(currentCharges.getTotalAmount()));
        parameters.put("statementDate",  currentCharges.getDateStr());
        
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        if (reportFormat.equalsIgnoreCase("html")) {
            JasperExportManager.exportReportToHtmlFile(jasperPrint, path + "\\BILLING-"+customer.getLastName()+invId+".html");
        }
        if (reportFormat.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\BILLING-"+customer.getLastName()+invId+".pdf");
        }

        return "redirect:/billingsummaries/"+customer.getId()+"/list";
    }
    
    public String exportReceipt(String reportFormat, Long customerId, Long receiptId) throws FileNotFoundException, JRException {
    	
        String path = "C:\\Users\\"+System.getProperty("user.name")+"\\Desktop\\Report";
        
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
       
        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(() -> new ResourceNotFoundException("Receipt", "id", receiptId));
        
        User issuedBy = userRepository.findById(receipt.getCreatedBy()).orElseThrow(() -> new ResourceNotFoundException("User", "id", receipt.getCreatedBy()));
        List<BillingSummary> billingSummaries = receipt.getBillingSummaries();
        
        if(billingSummaries.isEmpty())
        	return "redirect:/billingsummaries/"+customer.getId()+"/list";
        
        BillingSummary currentCharges = billingSummaries.get(0);    
        Double amountBalance = 0d;
        Double rebate = 0d;
        
        for(BillingDetail paymentDetail : currentCharges.getBillingDetails() ) {
        	if(paymentDetail.getPaymentDescription().equalsIgnoreCase("Rebate"))
        		rebate += paymentDetail.getAmount();
        	else
        		amountBalance += paymentDetail.getAmount();
        }
        
        // More than one billing
        Double prevAmountBalance = 0d;
        String recId = String.valueOf(currentCharges.getId());
        for(int index = 1; index < billingSummaries.size(); index++) {
        	prevAmountBalance += billingSummaries.get(index).getTotalAmount();
        }
        
        //load file and compile it
        File file = ResourceUtils.getFile("classpath:receipt.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(billingSummaries);
        Map<String, Object> parameters = new HashMap<>();
        
        parameters.put("billingTo", customer.getFullName());
        parameters.put("address", customer.getAddress());
        parameters.put("id", recId); 
        parameters.put("amountBalance", String.valueOf(amountBalance));
        parameters.put("rebate", String.valueOf(rebate));
        parameters.put("totalCharges", String.valueOf(receipt.getTotalAmount()));
        parameters.put("receiptDate",  receipt.getDatePaidStr());
        parameters.put("receivedBy", issuedBy.getName());
        parameters.put("statementDate", receipt.getDatePaidStr());
        
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        if (reportFormat.equalsIgnoreCase("html")) {
            JasperExportManager.exportReportToHtmlFile(jasperPrint, path + "\\RECEIPT-"+customer.getLastName()+recId+".html");
        }
        if (reportFormat.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\RECEIPT-"+customer.getLastName()+recId+".pdf");
        }

        return "redirect:/receipts/"+customer.getId()+"/list";
    }
}
