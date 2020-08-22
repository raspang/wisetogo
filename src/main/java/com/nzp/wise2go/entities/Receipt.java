package com.nzp.wise2go.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nzp.wise2go.entities.audit.UserDateAudit;

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
	
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billingSummary_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
	private BillingSummary billingSummary;

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

	public BillingSummary getBillingSummary() {
		return billingSummary;
	}

	public void setBillingSummary(BillingSummary billingSummary) {
		this.billingSummary = billingSummary;
	}

	@Override
	public String toString() {
		return "Receipt [id=" + id + ", totalAmount=" + totalAmount + ", datePaid=" + datePaid + ", billingSummary="
				+ billingSummary + "]";
	}
    
    
    
	
}
