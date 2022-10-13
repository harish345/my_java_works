package com.arunachala.um.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arunachala.alplcommon.exception.AlplAppException;
import com.arunachala.um.request.PasswordUpdateRequest;
import com.arunachala.um.service.PasswordService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class PasswordController {

	@Autowired
	private PasswordService passwordService;

	/**
	 * Update Password: on temporary password
	 * 
	 * @param <T>
	 * @throws HcvsAppException
	 */
	@PostMapping("/updatePassword")
	public ResponseEntity<Map<String, String>> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest)
			throws AlplAppException {
		Map<String, String> responseData;
		responseData = passwordService.updatePassword(passwordUpdateRequest.getUsername(), passwordUpdateRequest.getOldPassword(), passwordUpdateRequest.getNewPassword());
		if (responseData.containsKey("Success")) {
			return new ResponseEntity<>(responseData, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
		}

	}

}
