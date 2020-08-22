package com.nzp.wise2go.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.entities.BillingSummary;


public interface BillingSummaryRepository extends JpaRepository<BillingSummary, Long>
{

	Page<BillingSummary> findByCustomerAndIsPaidOrderByIdDesc(Customer customer, Boolean isPaid, Pageable pageable);
	
	List<BillingSummary> findByCustomerAndIsPaidOrderByIdDesc(Customer customer, Boolean isPaid );
	
	BillingSummary findOneByIsNextDueDate(Boolean isNextDueDate);

}
