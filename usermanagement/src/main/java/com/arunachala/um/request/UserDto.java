package com.arunachala.um.request;

import java.util.Set;

import com.arunachala.alplcommon.entity.AlplOrganisationMaster;
import com.arunachala.um.data.validation.ValidPassword;
import com.arunachala.um.model.Role;
import com.arunachala.um.model.UserRoleDepartmentMapping;

import lombok.Data;

@Data
public class UserDto {
	private String username;
	private String email;
	@ValidPassword
	private String password;
	private Set<Role> roles;
	private String name;
	private Integer orgId;
	private Set<UserRoleDepartmentMapping> roleDeptMap;
	private Set<AlplOrganisationMaster> orgs;
	
	
}
