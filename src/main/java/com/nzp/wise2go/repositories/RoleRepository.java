package com.nzp.wise2go.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzp.wise2go.entities.Role;


public interface RoleRepository extends JpaRepository<Role, Long>{

	public Role findByName(String name);
}
