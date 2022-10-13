package com.arunachala.um.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CpDetails implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7322487943555406979L;
	private String cpName;
	private Double lat;
	private Double lon;

}
