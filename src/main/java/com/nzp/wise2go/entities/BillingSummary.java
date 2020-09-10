package com.nzp.wise2go.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

import org.springframework.format.annotation.DateTimeFormat;

import com.nzp.wise2go.entities.audit.UserDateAudit;
import com.nzp.wise2go.utils.DateUtils;

@Entity
@Table(name="billing_summary")
public class BillingSummary extends UserDateAudit {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@NotNull(message="is required")
	@DateTimeFormat (pattern="yyyy-MM-dd")
	@Column(name="date")
	private LocalDate date;

	@NotNull(message="is required")
	@Column(name="total_amount")
	private Double totalAmount;

	@NotNull(message="is required")
	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="customer_id")
	private Customer customer;
	
	/*
	 * @NotEmpty(message="is required")
	 * 
	 * @OneToMany(mappedBy="billingSummary", cascade = {CascadeType.DETACH,
	 * CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
	 * fetch=FetchType.LAZY) private List<BillingDetail> paymentDetails = new
	 * ArrayList<>();
	 */
	
	@NotNull(message="is required")
	@DateTimeFormat (pattern="yyyy-MM-dd")
	@Column(name="next_due_date")
	private LocalDate nextDueDate;
	
	@Column(name="is_next_due_date")
	private Boolean isNextDueDate;
	
	@Column(name="is_paid")
	private Boolean isPaid;

	@Transient
	private String dateStr;
	
	@Transient
	private String nextDueDateStr;
	

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,  mappedBy="billingSummary")
	private Set<BillingDetail> billingDetails = new HashSet<>(); 
	
	public BillingSummary() {
		this.date = LocalDate.now();
		this.nextDueDate = LocalDate.now().plusMonths(1);
		this.isNextDueDate = true;
		this.totalAmount = 0.0;
		this.isPaid =false;
	}
	
	public BillingSummary(Customer customer) {
		this.customer = customer;
		this.date = LocalDate.now();
		this.nextDueDate = LocalDate.now().plusMonths(1);
		this.isNextDueDate = true;
		this.totalAmount = 0.0;
		this.isPaid = false;
	}
	
	public BillingSummary(LocalDate date, Double totalAmount, Customer customer, User issuedBy, LocalDate nextDueDate) {
		this.date = date;
		this.totalAmount = totalAmount;
		this.customer = customer;
		this.nextDueDate = nextDueDate; 
		this.isNextDueDate = true;
		this.isPaid = false;
	}

	public Long getId() {
		return id;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	/*
	 * public List<BillingDetail> getPaymentDetails() { return paymentDetails; }
	 * public void setPaymentDetails(List<BillingDetail> paymentDetails) {
	 * this.paymentDetails = paymentDetails; }
	 */
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalDate getNextDueDate() {
		return nextDueDate;
	}
	public void setNextDueDate(LocalDate nextDueDate) {
		this.nextDueDate = nextDueDate;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDateStr() {
		return DateUtils.displayDate(date);
	}
	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	public String getNextDueDateStr() {
		return DateUtils.displayDate(nextDueDate);
	}
	public void setNextDueDateStr(String nextDueDateStr) {
		this.nextDueDateStr = nextDueDateStr;
	}
	public Boolean getIsNextDueDate() {
		return isNextDueDate;
	}
	public void setIsNextDueDate(Boolean isNextDueDate) {
		this.isNextDueDate = isNextDueDate;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}
	
	public Set<BillingDetail> getBillingDetails() {
		return billingDetails;
	}

	public void setBillingDetails(Set<BillingDetail> billingDetails) {
		this.billingDetails = billingDetails;
	}

	@Override
	public String toString() {
		return "BillingSummary [id=" + id + ", date=" + date + ", totalAmount=" + totalAmount + ", customer=" + customer
				+ ", nextDueDate=" + nextDueDate + ", isNextDueDate=" + isNextDueDate + ", isPaid=" + isPaid
				+ ", dateStr=" + dateStr + ", nextDueDateStr=" + nextDueDateStr + "]";
	}
	

	/*
	 * public void add(BillingDetail paymentDetail) {
	 * paymentDetails.add(paymentDetail); paymentDetail.setBillingSummary(this); }
	 */


	
	
}
