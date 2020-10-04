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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.nzp.wise2go.entities.audit.UserDateAudit;
import com.nzp.wise2go.utils.DateUtils;

@Entity
@Table(name="customer")
public class Customer extends UserDateAudit{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message="is required")
	@NotNull(message="is required")
	@Column(name="PPPOE_account", unique = true)
	private String pppoeAccount;
	
	@NotEmpty(message="is required")
	@NotNull(message="is required")
	@Size(max = 100)
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="middle_name")
	private String middleName;
	
	@NotEmpty(message="is required")
	@NotNull(message="is required")
	@Column(name="last_name")
	private String lastName;
	
	@NotEmpty(message="is required")
	@Column(name="address")
	private String address;
	
	@NotEmpty(message="is required")
	@Column(name="contact_no")
	private String contactNo;
	
	private String email;

	@Column(name="active")
	private Boolean active;
	
	@DateTimeFormat (pattern="yyyy-MM-dd")
	private LocalDate dateDeactivate;
	
	private Boolean enable;

	@NotNull(message="is required")
	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="plan_avail_id")
	private PlanAvail planAvail;
	
	
	@OneToMany(mappedBy="customer", cascade = CascadeType.ALL)
	private List<BillingSummary> billingSummaries;
	
	
	@Transient
	private String dueDateStr;
	
	
	@Transient
	@DateTimeFormat (pattern="yyyy-MM-dd")
	private LocalDate dueDate;
	
	@Transient
	private String dateDeactivateStr;
	
	@Transient
	private String fullName;
	
	public Customer() {	
		this.active = true;
		this.enable = true;
	}

	public Customer(String firstName, String middleName, String lastName, String address, String contactNo,
			String pppoeAccount) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.address = address;
		this.contactNo = contactNo;
		this.pppoeAccount = pppoeAccount;
		this.active = true;
		this.enable = true;	
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getPppoeAccount() {
		return pppoeAccount;
	}

	public void setPppoeAccount(String pppoeAccount) {
		this.pppoeAccount = pppoeAccount;
	}

	public PlanAvail getPlanAvail() {
		return planAvail;
	}

	public void setPlanAvail(PlanAvail planAvail) {
		this.planAvail = planAvail;
	}

	public Boolean getActive() {
		return active;
	}

	public List<BillingSummary> getBillingSummaries() {
		return billingSummaries;
	}

	public void setBillingSummaries(List<BillingSummary> billingSummaries) {
		this.billingSummaries = billingSummaries;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	

	public LocalDate getDateDeactivate() {
		return dateDeactivate;
	}

	public void setDateDeactivate(LocalDate dateDeactivate) {
		this.dateDeactivate = dateDeactivate;
	}

	
	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void add(BillingSummary billingSummary) {
		if(billingSummaries == null)
			billingSummaries = new ArrayList<>();
		billingSummaries.add(billingSummary);
		billingSummary.setCustomer(this);
	}

	
	public LocalDate getDueDate() {
		
		if (billingSummaries != null && billingSummaries.size() > 0) {
			BillingSummary billSummary = billingSummaries.get(billingSummaries.size()-1);
			return billSummary != null && !billSummary.getIsPaid() ? billSummary.getNextDueDate() : null;
		}
			
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public String getDueDateStr() {
		
		if (getDueDate() != null)
			return DateUtils.displayDate(getDueDate());
		
		return dueDateStr;
	}

	public void setDueDateStr(String dueDateStr) {
		this.dueDateStr = dueDateStr;
	}
	
	
	

	public String getDateDeactivateStr() {
		return DateUtils.displayDate(dateDeactivate);
	}

	public void setDateDeactivateStr(String dateDeactivateStr) {
		this.dateDeactivateStr = dateDeactivateStr;
	}
	
	

	public String getFullName() {
		return lastName+", "+firstName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName="
				+ lastName + ", address=" + address + ", contactNo=" + contactNo + ", pppoeAccount=" + pppoeAccount
				+ "]";
	}
	
	
	
}
