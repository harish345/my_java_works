package com.arunachala.um.service;

import java.util.List;

import com.arunachala.alplcommon.exception.AlplAppException;
import com.arunachala.um.model.AlplRoles;
import com.arunachala.um.response.RoleResponse;

public interface RoleService {
	
	List<RoleResponse> getAllRoles() throws AlplAppException;

	List<AlplRoles> getAllAlplRoles() throws AlplAppException;

}
