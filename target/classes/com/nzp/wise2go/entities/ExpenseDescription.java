package com.nzp.wise2go.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="expense_description")
public class ExpenseDescription {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message="is required")
	@NotNull(message="is required")
	@Column(name="name", unique=true)
	private String name;

	private Boolean enable;
	
	
	public ExpenseDescription() {
		this.enable = true;
	}

	public ExpenseDescription(String name) {
		this.name = name;
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


	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return "ExpenseDescription [id=" + id + ", name=" + name + "]";
	}
	
	
}
