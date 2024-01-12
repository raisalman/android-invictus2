package net.invictusmanagement.invictuslifestyle.models;

public class AuthenticationResult {
    private long id;
    private String activationCode;
    private String roleName;
    private int roleId;
    private String authenticationCookie;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthenticationCookie() {
        return authenticationCookie;
    }

    public void setAuthenticationCookie(String authenticationCookie) {
        this.authenticationCookie = authenticationCookie;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}