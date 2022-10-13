package com.arunachala.um.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.arunachala.alplcommon.entity.AlplOrganisationMaster;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@DynamicUpdate
@Table(name = "alpl_user", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_name" }) })
@EqualsAndHashCode(of = "id")
public class User implements UserDetails, Serializable {

	private static final long serialVersionUID = -7727183064762389028L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;
	
	@Column(name = "user_name")
	private String username;
	
	@Column(name = "name")
	private String name;
	
	private String password;
	@Email(message = "Email Id is not valid!")
	private String email;
	private Boolean enabled;
	
	@Column(name="account_expired")
	private Boolean accountExpired;
	
	@Column(name = "account_locked")
	private Boolean accountLocked;
	
	@Column(name = "credentials_expired")
	private Boolean credentialsExpired;
	private Timestamp lastLoginTime;
	private String createdBy;
	private String updatedBy;
	private Timestamp updatedTime;
	private Boolean tempPasswordFlag;
	private Timestamp passwordCreatedTime;
	private Timestamp createdTime;
	// Failed Login Attempts
	private int loginAttempts;
	
	private String phone;
	@Column(name = "alpl_emp_id")
	private Integer empId;
	
	@Column(name = "alpl_unit_id")
	private Integer unit;

	/*@ManyToMany
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@OrderBy
	private Set<Role> role;*/
	
	@ManyToMany(fetch = FetchType.LAZY, targetEntity = Role.class)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<Role>();
	
	@ManyToMany(fetch = FetchType.LAZY, targetEntity = UserRoleDepartmentMapping.class)
	@JoinTable(name = "user_role_mapping", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "urd_mapping_id"))
	private Set<UserRoleDepartmentMapping> roleMapping = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY, targetEntity = AlplOrganisationMaster.class)
	@JoinTable(name = "user_org", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "org_id"))
	private Set<AlplOrganisationMaster> orgs = new HashSet<>();

	@Transient
	private Collection<? extends GrantedAuthority> authorities;
	
	@Transient
	private Collection<? extends GrantedAuthority> roleMappings;

	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}

}
