package com.nzp.wise2go.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzp.wise2go.entities.PaymentDescription;


public interface PaymentDescriptionRepository extends JpaRepository<PaymentDescription, Long>
{

	List<PaymentDescription> findByEnable(Boolean enable);
}
