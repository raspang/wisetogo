package com.nzp.wise2go.web;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.nzp.wise2go.entities.Expense;
import com.nzp.wise2go.entities.ExpenseDescription;
import com.nzp.wise2go.entities.ExpenseDetail;
import com.nzp.wise2go.repositories.ExpenseDescriptionRepository;
import com.nzp.wise2go.repositories.ExpenseRepository;


@Controller
@RequestMapping("/expenses")
@SessionAttributes("expense")
public class ExpenseController
{

	@Autowired
	private ExpenseDescriptionRepository expenseDescriptionRepository;

	@Autowired
	private ExpenseRepository expenseRepository;

	
	@GetMapping("/list")
	public String showPayments(HttpServletRequest request, Model model) {		
        int page = 0; 
        int size = 10; 
        
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
       
        
		model.addAttribute("expenses", expenseRepository.findByEnableOrderByDateDesc(true, PageRequest.of(page, size)));	
		return "expense/expenses";
	}
	
	@GetMapping("/showFormForAdd")
	public String showFormForAdd(@ModelAttribute("expense") Expense theExpense, 
			Model theModel) {
		
		Double total = 0.0;
		for(ExpenseDetail expenseDetail : theExpense.getExpenseDetails())
			total += expenseDetail.getAmount();		
		theExpense.setTotalAmount(total);
		
		theModel.addAttribute("expenseDescriptions", expenseDescriptionRepository.findByEnable(true));
		theModel.addAttribute("expenseDetails", theExpense.getExpenseDetails());
		theModel.addAttribute("expense", theExpense);
		return "expense/expense-form";
	}
	

	@GetMapping("/showFormForAddDetail")
	public String showFormForAddDetail( @ModelAttribute("expense") Expense theExpense, HttpServletRequest request, Model theModel) {
		  Double amount1 = Double.valueOf(request.getParameter("amount"));
		  Long expenseDescId = 0L;
		  try {
			  expenseDescId = Long.parseLong(request.getParameter("expenseDescriptionId"));
		  }catch (Exception e) {
				// TODO: handle exception
		  }
		  Optional<ExpenseDescription> expenseDescription = expenseDescriptionRepository.findById(expenseDescId);
		  
		  ExpenseDescription theExpenseDescription =  expenseDescription.orElse(new ExpenseDescription());
		  
		  ExpenseDetail expenseDetail = new ExpenseDetail(theExpenseDescription.getName(), request.getParameter("remarks"), amount1 );
		 
		  theExpense.add(expenseDetail);
		  
		  theModel.addAttribute("expense", theExpense);
		  
		return "redirect:/expenses/showFormForAdd";
	}
	
	@GetMapping("/showFormForAddClear")
	public String showFormForAddClear( HttpServletRequest request, Model theModel) {	
		theModel.addAttribute("expense", new Expense());
		return "redirect:/expenses/showFormForAdd";
	}
	
	@PostMapping("/save")
	public String saveExpense(@Valid @ModelAttribute("expense") Expense theExpense, BindingResult bindingResult, Model theModel) {
		String success = "created";
		if(theExpense.getId() != null) {
			success = "updated";
		}	
		if(bindingResult.hasErrors()) {
			Double total = 0.0;
			for(ExpenseDetail expenseDetail : theExpense.getExpenseDetails())
				total += expenseDetail.getAmount();
			theExpense.setTotalAmount(total);
			
			theModel.addAttribute("expenseDescriptions", expenseDescriptionRepository.findByEnable(true));
			theModel.addAttribute("expenseDetails", theExpense.getExpenseDetails());			
			return "expense/expense-form";
		}
		
		expenseRepository.save(theExpense);
		theModel.addAttribute("expense", new Expense());

		return "redirect:/expenses/list?success="+success;
	}
	
	@GetMapping("/delete")
	public String delete(@RequestParam("expenseId") Long theId) {
		expenseRepository.deleteById(theId);	
		return "redirect:/expenses/list";
	}
	
	@ModelAttribute("expense")
	public Expense getExpense() {
		return new Expense();	
	}

}
