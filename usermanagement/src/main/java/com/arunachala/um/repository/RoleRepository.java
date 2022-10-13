package com.arunachala.um.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arunachala.um.model.Role;

@Repository("oauthRoleRepository")
public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Role findByRoleName(String role);

}
