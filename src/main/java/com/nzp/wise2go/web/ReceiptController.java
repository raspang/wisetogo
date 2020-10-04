package com.nzp.wise2go.web;

import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.exception.ResourceNotFoundException;
import com.nzp.wise2go.repositories.CustomerRepository;
import com.nzp.wise2go.repositories.ReceiptRepository;
import com.nzp.wise2go.service.ReportService;

import net.sf.jasperreports.engine.JRException;


@Controller
@RequestMapping("/receipts")
public class ReceiptController
{
	@Autowired
	private CustomerRepository customerRepository;
	

	@Autowired
	private ReportService reportService;

	@Autowired
	private ReceiptRepository receiptRepository;
	
	@GetMapping("/{customerId}/list")
	public String showReceipts(@PathVariable Long customerId,
			HttpServletRequest request, 
			Model theModel) {
		
		Customer customer = customerRepository.findById(customerId).orElseThrow( () ->
			new ResourceNotFoundException("Customer", "id", customerId)
				);

		Boolean isPaid = false;
        int page = 0; 
        int size = 10; 
        
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        
        theModel.addAttribute("p", isPaid);
		theModel.addAttribute("customer", customer);
		theModel.addAttribute("receipts", receiptRepository.findByCustomerOrderByIdDesc(customer,  PageRequest.of(page, size)) );
		
		return "receipt/customer-receipt";
	}
	
	@GetMapping("/{customerId}/receipt/{receiptId}/{format}")
	public String generateReceipt(@PathVariable String format, @PathVariable Long customerId,
			@PathVariable Long receiptId) 
	    		throws FileNotFoundException, JRException {
	     return reportService.exportReceipt(format, customerId, receiptId);
	}


}
