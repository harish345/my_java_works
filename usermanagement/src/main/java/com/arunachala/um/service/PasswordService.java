package com.arunachala.um.service;

import java.util.Map;

import com.arunachala.alplcommon.exception.AlplAppException;


public interface PasswordService {

	Map<String, String> updatePassword(String username, String oldpwd, String pwd) throws AlplAppException;

	boolean forgotPassword(String email);

}
