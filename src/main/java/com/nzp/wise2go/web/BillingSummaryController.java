package com.nzp.wise2go.web;

import java.io.FileNotFoundException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.entities.BillingSummary;
import com.nzp.wise2go.entities.PaymentDescription;
import com.nzp.wise2go.entities.PaymentDetail;
import com.nzp.wise2go.exception.ResourceNotFoundException;
import com.nzp.wise2go.repositories.CustomerRepository;
import com.nzp.wise2go.repositories.PaymentDescriptionRepository;
import com.nzp.wise2go.service.ReportService;

import net.sf.jasperreports.engine.JRException;

import com.nzp.wise2go.repositories.BillingSummaryRepository;


@Controller
@RequestMapping("/billingsummaries")
@SessionAttributes({"billingSummary","customer"})
public class BillingSummaryController
{

	@Autowired
	private CustomerRepository customerRepository;
		
	@Autowired
	private PaymentDescriptionRepository paymentDescriptionRepository;
	
	@Autowired
	private BillingSummaryRepository billingSummaryRepository;
	
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
		
		model.addAttribute("customer", theCustomer);
		model.addAttribute("billingSummary", billingSummary);
		model.addAttribute("p", isPaid);
		model.addAttribute("billingSummaries", billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(theCustomer, isPaid, PageRequest.of(page, size)));

		return "billingsummary/customer-billings";
	}
	
	
	
	
	@GetMapping("/{customerId}/showFormForAdd")
	public String showFormForAdd(@PathVariable Long customerId,
			@ModelAttribute("billingSummary") BillingSummary billingSummary,		
			Model theModel) {

		if(billingSummary == null )
			return "redirect:/billingsummaries/list";
		
		Optional<Customer> customer = customerRepository.findById(customerId);
		
		Customer theCustomer = customer.orElse(null);
		
		billingSummary.setCustomer(theCustomer);
		
		Double total = 0.0;
		for(PaymentDetail paymentDetail : billingSummary.getPaymentDetails()) 
			total += paymentDetail.getAmount();	
		billingSummary.setTotalAmount(total);
		
		theModel.addAttribute("customer", theCustomer);
		
		theModel.addAttribute("paymentDescriptions", paymentDescriptionRepository.findByEnable(true));
		
		theModel.addAttribute("paymentDetails", billingSummary.getPaymentDetails());
		
		theModel.addAttribute("billingSummary", billingSummary);

		return "billingsummary/customer-billing-form";
	}
	
	@GetMapping("/showFormForAddDetail")
	public String showFormForAddDetail(  @ModelAttribute("billingSummary") BillingSummary billingSummary, HttpServletRequest request, Model theModel) {
		

		
		  Double amount1 = Double.valueOf(request.getParameter("amount"));

		  Long paymentDescId = 0L;
		  try {
				paymentDescId = Long.parseLong(request.getParameter("paymentDescriptionId"));
		  }catch (Exception e) {
				// TODO: handle exception
		  }
			 
		
		 Optional<PaymentDescription> paymentDescription = paymentDescriptionRepository.findById(paymentDescId);
		 
		 PaymentDescription thePaymentDescription = paymentDescription.orElse(null);

		 PaymentDetail paymentDetail = new PaymentDetail( thePaymentDescription.getName(), amount1, request.getParameter("remarks"));
		 
		 paymentDetail.setBillingSummary(billingSummary);
		 
		 billingSummary.add(paymentDetail);
		 
		 theModel.addAttribute("billingSummary", billingSummary);

		return "redirect:/billingsummaries/"+Long.parseLong(request.getParameter("customerId"))+"/showFormForAdd";
	}
	
	
	
	@GetMapping("/showFormForAddClear")
	public String showFormForAddClear( HttpServletRequest request, BindingResult bindingResult, Model theModel) {

		if(bindingResult.hasErrors())
			return "redirect:/billingsummaries/"+Long.parseLong(request.getParameter("customerId"))+"/list";
		
		Optional<Customer> customer = customerRepository.findById(Long.parseLong(request.getParameter("customerId")));
		
		Customer theCustomer = customer.orElse(null);
		
		theModel.addAttribute("billingSummary", new BillingSummary(theCustomer));
		
		return "redirect:/billingsummaries/"+theCustomer.getId()+"/showFormForAdd";
	}
			
	
	
	@PostMapping("/save")
	public String savePayment(@Valid @ModelAttribute("billingSummary") BillingSummary theBillingSummary, BindingResult bindingResult, Model theModel) {
		
		if(bindingResult.hasErrors()) {	
			Double total = 0.0;
			for(PaymentDetail paymentDetail : theBillingSummary.getPaymentDetails()) 
				total += paymentDetail.getAmount();	
			theBillingSummary.setTotalAmount(total);
			
			theModel.addAttribute("customer", theBillingSummary.getCustomer());
			
			theModel.addAttribute("paymentDescriptions", paymentDescriptionRepository.findByEnable(true));
			
			theModel.addAttribute("paymentDetails", theBillingSummary.getPaymentDetails());		
			
			return "billingsummary/customer-billing-form";
		}
		
		BillingSummary billingSummary = billingSummaryRepository.findOneByIsNextDueDate(true);
		if(billingSummary != null) {
			billingSummary.setIsNextDueDate(false);
			billingSummaryRepository.save(billingSummary);
		}
		billingSummaryRepository.save(theBillingSummary);
		return "redirect:/billingsummaries/"+theBillingSummary.getCustomer().getId()+"/list";
	}
	

	/*
	 * @GetMapping("/delete") public String delete(@RequestParam("billingSummaryId")
	 * Long theId) { billingSummaryRepository.deleteById(theId);
	 * 
	 * return "redirect:/billingsummaries/"+theBillingSummary.getCustomer().getId()+
	 * "/list"; }
	 */
	

	
	@GetMapping("/{customerId}/pay")
	public String showBillingSummaries(@PathVariable Long customerId, @RequestParam("billingSummaryId") Long theId, Model model) {
		Optional<Customer> customer = customerRepository.findById(customerId);
		Customer theCustomer =  customer.orElse(null);
		Optional<BillingSummary> billingSummary =  billingSummaryRepository.findById(theId);
		
		if(billingSummary != null) {
			BillingSummary theBillingSummary = billingSummary.get();
			theBillingSummary.setIsPaid(true);
			billingSummaryRepository.save(theBillingSummary);
		}
		
		return "redirect:/billingsummaries/"+theCustomer.getId()+"/success";
	}
	
	@GetMapping("/{customerId}/success")
	public String showSuccess(@PathVariable Long customerId, Model model) {
		Optional<Customer> customer = customerRepository.findById(customerId);
		Customer theCustomer =  customer.orElse(null);
		model.addAttribute("customer", theCustomer);
		
		return "billingsummary/success";
	}
	
	
	 @ModelAttribute("billingSummary")
	 public BillingSummary getBillingSummary(@PathVariable Long customerId) {
		 Customer theCustomer  = customerRepository.findById(customerId)
				 .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));		
		 return new BillingSummary(theCustomer);
	 }

	 
	@GetMapping("/report/{customerId}/{format}")
	public String generateReport(@PathVariable String format, @PathVariable Long customerId) 
	    		throws FileNotFoundException, JRException {
	     return reportService.exportReport(format, customerId);
	}
		

}
