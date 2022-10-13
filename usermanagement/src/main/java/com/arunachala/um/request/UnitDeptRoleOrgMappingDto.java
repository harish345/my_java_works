package com.arunachala.um.request;

import lombok.Data;

@Data
public class UnitDeptRoleOrgMappingDto {

    private Integer unitId;
    private Integer deptId;
    private Long roleId;
    private Integer orgId;

}
