package com.nzp.wise2go.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="billing_detail")
public class BillingDetail {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotEmpty(message = "is required") 
	@Column(name="payment_description")
	private String paymentDescription;
	
	private Double amount;
	
	private String remarks;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name="billing_summary_id")
	@OnDelete(action = OnDeleteAction.CASCADE) 
	private BillingSummary billingSummary;
	
	@Transient
	private String displayDetailStr;

	public BillingDetail() {
		
	}

	public BillingDetail(String paymentDescription, Double amount, String remarks,
			BillingSummary billingSummary) {
		this.paymentDescription = paymentDescription;
		this.amount = amount;
		this.remarks = remarks;
		this.billingSummary = billingSummary;
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

	public String getPaymentDescription() {
		return paymentDescription;
	}

	public void setPaymentDescription(String paymentDescription) {
		this.paymentDescription = paymentDescription;
	}

	public BillingSummary getBillingSummary() {
		return billingSummary;
	}

	public void setBillingSummary(BillingSummary billingSummary) {
		this.billingSummary = billingSummary;
	}

	public String getDisplayDetailStr() {
		return paymentDescription+"  "+remarks+"  "+String.valueOf(amount);
	}

	public void setDisplayDetailStr(String displayDetailStr) {
		this.displayDetailStr = displayDetailStr;
	}

	@Override
	public String toString() {
		return "BillingDetail [id=" + id + ", paymentDescription=" + paymentDescription + ", amount=" + amount
				+ ", remarks=" + remarks + ", billingSummary=" + billingSummary + "]";
	}




	


	
	
}
