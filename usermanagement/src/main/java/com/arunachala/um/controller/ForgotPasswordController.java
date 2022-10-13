package com.arunachala.um.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arunachala.alplcommon.exception.AlplAppException;
import com.arunachala.um.request.ForgotPasswordRequest;
import com.arunachala.um.service.PasswordService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
public class ForgotPasswordController {

	@Autowired
	private PasswordService passwordService;

	/**
	 * Forgot password controller
	 * 
	 * @param forgotPasswordRequest
	 * @return
	 * @throws AlplAppException
	 */
	@PostMapping("/forgotPassword")
	public ResponseEntity<HashMap<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest)
			throws AlplAppException {
		HashMap<String, String> responseData = new HashMap<>();
		boolean success = passwordService.forgotPassword(forgotPasswordRequest.getEmail());

		if (success) {
			log.info(
					"Forgot password for user request sent successfully. Please check the E-mail for temporary password.",
					forgotPasswordRequest.getEmail());
			responseData.put("Success", "Forgot password mail sent successfully.");
			return new ResponseEntity<>(responseData, HttpStatus.OK);
		} else {
			log.info("Forgot password attempt failed for user: "+forgotPasswordRequest.getEmail()+", please contact your system administrator.");
			responseData.put("error", "invalid_grant");
			responseData.put("error_description", "Forgot password request failed, please contact your system administrator.");
			return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
		}

	}

}
