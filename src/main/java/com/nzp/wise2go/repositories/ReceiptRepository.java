package com.nzp.wise2go.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.entities.Receipt;


public interface ReceiptRepository extends JpaRepository<Receipt, Long>
{

	Page<Receipt> findByCustomerOrderByIdDesc(Customer customer, Pageable pageable);

}
