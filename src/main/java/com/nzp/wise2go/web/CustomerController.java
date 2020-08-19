package com.nzp.wise2go.web;

import java.time.LocalDate;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.repositories.CustomerRepository;
import com.nzp.wise2go.repositories.PlanAvailRepository;

@Controller
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PlanAvailRepository planAvailRepository;


	@GetMapping("/list")
	public String showCustomers(HttpServletRequest request, Model model) {
		String keyword = "";
        int page = 0; //default page number is 0 (yes it is weird)
        int size = 10; //default page size is 10
        
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
		
        
        if (request.getParameter("keyword") != null && !request.getParameter("keyword").isEmpty()) {
        	keyword = request.getParameter("keyword");
        }

		model.addAttribute("customers", 
				customerRepository.findByLastNameOrFirstNameOrPppoeAccount(
						keyword, PageRequest.of(page, size)));
		
		model.addAttribute("keyword", keyword);

		return "customer/customers";
	}

	@GetMapping("/showFormForAdd")
	public String showFormForAdd(Model theModel) {
		Customer theCustomer = new Customer();
		theModel.addAttribute("customer", theCustomer);
		theModel.addAttribute("planAvails", planAvailRepository.findByEnable(true));
		return "customer/customer-form";
	}

	@GetMapping("/showFormForUpdate")
	public String showFormForUpdate(@RequestParam("customerId") Long theId, Model theModel) {
		Optional<Customer> theCustomer = customerRepository.findById(theId);
		theModel.addAttribute("customer", theCustomer);
		theModel.addAttribute("planAvails", planAvailRepository.findByEnable(true));
		return "customer/customer-form";
	}

	@PostMapping("/save")
	public String saveCustomer(@Valid  @ModelAttribute("customer") Customer theCustomer, 
			BindingResult bindingResult, Model theModel) {
		
		if(bindingResult.hasErrors()) {
			theModel.addAttribute("planAvails", planAvailRepository.findByEnable(true));
			return "customer/customer-form";
		}

		if (theCustomer.getId() != null) {
			Optional<Customer> customer = customerRepository.findById(theCustomer.getId());
			Customer customerTemp = customer.orElse(null);
			if (customerTemp != null && customerTemp.getActive() && !theCustomer.getActive()) {
				theCustomer.setDateDeactivate(LocalDate.now());
			}
		}

		customerRepository.save(theCustomer);
		return "redirect:/customers/list";
	}

	@GetMapping("/delete")
	public String delete(@RequestParam("customerId") Long theId) {
		Optional<Customer> temp = customerRepository.findById(theId);
		Customer theCustomer = temp.orElse(null);
		if(theCustomer != null) theCustomer.setEnable(false);
		customerRepository.save(theCustomer);
		return "redirect:/customers/list";
	}

	public String getPrincipal() {
		String username;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}
		return username;
	}

}
