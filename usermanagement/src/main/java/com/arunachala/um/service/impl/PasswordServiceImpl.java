package com.arunachala.um.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.arunachala.alplmaster.notification.EmailService;
import com.arunachala.alplmaster.request.TemplateRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arunachala.alplcommon.exception.AlplAppException;
import com.arunachala.um.model.PasswordHistory;
import com.arunachala.um.model.User;
import com.arunachala.um.repository.PasswordHistoryRepository;
import com.arunachala.um.repository.UserRepository;
import com.arunachala.um.request.UserDto;
import com.arunachala.um.service.PasswordService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class PasswordServiceImpl implements PasswordService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordHistoryRepository passwordHistoryRepository;

	@Autowired
	private PasswordEncoder userPasswordEncoder;

	@Autowired
	private Environment environment;

	@Autowired
	private EmailService emailService;

	@Override
	public Map<String, String> updatePassword(String username, String oldpwd, String pwd) throws AlplAppException {
		User existingUser = userRepository.findByUsername(username);
		Map<String, String> responseData = new HashMap<>();
		if (existingUser != null && userPasswordEncoder.matches(oldpwd, existingUser.getPassword())) {
			log.info("Updating password for user: {}" + username);
			String encryptedPwd = userPasswordEncoder.encode(pwd);
			boolean pwdcheck = checkOldPasswords(existingUser.getId(), pwd);
			if (pwdcheck) {
				// Bcrypted password
				existingUser.setPassword(encryptedPwd);
				Timestamp passwordCreatedTime = new Timestamp(System.currentTimeMillis());
				existingUser.setPasswordCreatedTime(passwordCreatedTime);
				existingUser.setTempPasswordFlag(false);
				existingUser.setCredentialsExpired(false);
				existingUser.setAccountExpired(false);
				existingUser.setAccountLocked(false);
				existingUser.setEnabled(true);

				User user = userRepository.save(existingUser);
				if (user != null) {
					PasswordHistory pwdhistory = passwordHistoryRepository.findByUserId(user.getId());
					pwdhistory.setPassword5(pwdhistory.getPassword4());
					pwdhistory.setPassword4(pwdhistory.getPassword3());
					pwdhistory.setPassword3(pwdhistory.getPassword2());
					pwdhistory.setPassword2(pwdhistory.getPassword1());
					pwdhistory.setPassword1(encryptedPwd);
					passwordHistoryRepository.save(pwdhistory);
					log.info("Updating password for user: {} successful " + username);
					responseData.put("Success", "Password Updated successfully");
					return responseData;
				} else {
					log.info("Updating password for user: {} failed " + username);
					responseData.put("Failure",
							"Password update attempt failed, please contact your system administrator.");
					return responseData;
				}
			} else {
				log.info("Updating password for user: {} failed, Password cannot be same has last 5 Passwords "
						+ username);
				responseData.put("Failure",
						"Password update attempt failed, Password cannot be same has last 5 Passwords.");
				return responseData;
			}

		} else {
			log.info("Updating password for user: {} failed, username and old password does not match with DB Username " + username);
			responseData.put("Failure", "Password update attempt failed, please enter valid username and password.");
			return responseData;
		}
	}

	public boolean checkOldPasswords(Long userId, String pwd) {
		PasswordHistory pwdHist = passwordHistoryRepository.findByUserId(userId);
		//Checking new password with the passwords from password history if it password doesnt match with any of the password it will check with next password if it matches it returns false if not matches it returns true
		if (pwdHist != null) {
			return (userPasswordEncoder.matches(pwd, pwdHist.getPassword1())) ? false
					: (userPasswordEncoder.matches(pwd, pwdHist.getPassword2())) ? false
							: (userPasswordEncoder.matches(pwd, pwdHist.getPassword3())) ? false
									: (userPasswordEncoder.matches(pwd, pwdHist.getPassword4())) ? false
											: (userPasswordEncoder.matches(pwd, pwdHist.getPassword5())) ? false : true;
		} else {
			return true;
		}
	}

	@Override
	public boolean forgotPassword(String username) {	

		User existingUser = userRepository.findByUsername(username); 

		if (existingUser != null) {
			String pwd = RandomStringUtils.randomAlphabetic(2).toUpperCase()+RandomStringUtils.randomAlphabetic(3).toLowerCase()+"@"+RandomStringUtils.randomNumeric(3);
			String encryptedPwd = userPasswordEncoder.encode(pwd); // Bcrypted password
			existingUser.setPassword(encryptedPwd);
			existingUser.setTempPasswordFlag(true); Timestamp currTimeStamp = new
					Timestamp(System.currentTimeMillis());
			existingUser.setPasswordCreatedTime(currTimeStamp);
			existingUser.setAccountLocked(false); existingUser.setAccountExpired(false);
			existingUser.setCredentialsExpired(false);

			User user = userRepository.save(existingUser); 

			if (user != null) {
				UserDto userDto	= new UserDto();
				userDto.setEmail(user.getEmail());
				userDto.setName(user.getName());
				userDto.setPassword(pwd);

				try {
					TemplateRequest request=new TemplateRequest();
					request.setSubject("Your Annamalaiyar - Command Center application password has been reset successfully");
					request.setTo(userDto.getEmail());
					request.setAction("forgotPasswordEmail");
					request.setChannel("Email");
					request.setLanguage("English");
					Map < String, Object > model = new HashMap<>();
					model.put("encryptedPwd",pwd);
					if(user.getEmpId()!=null) {
						model.put("empId",user.getEmpId());
					}else {
						model.put("empId",user.getUsername());
					}
					request.setValues(model);
					emailService.sendSimpleAlert(request);
					log.info("Forgot password for user: "+username+" successful"); 
					return true;
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					return false;

				}
			}
			else {
				log.info("Forgot password for user: "+username+" failed, email does not match with DB email");
				return false;
			}
		}
		return false;
	}
}
