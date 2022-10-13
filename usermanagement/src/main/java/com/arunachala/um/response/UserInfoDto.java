package com.arunachala.um.response;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class UserInfoDto {

    private Long id;
    private String name;
    private String userName;
    private List<HashMap> orgs;
    private List<HashMap> roles;
    private List<HashMap> units;
    private List<HashMap> departments;
    private Boolean isEnabled;
    private String phone;
    private String email;
    private Integer alplEmpId;
    private String userType;

}
