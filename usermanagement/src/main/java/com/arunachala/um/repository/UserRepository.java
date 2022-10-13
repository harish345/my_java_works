package com.arunachala.um.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arunachala.um.model.User;
import com.arunachala.um.projection.UserCPDto;
import com.arunachala.um.response.UserRoleDto;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findById(Long id);
	
	@Query("select u from User u where u.username<>'scheduler@arunachala.biz'")
	List<User> findAll();

	@Query(value="select user_name from alpl_user u, alpl_driver_list_master d\r\n" + 
			"where u.user_name=d.driver_code",nativeQuery=true)
	List<String> getDriverUsers();
	
	@Query("SELECT DISTINCT user FROM User user " +
            "INNER JOIN FETCH user.roles AS role " +
            "INNER JOIN FETCH user.roleMapping AS roleMapping " +
            "INNER JOIN FETCH user.orgs AS orgs " +
            "INNER JOIN FETCH role.permissions AS permission " +
            "WHERE user.username = :username")
    User findByUsername(@Param("username") String username);
	
	//	List<User> findAllOrderByUpdatedTimeDesc();
	List<User> findByEnabledOrderByUpdatedTimeDesc(boolean enabled);
	
	// @Query("select u from User u where u.enabled is true order by u.createdTime desc")
	@Query("select u from User u where u.enabled is true order by u.name asc")
	Page<User> findAllEnabledUsers(Pageable pageable);
	User findByEmail(String email);
	
	@Query(value = "select l.location_name as cpName,l.lat,l.lon from alpl_location_master l, alpl_user_control_point_mapping m\r\n" + 
			"where l.alpl_location_master_id = m.location_master_id\r\n" + 
			"and m.user_id = ?1",nativeQuery = true)
	List<UserCPDto> getCpDetails(Long id);

	@Query(value = "select au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email from alpl_user as au where au.user_id IN\r\n" + 
			"(select user_id from user_role as ur where ur.role_id = \r\n" + 
			"(select role_id from role as r where r.role_name = ?1))",nativeQuery = true)
	List<UserRoleDto> getUserListQuery(String roleName);

	@Query(value = "select distinct rslt.* from(\r\n" + 
			"select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role,au.alpl_emp_id\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur, \r\n" + 
			"unit_role_department_mapping urd  \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id  \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id  \r\n" + 
			"and ur.user_id=au.user_id  \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id  \r\n" + 
			"and urd.unit_id = ?1 and em.status='ACTIVE' and urd.dept_id = ?2 and urd.role_id = ?3 and urd.org_id=?4\r\n" + 
			"and (date || ' ' || ea.punch_in)\\:\\: timestamp>now()-interval '12 hours'\r\n" + 
			"and ea.is_absent=false\r\n" + 
			"and (\r\n" + 
			"(ea.punch_in is not null and ea.punch_in<>'00:00:00' and (ea.punch_out is null or ea.punch_out='00:00:00')	)\r\n" + 
			"or\r\n" + 
			"(ea.punch_in is not null and ea.punch_in<>'00:00:00' and (ea.punch_out is not null and ((extract(epoch from ea.punch_out)-extract(epoch from ea.punch_in))/60 between 0 and 5) )	)\r\n" + 
			")\r\n" + 
			"union\r\n" + 
			"select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role,au.alpl_emp_id \r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur,  \r\n" + 
			"unit_role_department_mapping urd   \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ur.user_id=au.user_id   \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id   \r\n" + 
			"and urd.unit_id = ?1 and em.status='ACTIVE' and urd.dept_id = ?2 and urd.role_id = 3 and urd.org_id=?4\r\n"+
			"union\r\n" + 
			"select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role,au.alpl_emp_id \r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur,  \r\n" + 
			"unit_role_department_mapping urd   \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ur.user_id=au.user_id   \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id   \r\n" + 
			"and urd.unit_id = ?1 and em.status='ACTIVE' and urd.dept_id = ?2 and urd.role_id = 4 and urd.org_id=?4\r\n"+
			"union\r\n" + 
			"select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role,au.alpl_emp_id\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur, \r\n" + 
			"unit_role_department_mapping urd  \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id  \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id  \r\n" + 
			"and ur.user_id=au.user_id  \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id  \r\n" + 
			"and urd.unit_id = 6 and em.status='ACTIVE' and urd.dept_id = ?2 and urd.role_id = 2 and urd.org_id=?4\r\n" + 
			"union\r\n" + 
			"select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role,au.alpl_emp_id\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur, \r\n" + 
			"unit_role_department_mapping urd  \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id  \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id  \r\n" + 
			"and ur.user_id=au.user_id  \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id  \r\n" + 
			"and urd.unit_id = 0 and em.status='ACTIVE' and urd.dept_id = 30 and urd.role_id = 1 and urd.org_id=?4) rslt",nativeQuery = true)
	List<UserRoleDto> getUserListAsDept(long unitId, long deptId, long roleId,int org);
	
	@Query(value = "select distinct userName,userId,name,email, role from(\r\n" + 
			"select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur,  \r\n" + 
			"unit_role_department_mapping urd   \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ur.user_id=au.user_id   \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id   \r\n" + 
			"and au.user_name=?1 and em.status='ACTIVE'\r\n" + 
			"and urd.unit_id = ?2\r\n" + 
			"and urd.dept_id = ?3\r\n" + 
			"and urd.role_id = ?4\r\n" + 
			"and urd.org_id=?5\r\n" + 
			"union\r\n" + 
			"select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur,  \r\n" + 
			"unit_role_department_mapping urd   \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ur.user_id=au.user_id   \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id   \r\n" + 
			"and urd.unit_id in ?6 and em.status='ACTIVE'\r\n" + 
			"and urd.dept_id = ?3\r\n" + 
			"and urd.role_id in ?7\r\n" + 
			"and urd.org_id=?5\r\n" + 
			"and ea.date >=current_date-1\r\n" + 
			"and (ea.punch_out is null or ea.punch_out ='00:00:00') ) rslt",nativeQuery = true)
	List<UserRoleDto> getUserListAsDept(String empId,long unitId, long deptId, long roleId,int org, List<Integer> units, List<Integer> mgrHodEd);
	
	@Query(value = "select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur,  \r\n" + 
			"unit_role_department_mapping urd   \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ur.user_id=au.user_id   \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id   \r\n" + 
			"and urd.unit_id = ?1 and em.status='ACTIVE'\r\n" + 
			"and urd.dept_id = ?2\r\n" + 
			"and urd.role_id = ?3\r\n" + 
			"and urd.org_id=?4 " ,nativeQuery = true)
	List<UserRoleDto> getUserListByRoleUnit(long unitId, long deptId, long roleId,int org);

	
	@Query(value = "select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur,  \r\n" + 
			"unit_role_department_mapping urd   \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ur.user_id=au.user_id   \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id   \r\n" + 
			"and urd.unit_id = ?1 and em.status='ACTIVE'\r\n" + 
			"and urd.dept_id = ?2\r\n" + 
			"and urd.role_id = ?3\r\n" + 
			"and urd.org_id=?4 and (date || ' ' || ea.punch_in)\\:\\: timestamp>now()-interval '12 hours'\r\n" + 
			"and ea.is_absent=false\r\n" + 
			"and (\r\n" + 
			"(ea.punch_in is not null and ea.punch_in<>'00:00:00' and (ea.punch_out is null or ea.punch_out='00:00:00')	)\r\n" + 
			"or\r\n" + 
			"(ea.punch_in is not null and ea.punch_in<>'00:00:00' and (ea.punch_out is not null and ((extract(epoch from ea.punch_out)-extract(epoch from ea.punch_in))/60 between 0 and 5) )	)\r\n" + 
			") " ,nativeQuery = true)
	List<UserRoleDto> getUserListByRoleUnitByAttendence(long unitId, long deptId, long roleId,int org);
	
	@Query(value="select distinct userName,userId,name,email, role from(\r\n" + 
			"select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur,  \r\n" + 
			"unit_role_department_mapping urd   \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ur.user_id=au.user_id   \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id   \r\n" + 
			"and au.user_name=?1 and em.status='ACTIVE'\r\n" + 
			"and urd.unit_id = ?2\r\n" + 
			"and urd.dept_id = ?3\r\n" + 
			"and urd.role_id = ?4\r\n" + 
			"and urd.org_id = ?5\r\n" + 
			"union\r\n" + 
			"\r\n" + 
			"select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur,  \r\n" + 
			"unit_role_department_mapping urd   \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ur.user_id=au.user_id   \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id   \r\n" + 
			"and au.user_name='10001')rslt",nativeQuery = true)
	List<UserRoleDto> getUserListEdHod(String empId,long unitId, long deptId, long roleId,int org);

	
	@Query(value = "select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur,  \r\n" + 
			"unit_role_department_mapping urd   \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ur.user_id=au.user_id   \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id   \r\n" + 
			"and au.user_name=?1 and em.status='ACTIVE'\r\n" + 
			"and urd.unit_id = ?2\r\n" + 
			"and urd.dept_id = ?3\r\n" + 
			"and urd.role_id = ?4\r\n" + 
			"and urd.org_id = ?5",nativeQuery = true)
	UserRoleDto getUser(String empId,long unitId, long deptId, long roleId,int org);
	
	@Query(value = "select distinct au.user_name as userName, au.user_id as UserId, au.name as name ,au.email as email ,urd.role_id as role\r\n" + 
			"from alpl_user as au ,alpl_employee_master em,alpl_employee_attendance ea,user_role_mapping ur,  \r\n" + 
			"unit_role_department_mapping urd   \r\n" + 
			"where au.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ea.alpl_emp_id=em.alpl_emp_id   \r\n" + 
			"and ur.user_id=au.user_id   \r\n" + 
			"and ur.urd_mapping_id = urd.role_dep_id   \r\n" + 
			"and au.user_name=?1 and em.status='ACTIVE'\r\n" + 
			"and urd.unit_id = ?2\r\n" + 
			"and urd.dept_id = ?3\r\n" + 
			"and urd.role_id = ?4\r\n" + 
			"and urd.org_id = ?5 and (date || ' ' || punch_in)\\:\\: timestamp>now()-interval '12 hours'\r\n" + 
			"and ea.is_absent=false\r\n" + 
			"and (\r\n" + 
			"(ea.punch_in is not null and ea.punch_in<>'00:00:00' and (ea.punch_out is null or ea.punch_out='00:00:00')	)\r\n" + 
			"or\r\n" + 
			"(ea.punch_in is not null and ea.punch_in<>'00:00:00' and (ea.punch_out is not null and ((extract(epoch from ea.punch_out)-extract(epoch from ea.punch_in))/60 between 0 and 5) )	)\r\n" + 
			")",nativeQuery = true)
	UserRoleDto getUserWithAttendence(String empId,long unitId, long deptId, long roleId,int org);
	
	@Query(value = "select distinct superior_role_id from alpl_role_hierarchy\r\n" + 
			"where unit_id=?1\r\n" + 
			"and dept_id =?2\r\n" + 
			"and role_id=?3",nativeQuery = true)
	Integer getSuperiorRole(long unitId, long deptId, long roleId);


	
}
