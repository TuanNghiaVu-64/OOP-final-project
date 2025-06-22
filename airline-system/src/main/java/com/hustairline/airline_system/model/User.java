package com.hustairline.airline_system.model;

public class User extends BaseEntity {
    private String username;
    private String password;
    private String role;
    private boolean approved;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}

    @Override
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty();
    }

    @Override
    public String getEntityType() {
        return "User";
    }

    // Getters and Setters
    
}
