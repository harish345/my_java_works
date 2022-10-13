package com.arunachala.um.repository;

import java.util.List;

import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.arunachala.um.model.UserRoleDepartmentMapping;
import com.arunachala.um.projection.UnitRoleDeptDto;

public interface UnitDepartmentRoleRepo extends JpaRepository<UserRoleDepartmentMapping,Long>{
	
	@Query(value = "select role_dep_id as id,u.unit_name unit,d.dept_name dept,r.description as role from unit_role_department_mapping rdm, alpl_unit_master u, alpl_department_master d,roles r\r\n" + 
			"where rdm.dept_id = d.dept_id\r\n" + 
			"and rdm.role_id = r.role_id\r\n" + 
			"and rdm.unit_id = u.unit_id",nativeQuery = true)
	List<UnitRoleDeptDto> getAllUnitDeptRoleMappings();

	@Query(value="select urdm.* from unit_role_department_mapping urdm where unit_id =?1 and dept_id =?2 and role_id =?3 and org_id =?4",
	nativeQuery = true)
	UserRoleDepartmentMapping getMappingIdByUnitDeptRoleOrg(Integer unitId, Integer deptId, Long roleId, Integer orgId);

}
