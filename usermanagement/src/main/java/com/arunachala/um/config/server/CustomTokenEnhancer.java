package com.arunachala.um.config.server;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.arunachala.alplcommon.entity.AlplOrganisationMaster;
import com.arunachala.um.model.User;
import com.arunachala.um.model.UserRoleDepartmentMapping;
import com.arunachala.um.projection.UserCPDto;
import com.arunachala.um.repository.UserRepository;
import com.arunachala.um.request.CpDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CustomTokenEnhancer implements TokenEnhancer {
	
	@Autowired
	private UserRepository userRepository;
	
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInfo = new HashMap<>();
        User user = (User) authentication.getPrincipal();
		String username = user.getUsername();
		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
		for(GrantedAuthority a : authorities) {
			if(a.getAuthority().equals("driver_user")) {
				additionalInfo.put("driverId", username.split("@")[0]);
			}
			if(a.getAuthority().equals("cp_user")) {
				additionalInfo.put("cpDetails", getCpDetails(user.getId()));
			}
		}
		additionalInfo.put("unitDeptRole", buildRoleMap(user));
		additionalInfo.put("organisations", buildUserOrgList(user));
        additionalInfo.put("userId", user.getId());
        additionalInfo.put("role", user.getAuthorities());
        //additionalInfo.put("role_map", user.getRoleMappings());
        additionalInfo.put("tempLogin", user.getTempPasswordFlag());
        additionalInfo.put("userProfileName", user.getName());
        additionalInfo.put("username", username);        
        // Saving last login timestamp in User table
		log.info("User logged in successfully: " + username);
		DateTime currentTimestamp = new DateTime();
		DateTime zoned=(currentTimestamp).withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
		Long millis = zoned.getMillis();
		additionalInfo.put("loginTime", millis);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		Timestamp lastLoginTime = new Timestamp(currentTimestamp.getMillis());
		log.info(username + " user logged in sucessfully at " + lastLoginTime);
		
		user.setLastLoginTime(lastLoginTime);
		user = userRepository.save(user);
		
		if (user.getLastLoginTime() != null) {
			log.info(username + " last login timestamp saved in DB for user: " + username + ". Last login timestamp: " + lastLoginTime);
		} else {
			log.info(username + " last login timestamp FAILED in DB for user: " + username + ". Last login timestamp: " + lastLoginTime);
		}
        
        return accessToken;
    }

	private List<Map<String,String>> buildUserOrgList(User user) {
		List<Map<String,String>> orgs = new ArrayList<>();
		for(AlplOrganisationMaster o : user.getOrgs()) {
			Map<String,String> orgMap = new HashMap<>();
			orgMap.put("orgId", o.getId()+"");
			orgMap.put("orgName", o.getOrganisationName());
			orgs.add(orgMap);
		}
		return orgs;
	}

	private List<Map<String,String>> buildRoleMap(User user) {
		List<Map<String,String>> roles = new ArrayList<>();
		for(UserRoleDepartmentMapping r : user.getRoleMapping()) {
			Map<String,String> obj = new HashMap<>();
			obj.put("authority", r.getAlplRole().getRoleName());
			obj.put("department", r.getAlplDeptMaster().getDeptName());
			obj.put("roleValue", r.getAlplUnitMaster().getUnitId()+","+r.getAlplDeptMaster().getDeptId()+","+r.getAlplRole().getId());
			roles.add(obj);
		}
		return roles;
	}

	private Object getCpDetails(Long id) {
		List<CpDetails> cpdetails = new ArrayList<>();
		List<UserCPDto> objects = new ArrayList<>();
		try {
			objects = userRepository.getCpDetails(id);
		}catch(Exception e) {
			log.error("Error fetching cp details for user : "+id);
		}
		if(!objects.isEmpty()) {
			for(UserCPDto cp : objects) {
				cpdetails.add(new CpDetails(cp.getCpName(),cp.getLat(),cp.getLon()));
			}
		}
		return cpdetails;
	}
}
