package com.arunachala.um.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse implements Serializable  {

	private static final long serialVersionUID = 2611136764706419468L;

	@JsonProperty("value")
	private int id;
	
	//@JsonProperty("roleName")
	@JsonIgnore
	private String roleName;
	
	@JsonProperty("label")
	private String roleDescription;
	
}
