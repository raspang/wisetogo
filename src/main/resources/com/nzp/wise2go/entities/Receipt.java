package com.nzp.wise2go.entities;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import com.nzp.wise2go.entities.audit.UserDateAudit;
import com.nzp.wise2go.utils.DateUtils;

@Entity
@Table(name="receipt")
public class Receipt extends UserDateAudit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message="amount cannot be empty")
	@Column(name="total_amount")
	private Double totalAmount;
	
	@DateTimeFormat (pattern="yyyy-MM-dd")
	@Column(name="paid_date")
	private LocalDate datePaid;
	
	@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="receipt_id", nullable = true)
	private List<BillingSummary> billingSummaries;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Customer customer;
	
	@Transient
	private String datePaidStr;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LocalDate getDatePaid() {
		return datePaid;
	}

	public void setDatePaid(LocalDate datePaid) {
		this.datePaid = datePaid;
	}

	public List<BillingSummary> getBillingSummaries() {
		return billingSummaries;
	}

	public void setBillingSummaries(List<BillingSummary> billingSummaries) {
		this.billingSummaries = billingSummaries;
	}


	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getDatePaidStr() {
		return DateUtils.displayDate(datePaid);
	}

	public void setDatePaidStr(String datePaidStr) {
		this.datePaidStr = datePaidStr;
	}

	@Override
	public String toString() {
		return "Receipt [id=" + id + ", totalAmount=" + totalAmount + ", datePaid=" + datePaid + ", billingSummaries="
				+ billingSummaries + "]";
	}


    
    
	
}
