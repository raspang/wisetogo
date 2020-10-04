package com.nzp.wise2go.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nzp.wise2go.entities.BillingDetail;
import com.nzp.wise2go.entities.BillingSummary;
import com.nzp.wise2go.entities.Expense;
import com.nzp.wise2go.entities.MonthReport;
import com.nzp.wise2go.entities.Receipt;
import com.nzp.wise2go.repositories.ExpenseRepository;
import com.nzp.wise2go.repositories.ReceiptRepository;

@Service
public class MonthlyReportService {

	
	@Autowired
	private ReceiptRepository receiptRepository;
	
	@Autowired
	private ExpenseRepository expenseRepository;
	

	public List<MonthReport> getIncomeReport(Integer month){
		List<MonthReport> listOfMonthlyReport = new ArrayList<>();
		MonthReport monthReport;
		for(Receipt receipt: getIncome(month)) {
			monthReport = new MonthReport();
			monthReport.setDate(receipt.getDatePaidStr());
			monthReport.setCustomer(receipt.getCustomer().getFullName());
			monthReport.setTotalAmount(String.format("%.2f",receipt.getTotalAmount()));
			String description = "";
			for(BillingSummary billingSummary: receipt.getBillingSummaries()) {
				for(BillingDetail billingDetail : billingSummary.getBillingDetails()) {
					description+=billingDetail.getPaymentDescription()+", ";
				}
			}
			monthReport.setDescription(description);
			listOfMonthlyReport.add(monthReport);
			
		}
		return listOfMonthlyReport;
	}
	

	
	public List<Expense> getExpense(Integer month){
		LocalDate date = LocalDate.now();
		date = date.minusMonths(getPositionListMonth(month));
		return expenseRepository.getByYearAndMonth(date.getYear(), date.getMonthValue());
	}
	
	public List<Receipt> getIncome(Integer month){
		LocalDate date = LocalDate.now();
		date = date.minusMonths(getPositionListMonth(month));
		return receiptRepository.getByYearAndMonth(date.getYear(), date.getMonthValue());
	}
	
	private Integer getPositionListMonth(Integer month){
		LocalDate myLocaldate = LocalDate.now();

		if(myLocaldate.getMonthValue() == month)
			return 0;

		myLocaldate = myLocaldate.minusMonths(1);
		if(myLocaldate.getMonthValue() == month)
			return 1;

		myLocaldate = myLocaldate.minusMonths(2);
		if(myLocaldate.getMonthValue() == month)
			return 2;
		
		myLocaldate = myLocaldate.minusMonths(3);
		if(myLocaldate.getMonthValue() == month)
			return 3;
		
		myLocaldate = myLocaldate.minusMonths(4);
		if(myLocaldate.getMonthValue() == month)
			return 4;
		
		myLocaldate = myLocaldate.minusMonths(5);
		if(myLocaldate.getMonthValue() == month)
			return 5;
		
		myLocaldate = myLocaldate.minusMonths(6);
		if(myLocaldate.getMonthValue() == month)
			return 6;
		
		myLocaldate = myLocaldate.minusMonths(7);
		if(myLocaldate.getMonthValue() == month)
			return 7;
		
		myLocaldate = myLocaldate.minusMonths(8);
		if(myLocaldate.getMonthValue() == month)
			return 8;
		
		myLocaldate = myLocaldate.minusMonths(9);
		if(myLocaldate.getMonthValue() == month)
			return 9;
		
		myLocaldate = myLocaldate.minusMonths(10);
		if(myLocaldate.getMonthValue() == month)
			return 10;
		
		myLocaldate = myLocaldate.minusMonths(11);
		if(myLocaldate.getMonthValue() == month)
			return 11;
		
		return -1;
	}
	

	public Double computeTotalAmountReceipt(List<Receipt> receipts) {
		Double totalAmount = 0d;
		
		for(Receipt receipt : receipts) {
			totalAmount += receipt.getTotalAmount();
		}
	
		return totalAmount;
	}

	public Double computeTotalAmountExpense(List<Expense> expenses) {
		Double totalAmount = 0d;
		
		for(Expense expense : expenses) {
			totalAmount -= expense.getTotalAmount();
		}
		return totalAmount;
	}
	
}
