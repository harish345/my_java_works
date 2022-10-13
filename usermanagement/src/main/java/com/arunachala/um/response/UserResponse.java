package com.arunachala.um.response;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserResponse {

	private Long id;
	private int roleId;
	private String name;
	private String email;
	private String role;
	private int unitRoleDeptId;
	private String userRole;
	private String dept;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy [HH:mm]")
	private Timestamp lastLoginTime;

	// private Set<Role> roles = new HashSet<Role>();
	public UserResponse(Long id, String name, String email, Integer roleId, String role, Timestamp lastLoginTime,int unitRoleDeptId,String userRole,String dept) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.roleId = roleId;
		this.role = role;
		this.lastLoginTime = lastLoginTime;
		this.userRole = userRole;
		this.dept=dept;
		this.unitRoleDeptId = unitRoleDeptId;
	}

}
