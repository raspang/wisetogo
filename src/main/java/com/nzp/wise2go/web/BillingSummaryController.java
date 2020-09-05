package com.nzp.wise2go.web;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.entities.BillingSummary;
import com.nzp.wise2go.entities.BillingDetail;
import com.nzp.wise2go.entities.Receipt;
import com.nzp.wise2go.exception.ResourceNotFoundException;
import com.nzp.wise2go.repositories.CustomerRepository;
import com.nzp.wise2go.repositories.PaymentDescriptionRepository;
import com.nzp.wise2go.repositories.ReceiptRepository;
import com.nzp.wise2go.service.ReportService;

import net.sf.jasperreports.engine.JRException;

import com.nzp.wise2go.repositories.BillingDetailRepository;
import com.nzp.wise2go.repositories.BillingSummaryRepository;


@Controller
@RequestMapping("/billingsummaries")
public class BillingSummaryController
{

	@Autowired
	private CustomerRepository customerRepository;
		
	@Autowired
	private PaymentDescriptionRepository paymentDescriptionRepository;
	
	@Autowired
	private BillingSummaryRepository billingSummaryRepository;
	
	@Autowired
	private ReceiptRepository receiptRepository;
	
	@Autowired
	private BillingDetailRepository billingDetailRepository;
	
	@Autowired
	private ReportService reportService;
	

	@GetMapping("/{customerId}/list")
	public String showBillingSummaries(@PathVariable Long customerId, HttpServletRequest request, Model model) {
		Optional<Customer> customer = customerRepository.findById(customerId);
		Customer theCustomer =  customer.orElse(null);
		BillingSummary billingSummary = new BillingSummary(theCustomer);

		Boolean isPaid = false;
        int page = 0; 
        int size = 10; 
        
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        
        if (request.getParameter("p") != null && !request.getParameter("p").isEmpty()) {
        	isPaid = Boolean.parseBoolean(request.getParameter("p"));
        }
		
        int available = billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(theCustomer, false).size();
        model.addAttribute("available", available);
		model.addAttribute("customer", theCustomer);
		model.addAttribute("billingSummary", billingSummary);
		model.addAttribute("p", isPaid);
		model.addAttribute("billingSummaries", billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(theCustomer, isPaid, PageRequest.of(page, size)) );

		return "billingsummary/customer-billings";
		
	}

	@GetMapping("/{customerId}/create-billing")
	public String createBillingSummary(@PathVariable Long customerId) {
		Customer theCustomer = customerRepository.findById(customerId).orElseThrow(
				() -> new ResourceNotFoundException("customer", "id", customerId));
		BillingSummary billingSummary = new BillingSummary();
		billingSummary.setCustomer(theCustomer);
		billingSummary = billingSummaryRepository.save(billingSummary);
		return "redirect:/billingsummaries/"+theCustomer.getId()+"/showFormForAdd/"+billingSummary.getId();
		
	}
	
	@GetMapping("/{customerId}/showFormForAdd/{billingSummaryId}")
	public String showFormForAdd(@PathVariable Long customerId,	@PathVariable Long billingSummaryId,	
			Model theModel) {
		BillingSummary theBillingSummary = billingSummaryRepository.findById(billingSummaryId).orElseThrow(
				()-> new ResourceNotFoundException("BillingSummary", "id", billingSummaryId));
		
		BillingDetail billingDetail = new BillingDetail();
		billingDetail.setBillingSummary(theBillingSummary);
		
		List<BillingDetail> billingDetails = billingDetailRepository.findByBillingSummaryOrderByIdDesc(theBillingSummary);
		Double totalAmmount = 0.0;
		for(BillingDetail tempBillingDetail : billingDetails) {
			totalAmmount += tempBillingDetail.getAmount();
		}
		theBillingSummary.setTotalAmount(totalAmmount);
		
		theModel.addAttribute("customer", theBillingSummary.getCustomer());
		theModel.addAttribute("paymentDescriptions", paymentDescriptionRepository.findByEnable(true));	
		theModel.addAttribute("billingSummary", theBillingSummary);
		theModel.addAttribute("billingDetail", billingDetail);
		theModel.addAttribute("billingDetails", billingDetails);
		
		return "billingsummary/customer-billing-form";
	}
	

	
	@PostMapping("/save")
	public String savePayment(@Valid @ModelAttribute("billingSummary") BillingSummary theBillingSummary, BindingResult bindingResult, Model theModel) {
		
		if(bindingResult.hasErrors()) {	
			
			theModel.addAttribute("customer", theBillingSummary.getCustomer());
			
			theModel.addAttribute("paymentDescriptions", paymentDescriptionRepository.findByEnable(true));
			
			//theModel.addAttribute("paymentDetails", theBillingSummary.getPaymentDetails());		
			
			return "billingsummary/customer-billing-form";
		}
		BillingSummary billingSummary = billingSummaryRepository.findFirstByIsNextDueDate(true);
		if(billingSummary != null) {
			billingSummary.setIsNextDueDate(false);
			billingSummaryRepository.save(billingSummary);
		}
		billingSummaryRepository.save(theBillingSummary);
		return "redirect:/billingsummaries/"+theBillingSummary.getCustomer().getId()+"/list";
	}

	
	
	

	 
	
		
	@GetMapping("/{customerId}/settle-payment")
	public String showBillingSummaries(@PathVariable Long customerId,  Model model) {
		Customer theCustomer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
		
		List<BillingSummary> billingSummaries =  billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(theCustomer, false);
		
		Double totalAmount = 0.0;
		for(int index = 0; index < billingSummaries.size(); index++)
			totalAmount += billingSummaries.get(index).getTotalAmount();
		
		Receipt receipt = new Receipt();
		receipt.setDatePaid(LocalDate.now());
		receipt.setTotalAmount(totalAmount);
		receipt.setBillingSummaries(billingSummaries);		
		
		Receipt createdReceipt = receiptRepository.save(receipt);
		
		for(BillingSummary billingSummary : billingSummaries) {
			billingSummary.setIsPaid(true);
			billingSummaryRepository.save(billingSummary);
		}

		return "redirect:/billingsummaries/"+theCustomer.getId()+"/receipt/"+createdReceipt.getId()+"/success";
	}
	
	@GetMapping("/{customerId}/receipt/{receiptId}/success")
	public String showSuccess(@PathVariable Long customerId, @PathVariable Long receiptId, Model model) {
	
		Customer theCustomer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
		
		Receipt theReceipt = receiptRepository.findById(receiptId).orElseThrow(() -> new ResourceNotFoundException("Receipt", "id", receiptId));
		
		model.addAttribute("customer", theCustomer);
		
		model.addAttribute("receipt", theReceipt);
		
		return "billingsummary/success";
	}
	

	@GetMapping("/report/{customerId}/{format}")
	public String generateReport(@PathVariable String format, @PathVariable Long customerId) 
	    		throws FileNotFoundException, JRException {
	     return reportService.exportReport(format, customerId);
	}
	
	@GetMapping("/{customerId}/receipt/{receiptId}/{format}")
	public String generateReceipt(@PathVariable String format, @PathVariable Long customerId,
			@PathVariable Long receiptId) 
	    		throws FileNotFoundException, JRException {
	     return reportService.exportReceipt(format, customerId, receiptId);
	}
}
