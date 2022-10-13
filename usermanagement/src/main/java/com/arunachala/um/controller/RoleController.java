package com.arunachala.um.controller;

import java.util.List;

import com.arunachala.um.model.AlplRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arunachala.alplcommon.exception.AlplAppException;
import com.arunachala.um.response.RoleResponse;
import com.arunachala.um.response.UnitRoleDeptMapResponse;
import com.arunachala.um.service.RoleService;
import com.arunachala.um.service.UnitDepartmentRoleService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class RoleController {
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	UnitDepartmentRoleService unitDeptRoleService;

	/**
	 * Fetch all roles.
	 * 
	 * @return
	 */
	@GetMapping("/allRoles")
	public ResponseEntity<List<RoleResponse>> getAllRoles() throws AlplAppException {
		return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
	}

	@GetMapping("/getAllAlplRoles")
	public ResponseEntity<List<AlplRoles>> getAllAlplRoles() throws AlplAppException {
		return new ResponseEntity<>(roleService.getAllAlplRoles(), HttpStatus.OK);
	}
	
	@GetMapping("/allUnitRoleDeptMappings")
	public ResponseEntity<List<UnitRoleDeptMapResponse>> getAllUnitRoleDeptMappings() throws AlplAppException {
		return new ResponseEntity<>(unitDeptRoleService.getAllUnitRoleDeptMappings(), HttpStatus.OK);
	}

}
