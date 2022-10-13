package com.arunachala.um.request;

import com.arunachala.alplcommon.entity.AlplOrganisationMaster;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CreateUserDto {

    private String username;
    private String email;
    private Integer empId;
    private Set<AlplOrganisationMaster> orgs;
    private String name;
    private List<UnitDeptRoleOrgMappingDto> roleDeptOrgMapList;

}