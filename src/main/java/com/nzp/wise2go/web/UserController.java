package com.nzp.wise2go.web;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nzp.wise2go.entities.Role;
import com.nzp.wise2go.entities.User;
import com.nzp.wise2go.repositories.RoleRepository;
import com.nzp.wise2go.repositories.UserRepository;


@Controller
@RequestMapping("/users")
public class UserController{

	
	@Autowired
	private UserRepository userRepository;

	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepository;
	
	
	@GetMapping("/list")
	public String showUsers(Model model) {
		model.addAttribute("users", userRepository.findAll());	
		return "user/users";
	}
	
	@GetMapping("/showFormForAdd")
	public String showFormForAdd(Model theModel) {
		User theUser = new User();
		theModel.addAttribute("user", theUser);
		return "user/user-form";
	}
	
	@GetMapping("/showFormForUpdate")
	public String showFormForUpdate(@RequestParam("userId") Long theId,
									Model theModel) {
		Optional<User> user = userRepository.findById(theId);
		User theUser = user.orElse(null);
	
		theModel.addAttribute("user", theUser);
		return "user/user-form";	
	}
	
	@PostMapping("/save")
	public String saveThe(@Valid @ModelAttribute("user") User theUser, BindingResult bindingResult) {
		String success = "created";
		if(theUser.getId() != null) {
			success = "updated";
		}
		if(bindingResult.hasErrors()) {
			return "user/user-form";	
		}
		
		theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
		userRepository.save(theUser);
		return "redirect:/users/list?success="+success;
	}
	
	@GetMapping("/delete")
	public String delete(@RequestParam("userId") Long theId) {
		userRepository.deleteById(theId);	
		return "redirect:/users/list";
	}
	
	@ModelAttribute("roles")
	public List<Role> getRoles(){
		return roleRepository.findAll();
	}
	

}