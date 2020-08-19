package com.nzp.wise2go.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzp.wise2go.entities.User;



public interface UserRepository extends JpaRepository<User, Long>
{

	//Optional<User> findByEmail(String email);
	
	Optional<User> findByUsername(String username);
	
}
