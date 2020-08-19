package com.nzp.wise2go.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzp.wise2go.entities.ExpenseDescription;


public interface ExpenseDescriptionRepository extends JpaRepository<ExpenseDescription, Long>
{
	
	List<ExpenseDescription> findByEnable(Boolean enable);

}
