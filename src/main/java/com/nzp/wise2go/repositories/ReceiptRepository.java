package com.nzp.wise2go.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nzp.wise2go.entities.Receipt;
import com.nzp.wise2go.entities.BillingSummary;


public interface ReceiptRepository extends JpaRepository<Receipt, Long>
{

	Page<Receipt> findByBillingSummaryOrderByIdDesc(BillingSummary billingSummary,  Pageable pageable);

}
