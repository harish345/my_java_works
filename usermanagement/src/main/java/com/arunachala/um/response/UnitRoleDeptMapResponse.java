package com.arunachala.um.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitRoleDeptMapResponse {
	
	private Integer id;
	private String unit;
	private String dept;
	private String role;

}
