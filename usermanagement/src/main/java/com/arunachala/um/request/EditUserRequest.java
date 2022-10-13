package com.arunachala.um.request;

import java.util.Set;

import com.arunachala.alplcommon.entity.AlplOrganisationMaster;
import com.arunachala.um.model.Role;
import com.arunachala.um.model.UserRoleDepartmentMapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditUserRequest {

	private Long id;
	private String name;
	private String email;
	private Set<Role> roles;
	private Set<UserRoleDepartmentMapping> roleDeptMap;
	private Set<AlplOrganisationMaster> orgs;

	
}
