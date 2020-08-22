package com.nzp.wise2go.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.nzp.wise2go.entities.BillingSummary;
import com.nzp.wise2go.exception.ResourceNotFoundException;
import com.nzp.wise2go.repositories.BillingSummaryRepository;


@Controller
@RequestMapping("/receipts")
public class ReceiptController
{


	@Autowired
	private BillingSummaryRepository billingSummaryRepository;
	

	@GetMapping("/{billingSummaryId}/list")
	public String showReceipts(@PathVariable Long billingSummaryId, HttpServletRequest request, Model model) {
	
		BillingSummary billingSummary = billingSummaryRepository.findById(billingSummaryId).orElseThrow(
				() -> new ResourceNotFoundException("BillingSummary", "id", billingSummaryId));

        int page = 0; 
        int size = 10; 
        
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        

		
		//model.addAttribute("receipts", billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(theCustomer, isPaid, PageRequest.of(page, size)));

		return "billingsummary/customer-billings";
	}
	
	
	
	
	@GetMapping("/{customerId}/showFormForAdd")
	public String showFormForAdd(@PathVariable Long customerId,
			@ModelAttribute("billingSummary") BillingSummary billingSummary,		
			Model theModel) {


		return "billingsummary/customer-billing-form";
	}
	
	
	
	@PostMapping("/save")
	public String savePayment(@Valid @ModelAttribute("billingSummary") BillingSummary theBillingSummary, BindingResult bindingResult, Model theModel) {
		
		if(bindingResult.hasErrors()) {	

			
			return "billingsummary/customer-billing-form";
		}
		

		return "redirect:/billingsummaries/"+theBillingSummary.getCustomer().getId()+"/list";
	}
	

	@GetMapping("/delete")
	public String delete(@RequestParam("billingSummaryId") Long theId) {
		billingSummaryRepository.deleteById(theId);	
		return "redirect:/billingsummaries/list";
	}
	



}
