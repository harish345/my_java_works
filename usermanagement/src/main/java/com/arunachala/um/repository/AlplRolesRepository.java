package com.arunachala.um.repository;

import com.arunachala.um.model.AlplRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlplRolesRepository extends JpaRepository<AlplRoles, Long> {



}
