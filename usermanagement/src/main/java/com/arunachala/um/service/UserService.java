package com.arunachala.um.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.arunachala.alplcommon.exception.AlplAppException;
import com.arunachala.alplcommon.exception.InvalidRequestException;
import com.arunachala.um.model.User;
import com.arunachala.um.request.CreateUserDto;
import com.arunachala.um.request.EditUserInfoRequest;
import com.arunachala.um.request.EditUserRequest;
import com.arunachala.um.request.UserDto;
import com.arunachala.um.response.UserInfoDto;
import com.arunachala.um.response.UserResponse;
import com.arunachala.um.response.UserResponseDto;
import com.arunachala.um.response.UserRoleDto;

public interface UserService {

	User save(User user);

	List<UserResponse> getAllUsers();

	UserResponse getUser(long id);

	List<UserInfoDto> getAllUsersInfo() throws AlplAppException;

	User findUserByUsername(String username);

	User update(User user);

	void delete(long id);

	User findUserByEmail(String email) throws AlplAppException;

	UserResponseDto getUsers(Integer page, Integer size) throws AlplAppException;

	Map<String, String> createNewUser(@Valid UserDto userdto) throws AlplAppException;

	Map<String, String> createUser(@Valid CreateUserDto createUserdto) throws AlplAppException, InvalidRequestException;

	User editUser(@Valid EditUserRequest userDetails) throws AlplAppException;

	User editUserInfo(@Valid EditUserInfoRequest userDetails) throws AlplAppException;

	User deleteUser(Long id) throws AlplAppException;

	User loadUserById(Long id) throws AlplAppException;

	List<UserRoleDto> getUserRole(String roleName);

	List<UserRoleDto> getListOfUsers(long unitId, long deptId, long roleId,int org,String empId);

	List<UserRoleDto> getListOfUsers(long unitId, long deptId, long roleId, int org);
}
