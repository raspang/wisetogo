package com.nzp.wise2go.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

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
	
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="receipt_id", nullable = true)
	private List<BillingSummary> billingSummaries;
	
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



	@Override
	public String toString() {
		return "Receipt [id=" + id + ", totalAmount=" + totalAmount + ", datePaid=" + datePaid + ", billingSummaries="
				+ billingSummaries + "]";
	}


    
    
	
}
