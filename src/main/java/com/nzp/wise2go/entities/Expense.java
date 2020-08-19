package com.nzp.wise2go.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.nzp.wise2go.entities.audit.UserDateAudit;
import com.nzp.wise2go.utils.DateUtils;

@Entity
@Table(name="expense")
public class Expense extends UserDateAudit{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5584995971014382996L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message="is required")
	@DateTimeFormat (pattern="yyyy-MM-dd")
	@Column(name="expense_date")
	private LocalDate date;
	
	@NotNull(message="is required")
	@Column(name="total_amount")
	private Double totalAmount;
	
	private Boolean enable;
	
	@NotEmpty(message="is required")
	@OneToMany(mappedBy="expense", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	private List<ExpenseDetail> expenseDetails  = new ArrayList<>();;
	
	
	@Transient
	private String dateStr;
	
	
	public Expense() {
		this.date = LocalDate.now();
		this.enable = true;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}


	public List<ExpenseDetail> getExpenseDetails() {
		return expenseDetails;
	}

	public void setExpenseDetails(List<ExpenseDetail> expenseDetails) {
		this.expenseDetails = expenseDetails;
	}
	
	public void add(ExpenseDetail expenseDetail) {
		expenseDetails.add(expenseDetail);
		expenseDetail.setExpense(this);
	}
	
	public String getDateStr() {
		return DateUtils.displayDate(date);
	}


	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}


	public Boolean getEnable() {
		return enable;
	}



	public void setEnable(Boolean enable) {
		this.enable = enable;
	}


	@Override
	public String toString() {
		return "Expense [id=" + id + ", date=" + date + ", totalAmount=" + totalAmount + ", expenseDetails="
				+ expenseDetails + "]";
	}
	
	/*
	 * @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE,
	 * CascadeType.PERSIST, CascadeType.REFRESH})
	 * 
	 * @JoinColumn(name="issuedBy_id") private User issuedBy;
	 */
	
	
	
}
