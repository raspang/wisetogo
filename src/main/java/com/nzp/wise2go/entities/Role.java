package com.nzp.wise2go.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;



@Entity
@Table(name="roles")
public class Role
{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NaturalId
	@Column(nullable=false, unique=true)
	private String name;
		

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}


	@Override
	public String toString() {
		String str = "";
		try {
			str = name.substring(5, name.length());
		}catch (Exception e) {
			// TODO: handle exception
		}
		return str;
	}
	
	
	
}
