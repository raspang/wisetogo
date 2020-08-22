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
import com.nzp.wise2go.entities.PaymentDetail;
import com.nzp.wise2go.exception.ResourceNotFoundException;
import com.nzp.wise2go.repositories.BillingSummaryRepository;
import com.nzp.wise2go.repositories.CustomerRepository;

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
	

    public String exportReport(String reportFormat, Long customerId) throws FileNotFoundException, JRException {
    	
        String path = "C:\\Users\\"+System.getProperty("user.name")+"\\Desktop\\Report";
        
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        
        List<BillingSummary> billingSummaries = billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(customer, false);
        
        if(billingSummaries.isEmpty())
        	return "redirect:/billingsummaries/"+customer.getId()+"/list";
        
        
        BillingSummary currentCharges = billingSummaries.get(0);    
        Double amountBalance = 0d;
        Double rebate = 0d;
        for(PaymentDetail paymentDetail : currentCharges.getPaymentDetails()) {
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
        try {
        	invId = "INV"+("00000000" + currentCharges.getId()).substring( String.valueOf( currentCharges.getId() ).length());
        }catch(Exception e) {}
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
            JasperExportManager.exportReportToHtmlFile(jasperPrint, path + "\\Billing-"+customer.getLastName()+".html");
        }
        if (reportFormat.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\Billing-"+customer.getLastName()+".pdf");
        }

        return "redirect:/billingsummaries/"+customer.getId()+"/list";
      
       
    }
}
