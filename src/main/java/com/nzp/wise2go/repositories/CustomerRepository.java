package com.nzp.wise2go.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nzp.wise2go.entities.Customer;


public interface CustomerRepository extends JpaRepository<Customer, Long>
{

	 @Query("select c from Customer c where (c.firstName like %?1 or c.lastName like %?1 or c.pppoeAccount  like %?1) and c.enable = true")
	Page<Customer> findByLastNameOrFirstNameOrPppoeAccount(@Param("keyword") String keyword,  Pageable pageable);
	

}
