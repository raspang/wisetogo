package com.nzp.wise2go.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nzp.wise2go.entities.Expense;


public interface ExpenseRepository extends JpaRepository<Expense, Long>
{

	Page<Expense> findByEnableOrderByDateDesc(Boolean enable, Pageable pageable);
	
	@Query("select e from Expense e where year(e.date) = ?1 and month(e.date) = ?2")
	List<Expense> getByYearAndMonth(int year, int month);
}
