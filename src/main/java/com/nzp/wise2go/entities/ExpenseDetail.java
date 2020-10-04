package com.nzp.wise2go.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="expense_detail")
public class ExpenseDetail {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="expense_description")
	private String expenseDescription;

	@Column(name="remarks")
	private String remarks;

	@NotNull(message="is required")
	@Column(name="amount")
	private Double amount;

	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="expense_id")
	private Expense expense;

	public ExpenseDetail() {
	
	}

	
	public ExpenseDetail(String expenseDescription, String remarks, @NotNull(message = "is required") Double amount) {
	
		this.expenseDescription = expenseDescription;
		this.remarks = remarks;
		this.amount = amount;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getExpenseDescription() {
		return expenseDescription;
	}

	public void setExpenseDescription(String expenseDescription) {
		this.expenseDescription = expenseDescription;
	}

	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}


	@Override
	public String toString() {
		return "Description=" + expenseDescription + ", Remarks=" + remarks + ", Amount=" + amount;
	}

	
	
	
}
