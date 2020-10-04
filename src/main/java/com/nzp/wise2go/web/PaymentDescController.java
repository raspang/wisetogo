package com.nzp.wise2go.web;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nzp.wise2go.entities.PaymentDescription;
import com.nzp.wise2go.repositories.PaymentDescriptionRepository;


@Controller
@RequestMapping("/paymentdescriptions")
public class PaymentDescController
{

	@Autowired
	private PaymentDescriptionRepository paymentDescriptionRepository;
	

	@GetMapping("/list")
	public String showPaymentDescriptions(Model model) {
		model.addAttribute("paymentDescriptions", paymentDescriptionRepository.findByEnable(true));	
		return "paymentdescription/paymentdescriptions";
	}
	
	@GetMapping("/showFormForAdd")
	public String showFormForAdd(Model theModel) {
		PaymentDescription thePaymentDescription = new PaymentDescription();
		theModel.addAttribute("paymentDescription", thePaymentDescription);
		return "paymentdescription/paymentdescription-form";
	}
	
	@GetMapping("/showFormForUpdate")
	public String showFormForUpdate(@RequestParam("paymentDescriptionId") Long theId, Model theModel) {
		Optional<PaymentDescription> thePaymentDescription = paymentDescriptionRepository.findById(theId);		
		theModel.addAttribute("paymentDescription", thePaymentDescription);
		return "paymentdescription/paymentdescription-form";	
	}
	
	@PostMapping("/save")
	public String savePaymentDescription(@Valid @ModelAttribute("paymentDescription") PaymentDescription thePaymentDescription, BindingResult bindingResult) {
		String success = "created";
		if(thePaymentDescription.getId() != null) {
			success = "updated";
		}
		if(bindingResult.hasErrors())
			return "paymentdescription/paymentdescription-form";
		
		paymentDescriptionRepository.save(thePaymentDescription);
		return "redirect:/paymentdescriptions/list?success="+success;
	}
	
	@GetMapping("/delete")
	public String delete(@RequestParam("paymentDescriptionId") Long theId) {
		Optional<PaymentDescription> temp = paymentDescriptionRepository.findById(theId);
		PaymentDescription thePaymentDescription= temp.orElse(null);
		if(thePaymentDescription != null) thePaymentDescription.setEnable(false); 
		paymentDescriptionRepository.save(thePaymentDescription);	
		return "redirect:/paymentdescriptions/list";
	}

}
