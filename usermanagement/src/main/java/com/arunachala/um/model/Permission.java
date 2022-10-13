package com.arunachala.um.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "permission", uniqueConstraints = { @UniqueConstraint(columnNames = { "permission_name" }) })
@EqualsAndHashCode(of = "id")
public class Permission implements Serializable {

	private static final long serialVersionUID = 4471530844854358576L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "permission_id")
	private Long id;

	@Column(name = "permission_name")
	private String permissionName;

}
