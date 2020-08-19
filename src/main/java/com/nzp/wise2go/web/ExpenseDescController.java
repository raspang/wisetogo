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

import com.nzp.wise2go.entities.ExpenseDescription;
import com.nzp.wise2go.repositories.ExpenseDescriptionRepository;


@Controller
@RequestMapping("/expensedescriptions")
public class ExpenseDescController
{

	@Autowired
	private ExpenseDescriptionRepository expenseDescriptionRepository;
	
	
	@GetMapping("/list")
	public String showPayments(Model model) {
		model.addAttribute("expenseDescriptions", expenseDescriptionRepository.findByEnable(true));	
		return "expensedescription/expensedescriptions";
	}
	
	@GetMapping("/showFormForAdd")
	public String showFormForAdd(Model theModel) {
		ExpenseDescription theExpenseDescription = new ExpenseDescription();
		theModel.addAttribute("expenseDescription", theExpenseDescription);
		return "expensedescription/expensedescription-form";
	}
	
	@GetMapping("/showFormForUpdate")
	public String showFormForUpdate(@RequestParam("expenseDescriptionId") Long theId,
									Model theModel) {
		Optional<ExpenseDescription> theExpenseDescription = expenseDescriptionRepository.findById(theId);		
		theModel.addAttribute("expenseDescription", theExpenseDescription);
		return "expensedescription/expensedescription-form";	
	}
	
	@PostMapping("/save")
	public String saveExpenseDescription(@Valid  @ModelAttribute("expenseDescription") ExpenseDescription theExpenseDescription, BindingResult bindingResult) {
		if(bindingResult.hasErrors())
			return "expensedescription/expensedescription-form";
		expenseDescriptionRepository.save(theExpenseDescription);
		return "redirect:/expensedescriptions/list";
	}
	
	@GetMapping("/delete")
	public String delete(@RequestParam("expenseDescriptionId") Long theId) {
		Optional<ExpenseDescription> temp = expenseDescriptionRepository.findById(theId);
		ExpenseDescription theExpenseDescription = temp.orElse(null);
		if(theExpenseDescription != null) theExpenseDescription.setEnable(false); 
		expenseDescriptionRepository.save(theExpenseDescription);	
		return "redirect:/expensedescriptions/list";
	}

}
