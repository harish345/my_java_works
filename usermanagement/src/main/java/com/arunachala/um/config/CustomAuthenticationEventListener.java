package com.arunachala.um.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.arunachala.alplcommon.utils.AppSecurityConstants;
import com.arunachala.um.model.User;
import com.arunachala.um.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("authenticationEventListner")
public class CustomAuthenticationEventListener implements AuthenticationEventPublisher {

	@Autowired
	private UserRepository userRepository;

	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		log.info("---------------- Authentication Success --------------------");
		User user = (User) authentication.getPrincipal();
		log.info("User: {} logged in successfully", user.getEmail());

		// Clear login Failed attempts
		User userExists = userRepository.findByUsername(user.getUsername());
		if (userExists != null) {
			
			if (userExists.getLoginAttempts() != 0) {
				// Only updating the Table if there are any failed attempts more than 0
				log.info("Save User: Settting Login Attempts to 0 for user: {}", userExists.getUsername());
				userExists.setLoginAttempts(0);
				if (userRepository.save(userExists) != null) {
					log.info("Login Attempts Refreshed for user {}", userExists.getUsername());
				} else {
					log.error("Login Attempts Refresh Failed for User: {}", userExists.getUsername());
				}
			}
			
		} else {
			log.error("Save User: Settting Login Attempts to 0 for user: {} failed", userExists.getUsername());
		}

	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		try {
			log.info("---------------- Authentication Failure --------------------");
			String user = authentication.getName();
			log.info("User: {} authentication failed", authentication.getName());
			
			// Login Attempts increase counter and lock user
			User userExists = userRepository.findByUsername(user);
			if (userExists != null) {
				int loginAttempts = userExists.getLoginAttempts();
				
				if (loginAttempts +1 >= AppSecurityConstants.MAX_LOGIN_ATTEMPTS) {
					// Maximum Attempts Reached i.e. 5
					userExists.setAccountLocked(true);
					log.info("Maximum Invalid login attempts reached for user: {}. User will be locked now", user);
				} else {
					log.info("Login Attempt Failed: User: {} :: Successive Failed Attempts", user);
					loginAttempts += 1;
					userExists.setLoginAttempts(loginAttempts);
					log.info("Failed Login Attempts: {} for User: {}", loginAttempts, user);
				}
				
				if (userRepository.save(userExists) != null) {
					log.info("Login Attempts incremented for user {}", user);
				} else {
					log.error("Login Attempts increment Failed for User: {}", user);
				}
				
			} else {
				log.error("User Doesn't exist: Incrementing Login Attempts for user: {} failed", user);
			}
			
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid Username and Password");
		}
		
	}

}
