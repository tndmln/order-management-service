package com.order.management.auth.dto;

import com.order.management.auth.entities.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 8, max = 64)
	private String password;

	@NotBlank
	@Size(max = 100)
	private String fullName;

	private Role roles;
}
