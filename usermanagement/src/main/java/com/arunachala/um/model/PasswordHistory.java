package com.arunachala.um.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "password_history")
public class PasswordHistory implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@Column(name = "password_1")
	private String password1;
	
	@Column(name = "password_2")
	private String password2;
	
	@Column(name = "password_3")
	private String password3;
	
	@Column(name = "password_4")
	private String password4;
	
	@Column(name = "password_5")
	private String password5;
	
	@UpdateTimestamp
	@Column(name = "update_timestamp")
	private Timestamp updateTime;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

}
