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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="payment_detail")
public class PaymentDetail {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotNull(message="is required")
	@Column(name="payment_description")
	private String paymentDescription;
	
	private Double amount;
	
	private String remarks;
	
	@NotNull(message="is required")
	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="billing_summary_id")
	private BillingSummary billingSummary;
	
	@Transient
	private String displayDetailStr;

	public PaymentDetail() {
		
	}
	
	public PaymentDetail(String paymentDescription, Double amount, String remarks) {
		this.paymentDescription = paymentDescription;
		this.amount = amount;
		this.remarks = remarks;
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
		return "PaymentDetail [id=" + id + ", paymentDescription=" + paymentDescription + ", amount=" + amount
				+ ", remarks=" + remarks + ", displayDetailStr=" + displayDetailStr + "]";
	}



	


	
	
}
