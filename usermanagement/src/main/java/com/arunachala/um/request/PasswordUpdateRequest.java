package com.arunachala.um.request;

import lombok.Data;

@Data
public class PasswordUpdateRequest {
	private String username;
	private String oldPassword;
	private String newPassword;
}
