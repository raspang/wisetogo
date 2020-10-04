package com.nzp.wise2go.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.entities.Receipt;


public interface ReceiptRepository extends JpaRepository<Receipt, Long>
{

	Page<Receipt> findByCustomerOrderByIdDesc(Customer customer, Pageable pageable);

	List<Receipt> findByDatePaid(LocalDate datePaid);
	
	@Query("select r from Receipt r where year(r.datePaid) = ?1 and month(r.datePaid) = ?2")
	List<Receipt> getByYearAndMonth(int year, int month);
}
