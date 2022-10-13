package com.arunachala.um.controller;

import com.arunachala.alplcommon.exception.AlplAppException;
import com.arunachala.um.response.UserRoleDto;
import com.arunachala.um.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/master/users")
@Slf4j
public class UserMasterController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/userlist/{unit}/{dept}/{role}/{empId}/{org}")
    public ResponseEntity<List<UserRoleDto>> getUserList(@PathVariable(value = "unit") long unitId, @PathVariable(value = "dept") long deptId, @PathVariable(value = "role") long roleId,@PathVariable(value = "empId") String empId,@PathVariable(value = "org") int org) throws AlplAppException {
        List<UserRoleDto> response = userService.getListOfUsers(unitId, deptId, roleId,org,empId);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new  ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping(value = "/userlist/{unit}/{dept}/{role}/{org}")
    public ResponseEntity<List<UserRoleDto>> getUserList(@PathVariable(value = "unit") long unitId, @PathVariable(value = "dept") long deptId, @PathVariable(value = "role") long roleId,@PathVariable(value = "org") int org) throws AlplAppException {
        List<UserRoleDto> response = userService.getListOfUsers(unitId, deptId, roleId,org);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new  ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
