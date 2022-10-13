package com.arunachala.um.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.arunachala.um.model.PasswordHistory;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long>  {
	
	Optional<PasswordHistory> findById(Long id);
	
	@Query("select p from PasswordHistory p where p.user.id = :userId")
	PasswordHistory findByUserId(Long userId);

}
