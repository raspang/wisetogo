package com.nzp.wise2go.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nzp.wise2go.entities.Expense;


public interface ExpenseRepository extends JpaRepository<Expense, Long>
{

	Page<Expense> findByEnableOrderByIdDesc(Boolean enable, Pageable pageable);
}
