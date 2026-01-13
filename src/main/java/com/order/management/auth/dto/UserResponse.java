package com.order.management.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

	private String id;
	private String email;
	private String fullName;
	private String role;
	private boolean active;
}
