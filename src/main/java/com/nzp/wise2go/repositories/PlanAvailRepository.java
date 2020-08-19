package com.nzp.wise2go.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzp.wise2go.entities.PlanAvail;


public interface PlanAvailRepository extends JpaRepository<PlanAvail, Long>
{

	List<PlanAvail> findByEnable(Boolean enable);

}
