package net.invictusmanagement.invictuslifestyle.models;

public class UserStatus {
    private Boolean isActive;
    private String role;

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}