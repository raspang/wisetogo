package com.nzp.wise2go.entities;

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

@Entity
@Table(name="plan_avail")
public class PlanAvail {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="name")
	private String name;
	
	@NotNull(message="is required")
	@NotEmpty(message="is required")
	@Column(name="type")
	private String type;
	
	@NotNull(message="is required")
	@NotEmpty(message="is required")
	@Column(name="max_mbps")
	private String maxMbps;
	
	@NotNull(message="is required")
	@Column(name="price")
	private Double price;
	
	@OneToMany(mappedBy="planAvail", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	private List<Customer> customers;
	
	private Boolean enable;
	
	@Transient
	private String planStr;
	
	public PlanAvail() {
		this.enable = true;
	}

	public PlanAvail(String name, String type, String maxMbps, Double price) {
		this.name = name;
		this.type = type;
		this.maxMbps = maxMbps;
		this.price = price;
		this.enable = true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMaxMbps() {
		return maxMbps;
	}

	public void setMaxMbps(String maxMbps) {
		this.maxMbps = maxMbps;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}


	public String getPlanStr() {
		String str = type.concat(" - ").concat(name).concat(" ").concat("[").concat(maxMbps).concat("]").concat(" - ").concat(String.valueOf(price));
		return str;
	}

	public void setPlanStr(String planStr) {
		this.planStr = planStr;
	}

	
	
	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return "PlanAvail [id=" + id + ", name=" + name + ", type=" + type + ", maxMbps=" + maxMbps + ", price=" + price
				+ "]";
	}

	
	
	
}
