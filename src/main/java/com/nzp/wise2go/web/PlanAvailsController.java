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

import com.nzp.wise2go.entities.PlanAvail;
import com.nzp.wise2go.repositories.PlanAvailRepository;


@Controller
@RequestMapping("/planavails")
public class PlanAvailsController
{

	@Autowired
	private PlanAvailRepository planAvailRepository;
	
	@GetMapping("/list")
	public String showPlanAvails(Model model) {
		model.addAttribute("planAvails", planAvailRepository.findByEnable(true));	
		return "planavail/planavails";
	}
	
	@GetMapping("/showFormForAdd")
	public String showFormForAdd(Model theModel) {
		PlanAvail thePlanAvail = new PlanAvail();
		theModel.addAttribute("planAvail", thePlanAvail);
		return "planavail/planavail-form";
	}
	
	@GetMapping("/showFormForUpdate")
	public String showFormForUpdate(@RequestParam("planAvailId") Long theId,
									Model theModel) {
		Optional<PlanAvail> thePlanAvail = planAvailRepository.findById(theId);		
		theModel.addAttribute("planAvail", thePlanAvail);
		return "planavail/planavail-form";	
	}
	
	@PostMapping("/save")
	public String saveThe(@Valid @ModelAttribute("planAvail") PlanAvail thePlanAvail, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors())
			return "planavail/planavail-form";	
		
		planAvailRepository.save(thePlanAvail);
		return "redirect:/planavails/list";
	}
	
	@GetMapping("/delete")
	public String delete(@RequestParam("planAvailId") Long theId) {
		Optional<PlanAvail> temp = planAvailRepository.findById(theId);
		PlanAvail thePlanAvail = temp.orElse(null);
		if(thePlanAvail != null) thePlanAvail.setEnable(false); 
		planAvailRepository.save(thePlanAvail);		
		return "redirect:/planavails/list";
	}
	

}
