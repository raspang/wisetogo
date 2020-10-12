package com.nzp.wise2go.web;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nzp.wise2go.service.ReportService;

import net.sf.jasperreports.engine.JRException;


@Controller
@RequestMapping("/reports")
public class ReportController{
	

	@Autowired
	private ReportService reportService;
	

	
	private List<String> months = Arrays.asList("January", "February", "March",
			"April", "May", "June", "July", "August", "September", "October", "November", "December");
	
	@GetMapping("/list")
	public String showReports(Model model) {
		
		model.addAttribute("months", getListMonths());
	
		return "report/reports";
	}
	
	@GetMapping("/income-report")
	public String generateMonthlyIncomeReport(HttpServletResponse resp, @RequestParam("month") String month) throws JRException, IOException {
		int i;
		for(i = 1; i <= 12; i++) {
			if(months.get(i-1).equals(month)) {
				break;
			}
		}
	
		return reportService.exportMonthlyReport(resp, "pdf", i);
	}
	
	@GetMapping("/expense-report")
	public String generateMonthlyExpenseReport(HttpServletResponse resp, @RequestParam("month") String month) throws JRException, IOException {
		int i;
		for(i = 1; i <= 12; i++) {
			if(months.get(i-1).equals(month)) {
				break;
			}
		}
		return reportService.exportMonthlyExpense(resp, "pdf", i);
	
	}
	
	private List<String> getListMonths(){
		LocalDate myLocaldate = LocalDate.now();

		List<String> listOfMonths = new ArrayList<String>();
		
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		myLocaldate = myLocaldate.minusMonths(1);
		listOfMonths.add(months.get(myLocaldate.getMonthValue()-1));
		
		
		return listOfMonths;
	}

}
