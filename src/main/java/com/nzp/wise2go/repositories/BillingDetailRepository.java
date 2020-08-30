package com.nzp.wise2go.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzp.wise2go.entities.BillingDetail;
import com.nzp.wise2go.entities.BillingSummary;


public interface BillingDetailRepository extends JpaRepository<BillingDetail, Long>
{
	List<BillingDetail> findByBillingSummaryOrderByIdDesc(BillingSummary billingSummary);
}
