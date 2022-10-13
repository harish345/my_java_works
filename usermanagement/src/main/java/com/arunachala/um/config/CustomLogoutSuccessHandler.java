package com.arunachala.um.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.arunachala.alplcommon.utils.AppSecurityConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("customLogoutSuccessHandler")
public class CustomLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
		implements LogoutSuccessHandler {

	@Autowired
	private TokenStore tokenStore;

	@SuppressWarnings("unused")
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		String bearerToken = request.getHeader(AppSecurityConstants.HEADER_AUTHORIZATION);
		String token = null;
		String user = "";
		JSONObject responseObj = new JSONObject();

		if (bearerToken != null && (bearerToken.startsWith(AppSecurityConstants.BEARER_AUTHENTICATION)
				|| bearerToken.startsWith(AppSecurityConstants.BEARER_AUTHENTICATION_1))) {
			
			if (bearerToken.startsWith(AppSecurityConstants.BEARER_AUTHENTICATION)) {
				token = bearerToken.substring("Bearer".length() + 1);
			} else if (bearerToken.startsWith(AppSecurityConstants.BEARER_AUTHENTICATION_1)) {
				token = bearerToken.substring("bearer".length() + 1);
				log.info("bearer token: ", token);
			} else {
				log.info("Bearer Token: invalid, attempt bearer token");
			}

			log.info("Invalidating Access Token: " + token);

			OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
			OAuth2RefreshToken oAuth2RefreshToken = oAuth2AccessToken.getRefreshToken();
			if (oAuth2AccessToken != null) {
				user = (String)oAuth2AccessToken.getAdditionalInformation().get("username");
				tokenStore.removeAccessToken(oAuth2AccessToken);
				log.info("Access Token Invalidation Successful. User "+user+" has been logged out successfully.");
				
				if (oAuth2RefreshToken != null) {
					tokenStore.removeRefreshToken(oAuth2RefreshToken);
					log.info("Refresh Token Invalidation Successful. User "+user+" has been logged out successfully.");
				} else {
					log.warn("Refresh Token Invalidation failed. User "+user+" has been logged out successfully.");
				}
				response.setStatus(HttpServletResponse.SC_OK);
				try {
					responseObj.put("status", user+" logged out successfully");
				} catch (JSONException e) {
					log.error(e.getMessage());
				}
			} else {
				log.warn("Token Invalidation Failed, for user %s");
				response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
				try {
					responseObj.put("status",user+" logged out failed");
				} catch (JSONException e) {
					log.error(e.getMessage());
				}
			}
			response.getWriter().write(responseObj.toString());
		} else {
			log.warn("Invalid Bearer Token, token invalidation/logout attempt failed");
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
			try {
				responseObj.put("status", user+" logged out failed");
			} catch (JSONException e) {
				log.error(e.getMessage());
			}
			response.getWriter().write(responseObj.toString());
		}

	}
}
