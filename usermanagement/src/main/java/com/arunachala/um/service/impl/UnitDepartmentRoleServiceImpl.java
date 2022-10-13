package com.arunachala.um.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arunachala.um.projection.UnitRoleDeptDto;
import com.arunachala.um.repository.UnitDepartmentRoleRepo;
import com.arunachala.um.response.UnitRoleDeptMapResponse;
import com.arunachala.um.service.UnitDepartmentRoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UnitDepartmentRoleServiceImpl implements UnitDepartmentRoleService{
	
	@Autowired
	private UnitDepartmentRoleRepo repo;

	@Override
	public List<UnitRoleDeptMapResponse> getAllUnitRoleDeptMappings() {
		List<UnitRoleDeptDto> objects = new ArrayList<>();
		try {
			objects = repo.getAllUnitDeptRoleMappings();
			return buildUnitDeptRoleResponse(objects);
		}catch(Exception e) {
			log.error("Error getting unit role dept mapping data: "+e.getMessage());
		}
		return new ArrayList<>();
	}

	private List<UnitRoleDeptMapResponse> buildUnitDeptRoleResponse(List<UnitRoleDeptDto> objects) {
		List<UnitRoleDeptMapResponse> response = new ArrayList<>();
		if(!objects.isEmpty()) {
			for(UnitRoleDeptDto o : objects) {
				response.add(new UnitRoleDeptMapResponse(o.getId(),o.getUnit(),o.getDept(),o.getRole()));
			}
		}
		return response;

	}

}
