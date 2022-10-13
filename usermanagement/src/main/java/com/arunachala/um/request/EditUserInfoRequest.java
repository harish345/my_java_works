package com.arunachala.um.request;

import com.arunachala.alplcommon.entity.AlplOrganisationMaster;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class EditUserInfoRequest {

    private Long id;
    private String name;
    private Set<AlplOrganisationMaster> orgs;
    private List<UnitDeptRoleOrgMappingDto> roleDeptOrgMapList;
    private Boolean isEnabled;
    private String email;

}
