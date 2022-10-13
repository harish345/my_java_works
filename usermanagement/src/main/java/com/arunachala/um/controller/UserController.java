package com.arunachala.um.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arunachala.alplcommon.exception.InvalidRequestException;
import com.arunachala.um.request.CreateUserDto;
import com.arunachala.um.request.EditUserInfoRequest;
import com.arunachala.um.response.UserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arunachala.alplcommon.exception.AlplAppException;
import com.arunachala.um.request.EditUserRequest;
import com.arunachala.um.request.UserDto;
import com.arunachala.um.response.UserResponse;
import com.arunachala.um.response.UserResponseDto;
import com.arunachala.um.response.UserRoleDto;
import com.arunachala.um.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/users")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	/**/

	/**
	 * Fetches all users.
	 * 
	 * @return
	 */
	@GetMapping("/allUsers")
	public ResponseEntity<List<UserResponse>> getAllUsers(){
		return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
	}

	/**
	 * Fetch users on the basis of page and size
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("/users")
	public ResponseEntity<UserResponseDto> getUsers(@RequestParam final Integer page, @RequestParam final Integer size)
			throws AlplAppException {
		return new ResponseEntity<>(userService.getUsers(page, size), HttpStatus.OK);
	}

	/**
	 * Fetch users on the basis
	 * @return
	 */
	@GetMapping("/getallusers")
	public ResponseEntity<List<UserInfoDto>> getAllUsersInfo() throws AlplAppException {
		return new ResponseEntity<>(userService.getAllUsersInfo(), HttpStatus.OK);
	}

	@GetMapping(value = "/user/{id}")
	public ResponseEntity<UserResponse> getUser(@PathVariable(value = "id") Long id) throws AlplAppException {
			UserResponse response = userService.getUser(id);
			if (response != null) {
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				return new  ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}

	/**
	 * Create User.
	 * 
	 * @param user
	 * @return
	 */
	//@PostMapping("/createUser")
	public ResponseEntity<Map<String, String>> createNewUser(@RequestBody UserDto userdto) throws AlplAppException {
		Map<String, String> responseData;
		responseData = userService.createNewUser(userdto);
		if (responseData.containsKey("Success")) {
			return new ResponseEntity<>(responseData, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Create New User.
	 *
	 * @param createUserDto
	 * @return
	 */
	@PostMapping("/createNewUser")
	public ResponseEntity<Map<String, String>> createNewUser(@RequestBody CreateUserDto createUserDto) throws AlplAppException, InvalidRequestException {
		Map<String, String> responseData;
		responseData = userService.createUser(createUserDto);
		return new ResponseEntity<>(responseData, HttpStatus.CREATED);
	}

	@DeleteMapping(value = "/user/{id}")
	public String delete(@PathVariable(value = "id") Long id) throws AlplAppException {
		userService.deleteUser(id);
		return "success";
	}

	/**
	 * Update the existing user.
	 * 
	 * @param userDetails
	 * @return
	 */
	@PostMapping("/editUser")
	public ResponseEntity<Map<String, String>> updateUser(@RequestBody EditUserRequest userDetails)
			throws AlplAppException {
		Map<String, String> responseData = new HashMap<>();
		if (userService.editUser(userDetails) != null) {
			responseData.put("Success", "Updated User successfully");
			return new ResponseEntity<>(responseData, HttpStatus.CREATED);
		} else {
			responseData.put("Failure", "User does not exists!!");
			return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Update the existing user.
	 *
	 * @param user
	 * @return
	 */
	@PostMapping("/editUserInfo")
	public ResponseEntity<Map<String, String>> updateUserInfo(@RequestBody EditUserInfoRequest userDetails)
			throws AlplAppException {
		Map<String, String> responseData = new HashMap<>();
		if (userService.editUserInfo(userDetails) != null) {
			responseData.put("Success", "Updated User successfully");
			return new ResponseEntity<>(responseData, HttpStatus.CREATED);
		} else {
			responseData.put("Failure", "User does not exists!!");
			return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Updates the database record as deleted.
	 * 
	 * @param user
	 * @return
	 */
	@PostMapping("/delete/{id}")
	public ResponseEntity<Map<String, String>> deleteUser(@PathVariable(value = "id") Long id) throws AlplAppException {
		Map<String, String> responseData = new HashMap<>();
		if (userService.deleteUser(id) != null) {
			responseData.put("Success", "Deleted User successfully");
			return new ResponseEntity<>(responseData, HttpStatus.CREATED);
		} else {
			responseData.put("Failure", "User does not exists!!");
			return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
		}

	}
	
	@GetMapping(value = "/usersbyrolename")
	public ResponseEntity<List<UserRoleDto>> getUser(@RequestParam String rolename) throws AlplAppException {
			List<UserRoleDto> response = userService.getUserRole(rolename);
			if (response != null) {
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				return new  ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}

}
