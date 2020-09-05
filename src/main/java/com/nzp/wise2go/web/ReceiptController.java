package com.nzp.wise2go.web;

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
import com.nzp.wise2go.entities.BillingSummary;
import com.nzp.wise2go.repositories.BillingSummaryRepository;


@Controller
@RequestMapping("/receipts")
public class ReceiptController
{
	@Autowired
	private BillingSummaryRepository billingSummaryRepository;
	
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
	


}
