package com.arunachala.um.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.arunachala.alplcommon.entity.AlplDepartmentMaster;
import com.arunachala.alplcommon.entity.AlplOrganisationMaster;
import com.arunachala.alplcommon.entity.AlplUnitMaster;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "unit_role_department_mapping")
public class UserRoleDepartmentMapping implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3097165786881542268L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_dep_id")
	private Long id;
	
	@OneToOne
	@JoinColumn(name="unit_id")
	private AlplUnitMaster alplUnitMaster;

	@OneToOne
	@JoinColumn(name="dept_id")
	private AlplDepartmentMaster alplDeptMaster;
	
	@OneToOne
	@JoinColumn(name="role_id")
	private AlplRoles alplRole;

	@OneToOne
	@JoinColumn(name="org_id")
	private AlplOrganisationMaster alplOrganisationMaster;

}
