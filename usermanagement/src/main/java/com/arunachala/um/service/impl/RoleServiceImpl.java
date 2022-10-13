package com.arunachala.um.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.arunachala.um.model.AlplRoles;
import com.arunachala.um.repository.AlplRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arunachala.alplcommon.exception.AlplAppException;
import com.arunachala.alplcommon.utils.AppConstants;
import com.arunachala.alplcommon.utils.AppErrorCodes;
import com.arunachala.um.model.Role;
import com.arunachala.um.repository.RoleRepository;
import com.arunachala.um.response.RoleResponse;
import com.arunachala.um.service.RoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("roleService")
public class RoleServiceImpl implements RoleService  {
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AlplRolesRepository alplRolesRepository;

	/**
	 * Fetches the list of Users.
	 * 
	 * @throws HcvsException
	 */
	//@PreAuthorize("hasAuthority('admin')")
	@Override
	public List<RoleResponse> getAllRoles() throws AlplAppException {
		try {
			List<Role> roles = roleRepository.findAll();
			if (!roles.isEmpty()) {
				List<RoleResponse> roleResponses = new ArrayList<>();
				for (Role role : roles) {
					RoleResponse roleResponse = new RoleResponse();
					roleResponse.setId(role.getId());
					roleResponse.setRoleName(role.getRoleName());
					roleResponse.setRoleDescription(role.getRoleDescription());
					roleResponses.add(roleResponse);
				}
				log.info("Number of user : " + roles.size());
				return roleResponses;
			} else {
				throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, "", AppConstants.SEV_HIGH, "");
			}
			
		} catch (Exception ex) {
			throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, "", AppConstants.SEV_HIGH, ex);
		}
	}

	@Override
	public List<AlplRoles> getAllAlplRoles() throws AlplAppException {
		List<AlplRoles> roles = new ArrayList<>();
		try {
			roles = alplRolesRepository.findAll();
			return roles;
		} catch (Exception e){
			throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, "", AppConstants.SEV_HIGH, e);
		}
	}

}
