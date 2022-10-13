package com.arunachala.um.service.impl;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.arunachala.alplcommon.entity.AlplDepartmentMaster;
import com.arunachala.alplcommon.entity.AlplOrganisationMaster;
import com.arunachala.alplcommon.entity.AlplRoleMaster;
import com.arunachala.alplcommon.entity.AlplUnitMaster;
import com.arunachala.alplcommon.exception.InvalidRequestException;
import com.arunachala.alplcommon.repository.EmployeeMasterRepository;
import com.arunachala.alplmaster.notification.EmailService;
import com.arunachala.alplmaster.request.TemplateRequest;
import com.arunachala.um.model.*;
import com.arunachala.um.repository.UnitDepartmentRoleRepo;
import com.arunachala.um.request.*;
import com.arunachala.um.response.UserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arunachala.alplcommon.exception.AlplAppException;
import com.arunachala.alplcommon.utils.AppConstants;
import com.arunachala.alplcommon.utils.AppErrorCodes;
import com.arunachala.um.repository.PasswordHistoryRepository;
import com.arunachala.um.repository.UserRepository;
import com.arunachala.um.response.UserResponse;
import com.arunachala.um.response.UserResponseDto;
import com.arunachala.um.response.UserRoleDto;
import com.arunachala.um.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "userService")
@PropertySource("classpath:application.properties")
public class UserServiceImpl implements UserDetailsService, UserService {

	private static final String AUTHORITY_GRANULARITY = "roles";

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordHistoryRepository passwordHistoryRepository;

	@Autowired
	private UnitDepartmentRoleRepo unitDepartmentRoleRepo;

	@Autowired
	private PasswordEncoder userPasswordEncoder;

	@Autowired
	private Environment environment;

	@Autowired
	private EmployeeMasterRepository empRepo;

	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(userId);
		if (user != null) {
			Set<SimpleGrantedAuthority> authorities = new HashSet<>();
			Set<SimpleGrantedAuthority> roleMappings = new HashSet<>();
			if (AUTHORITY_GRANULARITY.equalsIgnoreCase("roles")) {
				// For Roles as Authorities i.e. admin, manager, controller
				authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRoleName()))
						.collect(Collectors.toSet());
				roleMappings = user.getRoleMapping().stream().map(roleMap -> new SimpleGrantedAuthority(roleMap.getAlplRole().getRoleName())).collect(Collectors.toSet());
			} else if (AUTHORITY_GRANULARITY.equalsIgnoreCase("permissions")) {
				// For Permissions as Authorities i.e. permissions table data
				for (Role role : user.getRoles()) {
					authorities = role.getPermissions().stream()
							.map(permission -> new SimpleGrantedAuthority(permission.getPermissionName()))
							.collect(Collectors.toSet());
				}
			}
			// Set User Authorities either Roles or Permissions
			user.setAuthorities(authorities);
			user.setRoleMappings(roleMappings);

			return user;
		}
		throw new UsernameNotFoundException("Invalid username or password.");

	}

	public List<UserResponse> getAllUsers() {
		List<User> users = userRepository.findAll();

		return users.stream().filter(user -> user.isEnabled()).map(user -> {
			return new UserResponse(user.getId(), user.getName(), user.getEmail(),
					user.getRoles().iterator().next().getId(),user.getRoles().iterator().next().getRoleName(), user.getLastLoginTime(),user.getRoleMapping().iterator().next().getId().intValue(),
					user.getRoleMapping().iterator().next().getAlplRole().getDescription(),user.getRoleMapping().iterator().next().getAlplDeptMaster().getDeptName());
		}).collect(Collectors.toList());
	}

	@Override
	public UserResponse getUser(long user_id) {
		User user = userRepository.findById(user_id).get();
		if (user != null) {
			//				UserResponse(Long id, String name, String email, Integer roleId, String role, Timestamp lastLoginTime)
			UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRoles().iterator().next().getId(), user.getRoles().iterator().next().getRoleName(), user.getLastLoginTime(),user.getRoleMapping().iterator().next().getId().intValue(),
					user.getRoleMapping().iterator().next().getAlplRole().getDescription(),user.getRoleMapping().iterator().next().getAlplDeptMaster().getDeptName());
			return response;
		}
		else
			return null;
	}

	@Override
	public List<UserInfoDto> getAllUsersInfo() throws AlplAppException {
		List<UserInfoDto> userInfoDtoList = new ArrayList<>();
		try{
			List<User> userList = userRepository.findAll();
			List<String> drivers=userRepository.getDriverUsers();
			userList.forEach(user->{
				UserInfoDto userInfo = new UserInfoDto();
				userInfo.setId(user.getId());
				userInfo.setName(user.getName());
				userInfo.setIsEnabled(user.isEnabled());
				userInfo.setUserName(user.getUsername());
				userInfo.setPhone(user.getPhone());
				userInfo.setEmail(user.getEmail());
				userInfo.setAlplEmpId(user.getEmpId());
				Set<UserRoleDepartmentMapping> userMappingList = user.getRoleMapping();
				List<HashMap> orgs = new ArrayList<>();
				List<HashMap> roles = new ArrayList<>();
				List<HashMap> units = new ArrayList<>();
				List<HashMap> departments = new ArrayList<>();
				userMappingList.forEach(mapping ->{
					HashMap orgMap = new HashMap<>();
					HashMap rolesMap = new HashMap<>();
					HashMap deptMap = new HashMap<>();
					HashMap unitsMap = new HashMap<>();
					orgMap.put("id", mapping.getAlplOrganisationMaster().getId());
					orgMap.put("value",mapping.getAlplOrganisationMaster().getOrganisationName());
					orgs.add(orgMap);
					rolesMap.put("id", mapping.getAlplRole().getId());
					rolesMap.put("value", mapping.getAlplRole().getRoleName());
					roles.add(rolesMap);
					unitsMap.put("id", mapping.getAlplUnitMaster().getUnitId());
					unitsMap.put("value",mapping.getAlplUnitMaster().getUnitName());
					units.add(unitsMap);
					deptMap.put("id", mapping.getAlplDeptMaster().getDeptId());
					deptMap.put("value", mapping.getAlplDeptMaster().getDeptName());
					departments.add(deptMap);
				});
				userInfo.setOrgs(orgs);
				userInfo.setRoles(roles);
				userInfo.setUnits(units);
				userInfo.setDepartments(departments);
				if(drivers.contains(user.getUsername())) {
					userInfo.setUserType("DRIVER");
				}else {
					userInfo.setUserType("EMPLOYEE");
				}
				userInfoDtoList.add(userInfo);
			});
			return userInfoDtoList;
		} catch (Exception e){
			throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, e.getMessage(), AppConstants.SEV_HIGH, e);
		}
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User update(User user) {

		return userRepository.save(user);
	}

	@Override
	public User findUserByEmail(String email) throws AlplAppException {
		return userRepository.findByEmail(email);
	}
	/**
	 * Fetches users of a particular page.
	 * 
	 * @throws AlplAppException
	 */
	@Override
	public UserResponseDto getUsers(Integer page, Integer size) throws AlplAppException {
		try {
			UserResponseDto userResponseDto;
			log.info("Get page Number :: " + page + " User details. Size :: " + size);
			Pageable pageable = PageRequest.of(page, size);
			Page<User> users = userRepository.findAllEnabledUsers(pageable);
			if (users.hasContent()) {
				log.debug("Number of users in page :: " + page + "are " + users.getSize());
				List<UserResponse> userResponseList = users.stream().filter(user -> user.getEnabled()).map(user -> {
					return new UserResponse(user.getId(), user.getName(), user.getEmail(),
							user.getRoles().iterator().next().getId(), user.getRoles().iterator().next().getRoleDescription(), user.getLastLoginTime(),user.getRoleMapping().iterator().next().getId().intValue(),
							user.getRoleMapping().iterator().next().getAlplRole().getDescription(),user.getRoleMapping().iterator().next().getAlplDeptMaster().getDeptName());
				}).collect(Collectors.toList());

				userResponseDto = UserResponseDto.builder().userResponseList(userResponseList)
						.totalElements(users.getTotalElements()).totalPages(users.getTotalPages())
						.currentSliceNumber(users.getNumber()).currentSliceSize(users.getNumberOfElements())
						.hasPrevious(users.hasPrevious()).hasNext(users.hasNext()).isFirst(users.isFirst())
						.isLast(users.isLast()).build();

				return userResponseDto;
			} else {
				throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, "Page Number :: " + page, AppConstants.SEV_HIGH,
						"Data not fount in the page.");
			}
		} catch (Exception ex) {
			throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, "Page Number :: " + page, AppConstants.SEV_HIGH,
					ex);
		}
	}

	/**
	 * Creates new user.
	 * 
	 * @param User
	 * @throws AlplAppException
	 */
	@Override
	public Map<String, String> createNewUser(@Valid UserDto userdto) throws AlplAppException {
		Map<String, String> responseData = new HashMap<>();

		try {
			User userObject = findUserByEmail(userdto.getEmail());
			String encryptedPwd = userPasswordEncoder.encode(userdto.getPassword());
			if (userObject != null) {
				if(userObject.getEnabled()){
					responseData.put("Failure", "User Already Exists!!");
					return responseData;
				}
				userObject.setEnabled(AppConstants.BOOLEAN_TRUE);
				userObject.setName(userdto.getName());
				userObject.setUsername(userdto.getUsername());
				userObject.setPassword(encryptedPwd);
				Timestamp currTimeStamp = new Timestamp(System.currentTimeMillis());
				userObject.setPasswordCreatedTime(currTimeStamp);
				userObject.setCreatedTime(currTimeStamp);
				userObject.setRoles(userdto.getRoles());
				userObject.setRoleMapping(userdto.getRoleDeptMap());
				userObject.setAccountExpired(AppConstants.BOOLEAN_FALSE);
				userObject.setAccountLocked(AppConstants.BOOLEAN_FALSE);
				userObject.setCredentialsExpired(AppConstants.BOOLEAN_FALSE);
				userObject.setTempPasswordFlag(AppConstants.BOOLEAN_TRUE);
				userObject.setOrgs(userdto.getOrgs());
				log.debug("Enabling already existing User : {}", userObject);
				if (userRepository.save(userObject) != null) {
					PasswordHistory pwdhistory = passwordHistoryRepository.findByUserId(userObject.getId());
					if(pwdhistory==null) {
						pwdhistory = new PasswordHistory();
						pwdhistory.setId(userObject.getId());
					}
					pwdhistory.setPassword1(null);
					pwdhistory.setPassword2(null);
					pwdhistory.setPassword3(null);
					pwdhistory.setPassword4(null);
					pwdhistory.setPassword5(null);
					passwordHistoryRepository.save(pwdhistory);
					responseData.put("Success", "Created User successfully");
					return responseData;
				} else {
					responseData.put("Failure", "Unable to Create user Please Try again");
					return responseData;
				}

			} else {
				User user = new User();
				user.setUsername(userdto.getUsername());
				user.setName(userdto.getName());
				user.setPassword(encryptedPwd); // Bcrypted password
				user.setEmail(userdto.getEmail());
				user.setEnabled(true);
				user.setAccountExpired(false);
				user.setAccountLocked(false);
				user.setCredentialsExpired(false);
				user.setTempPasswordFlag(true);
				Timestamp currTimeStamp = new Timestamp(System.currentTimeMillis());
				user.setCreatedTime(currTimeStamp);
				Timestamp passwordCreatedTime = new Timestamp(System.currentTimeMillis());
				user.setPasswordCreatedTime(passwordCreatedTime);
				user.setRoles(userdto.getRoles());
				user.setRoleMapping(userdto.getRoleDeptMap());
				user.setOrgs(userdto.getOrgs());
				log.debug("Create User : {}", user);
				if (userRepository.save(user) != null) {

					PasswordHistory pwdhistory = new PasswordHistory();
					pwdhistory.setUser(user);
					passwordHistoryRepository.save(pwdhistory);

					responseData.put("Success", "Created User successfully");
					return responseData;
				} else {
					responseData.put("Failure", "Unable to Create user Please Try again");
					return responseData;
				}
			}
		} catch (Exception ex) {
			throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND,
					"Employee ID :: " + userdto.getUsername() + " " + ex, AppConstants.SEV_HIGH, ex);
		}
	}

	@Override
	public Map<String, String> createUser(@Valid CreateUserDto createUserdto) throws AlplAppException, InvalidRequestException {
		Map<String, String> responseData = new HashMap<>();
		Set<Role> appRoles = new HashSet<>();
		Role role = new Role();
		role.setId(2);
		appRoles.add(role);
		try{
			User userObject = findUserByUsername(createUserdto.getUsername());
			String encryptedPwd = userPasswordEncoder.encode(AppConstants.DEFAULT_PASSWORD);
			if (userObject != null) {
				// Enable the user if not enabled and rest the user profile settings with new one
				if(userObject.isEnabled()){
					throw new InvalidRequestException(AppErrorCodes.NO_DATA_FOUND, "Emp ID :: " + createUserdto.getUsername() + " User Already Exists!!" , AppConstants.SEV_HIGH, "User Already Exists!!");
				}
				userObject.setEnabled(AppConstants.BOOLEAN_TRUE);
				userObject.setName(createUserdto.getName());
				userObject.setUsername(createUserdto.getUsername());
				userObject.setPassword(encryptedPwd);
				if(createUserdto.getEmpId()!=null) {
					userObject.setEmpId(createUserdto.getEmpId());
					userObject.setPhone(empRepo.getEmployeePhone(createUserdto.getEmpId()));
				}
				userObject.setEmail(createUserdto.getEmail());
				Timestamp currTimeStamp = new Timestamp(System.currentTimeMillis());
				userObject.setPasswordCreatedTime(currTimeStamp);
				userObject.setCreatedTime(currTimeStamp);
				//				Assign unit, dept, role and org mappings
				List<UnitDeptRoleOrgMappingDto> mappings = createUserdto.getRoleDeptOrgMapList();
				Set<UserRoleDepartmentMapping> mapList = new HashSet<>();
				mappings.forEach(mapping ->{
					UserRoleDepartmentMapping mapCheck = unitDepartmentRoleRepo.getMappingIdByUnitDeptRoleOrg(mapping.getUnitId(), mapping.getDeptId(), mapping.getRoleId(), mapping.getOrgId());
					UserRoleDepartmentMapping map = new UserRoleDepartmentMapping();
					if(mapCheck == null){
						AlplUnitMaster unitMaster = new AlplUnitMaster();
						AlplDepartmentMaster deptMaster = new AlplDepartmentMaster();
						AlplRoles roles = new AlplRoles();
						AlplOrganisationMaster orgMaster = new AlplOrganisationMaster();
						unitMaster.setUnitId(mapping.getUnitId());
						deptMaster.setDeptId(mapping.getDeptId());
						roles.setId(mapping.getRoleId());
						orgMaster.setId(mapping.getOrgId());
						map.setAlplUnitMaster(unitMaster);
						map.setAlplDeptMaster(deptMaster);
						map.setAlplRole(roles);
						map.setAlplOrganisationMaster(orgMaster);
						UserRoleDepartmentMapping mapSaved = unitDepartmentRoleRepo.save(map);
						mapList.add(mapSaved);
					} else{
						mapList.add(mapCheck);
					}
				});
				userObject.setRoleMapping(mapList);
				userObject.setAccountExpired(AppConstants.BOOLEAN_FALSE);
				userObject.setAccountLocked(AppConstants.BOOLEAN_FALSE);
				userObject.setCredentialsExpired(AppConstants.BOOLEAN_FALSE);
				userObject.setTempPasswordFlag(AppConstants.BOOLEAN_TRUE);
				userObject.setOrgs(createUserdto.getOrgs());
				userObject.setRoles(appRoles);
				log.debug("Enabling already existing User : {}", userObject);
				if (userRepository.save(userObject) != null) {
					PasswordHistory pwdhistory = passwordHistoryRepository.findByUserId(userObject.getId());
					if(pwdhistory==null) {
						pwdhistory = new PasswordHistory();
						pwdhistory.setId(userObject.getId());
					}
					pwdhistory.setPassword1(null);
					pwdhistory.setPassword2(null);
					pwdhistory.setPassword3(null);
					pwdhistory.setPassword4(null);
					pwdhistory.setPassword5(null);
					passwordHistoryRepository.save(pwdhistory);
					SendUserCreationEmail(userObject, AppConstants.DEFAULT_PASSWORD);
					responseData.put("Success", "Created User successfully");
					return responseData;
				} else {
					throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, "Emp ID :: " + createUserdto.getUsername() + " Unable to Create user Please Try again", AppConstants.SEV_HIGH, "Error while saving user");
				}
			} else{
				// Create New User
				User user = new User();
				user.setUsername(createUserdto.getUsername());
				user.setName(createUserdto.getName());
				user.setPassword(encryptedPwd); // Bcrypted password
				user.setEmail(createUserdto.getEmail());
				if(createUserdto.getEmpId()!=null) {
					user.setEmpId(createUserdto.getEmpId());
					user.setPhone(empRepo.getEmployeePhone(createUserdto.getEmpId()));
				}
				user.setEnabled(true);
				user.setAccountExpired(false);
				user.setAccountLocked(false);
				user.setCredentialsExpired(false);
				user.setTempPasswordFlag(true);
				Timestamp currTimeStamp = new Timestamp(System.currentTimeMillis());
				user.setCreatedTime(currTimeStamp);
				Timestamp passwordCreatedTime = new Timestamp(System.currentTimeMillis());
				user.setPasswordCreatedTime(passwordCreatedTime);
				//				Assign unit, dept, role and org mappings
				List<UnitDeptRoleOrgMappingDto> mappings = createUserdto.getRoleDeptOrgMapList();
				Set<UserRoleDepartmentMapping> mapList = new HashSet<>();
				mappings.forEach(mapping ->{
					UserRoleDepartmentMapping mapCheck = unitDepartmentRoleRepo.getMappingIdByUnitDeptRoleOrg(mapping.getUnitId(), mapping.getDeptId(), mapping.getRoleId(), mapping.getOrgId());
					UserRoleDepartmentMapping map = new UserRoleDepartmentMapping();
					if(mapCheck == null){
						AlplUnitMaster unitMaster = new AlplUnitMaster();
						AlplDepartmentMaster deptMaster = new AlplDepartmentMaster();
						AlplRoles roles = new AlplRoles();
						AlplOrganisationMaster orgMaster = new AlplOrganisationMaster();
						unitMaster.setUnitId(mapping.getUnitId());
						deptMaster.setDeptId(mapping.getDeptId());
						roles.setId(mapping.getRoleId());
						orgMaster.setId(mapping.getOrgId());
						map.setAlplUnitMaster(unitMaster);
						map.setAlplDeptMaster(deptMaster);
						map.setAlplRole(roles);
						map.setAlplOrganisationMaster(orgMaster);
						UserRoleDepartmentMapping mapSaved = unitDepartmentRoleRepo.save(map);
						mapList.add(mapSaved);
					} else{
						mapList.add(mapCheck);
					}
				});
				user.setRoleMapping(mapList);
				user.setOrgs(createUserdto.getOrgs());
				user.setRoles(appRoles);
				log.debug("Create User : {}", user);
				if (userRepository.save(user) != null) {
					PasswordHistory pwdhistory = new PasswordHistory();
					pwdhistory.setUser(user);
					passwordHistoryRepository.save(pwdhistory);
					SendUserCreationEmail(user, AppConstants.DEFAULT_PASSWORD);
					responseData.put("Success", "Created User successfully");
					return responseData;
				} else {
					throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, "Emp ID :: " + createUserdto.getUsername() + " Unable to Create user Please Try again", AppConstants.SEV_HIGH, "Error while saving user");
				}
			}
		} catch (InvalidRequestException ex){
			throw new InvalidRequestException(AppErrorCodes.NO_DATA_FOUND, ex.getMessage(), AppConstants.SEV_HIGH, ex.getDescription());
		} catch(AlplAppException ex){
			throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, ex.getMessage(), AppConstants.SEV_HIGH, ex.getDescription());
		} catch(Exception ex){
			throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, "Emp ID :: " + createUserdto.getUsername() + " " + ex, AppConstants.SEV_HIGH, ex);
		}
	}

	public Boolean SendUserCreationEmail(User userobj,String pwd){
		TemplateRequest request=new TemplateRequest();
		request.setSubject("Alpl User Account Creation");
		request.setTo(userobj.getEmail());
		request.setAction("newUserCreation");
		request.setChannel("Email");
		request.setLanguage("English");
		request.setSubject("Congratulations, your new Annamalaiyar - Command Center application account has been created in");
		Map < String, Object > model = new HashMap<>();
		model.put("name", userobj.getName());
		model.put("encryptedPwd", AppConstants.DEFAULT_PASSWORD);
		if(userobj.getEmpId()!=null) {
			model.put("empId",userobj.getEmpId().toString());
		}else {
			model.put("empId",userobj.getUsername());
		}
		request.setValues(model);
		emailService.sendSimpleAlert(request);
		return true;
	}

	@Override
	public User editUser(@Valid EditUserRequest userDetails) throws AlplAppException {
		try {

			User user = userRepository.findById(userDetails.getId()).get();
			user.setName(userDetails.getName());
			user.setEmail(userDetails.getEmail());
			user.setRoles(userDetails.getRoles());
			user.setOrgs(userDetails.getOrgs());
			user.setTempPasswordFlag(AppConstants.BOOLEAN_TRUE);
			user.setRoleMapping(userDetails.getRoleDeptMap());
			return userRepository.save(user);

		} catch (Exception ex) {
			throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND,
					"EmailID :: " + userDetails.getEmail() + ex.getMessage(), AppConstants.SEV_HIGH, ex);
		}
	}

	@Override
	public User editUserInfo(@Valid EditUserInfoRequest userDetails) throws AlplAppException {
		try {
			User user = userRepository.findById(userDetails.getId()).get();
			user.setName(userDetails.getName());
			user.setOrgs(userDetails.getOrgs());
			user.setTempPasswordFlag(AppConstants.BOOLEAN_TRUE);
			user.setEmail(userDetails.getEmail());
			List<UnitDeptRoleOrgMappingDto> mappings = userDetails.getRoleDeptOrgMapList();
			Set<UserRoleDepartmentMapping> mapList = new HashSet<>();
			mappings.forEach(mapping ->{
				UserRoleDepartmentMapping mapCheck = unitDepartmentRoleRepo.getMappingIdByUnitDeptRoleOrg(mapping.getUnitId(), mapping.getDeptId(), mapping.getRoleId(), mapping.getOrgId());
				UserRoleDepartmentMapping map = new UserRoleDepartmentMapping();
				if(mapCheck == null){
					AlplUnitMaster unitMaster = new AlplUnitMaster();
					AlplDepartmentMaster deptMaster = new AlplDepartmentMaster();
					AlplRoles roles = new AlplRoles();
					AlplOrganisationMaster orgMaster = new AlplOrganisationMaster();
					unitMaster.setUnitId(mapping.getUnitId());
					deptMaster.setDeptId(mapping.getDeptId());
					roles.setId(mapping.getRoleId());
					orgMaster.setId(mapping.getOrgId());
					map.setAlplUnitMaster(unitMaster);
					map.setAlplDeptMaster(deptMaster);
					map.setAlplRole(roles);
					map.setAlplOrganisationMaster(orgMaster);
					UserRoleDepartmentMapping mapSaved = unitDepartmentRoleRepo.save(map);
					mapList.add(mapSaved);
				} else{
					mapList.add(mapCheck);
				}
			});
			user.setRoleMapping(mapList);
			return userRepository.save(user);
		} catch (Exception ex) {
			throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND,
					"UserID :: " + userDetails.getId() + ex.getMessage(), AppConstants.SEV_HIGH, ex);
		}
	}

	@Override
	public User deleteUser(Long id) throws AlplAppException {
		try {

			User user = userRepository.findById(id).get();
			if (user != null) {
				user.setEnabled(false);
				return userRepository.saveAndFlush(user);
			} else {
				return null;
			}

		} catch (Exception ex) {
			throw new AlplAppException(AppErrorCodes.NO_DATA_FOUND, "ID :: " + id + ex.getMessage(),
					AppConstants.SEV_HIGH, ex);
		}
	}

	@Override
	public User loadUserById(Long id) throws AlplAppException {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));

		return user;
	}

	@Override
	public void delete(long id) {
		userRepository.deleteById(id);
	}

	@Override
	public List<UserRoleDto> getUserRole(String roleName) {

		List<UserRoleDto> user = userRepository.getUserListQuery(roleName);
		return user;
	}

	@Override
	public List<UserRoleDto> getListOfUsers(long unitId, long deptId, long roleId,int org,String empId) {
		List<UserRoleDto> user = new ArrayList<>();
		UserRoleDto usr = null;
		Integer superiorRole = null;
		if(empId.equals("10001") || (unitId==0  && roleId==0 && deptId==0)){
			usr = userRepository.getUser(empId, unitId, deptId, roleId, org);
			if(usr!=null)
				user.add(usr);
		}
		else {
			if(roleId==4) {
				usr=userRepository.getUserWithAttendence(empId, unitId, 0, roleId, org);
			}else if(roleId==2) {
				usr=userRepository.getUser(empId, 6, deptId, roleId, org);
			}else {
				usr=userRepository.getUserWithAttendence(empId, unitId, deptId, roleId, org);
			}
			
			superiorRole= userRepository.getSuperiorRole(unitId, deptId, roleId);
			if (superiorRole!=null && superiorRole==0) {
				UserRoleDto ed = userRepository.getUser("10001", 0, 0, 0, 1);
				if(usr!=null && ed!=null) {
					user.add(ed);
					user.add(usr);
				}
			}else if (superiorRole!=null && superiorRole==2) {
				user=userRepository.getUserListByRoleUnit(6, deptId, 2,org);
				if(usr!=null && user!=null) {
					user.add(usr);
				}
			}else if (superiorRole!=null && superiorRole==3) {
				user=userRepository.getUserListByRoleUnitByAttendence(unitId, deptId, 3,org);
				if(usr!=null && user!=null) {
					user.add(usr);
				}
			}else if (superiorRole!=null && superiorRole==4) {
				user=userRepository.getUserListByRoleUnitByAttendence(unitId, 0, 4,org);
				if(usr!=null && user!=null) {
					user.add(usr);
				}
			}else {
				user.add(usr);
			}
		}
		return user;
	}

	@Override
	public List<UserRoleDto> getListOfUsers(long unitId, long deptId, long roleId,int org) {
		List<UserRoleDto> user = userRepository.getUserListAsDept(unitId, deptId, roleId,org);
		return user;
	}
}