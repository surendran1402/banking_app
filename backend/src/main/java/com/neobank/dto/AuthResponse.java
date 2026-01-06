package com.neobank.dto;

import java.util.List;

public class AuthResponse {
    private boolean success;
    private String token;
    private UserDTO user;
    private List<UserDTO> users;
    private PaginationDTO pagination;
    private String message;
    private String error;

    public AuthResponse() {}

    public static AuthResponseBuilder builder() { return new AuthResponseBuilder(); }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public List<UserDTO> getUsers() { return users; }
    public void setUsers(List<UserDTO> users) { this.users = users; }

    public PaginationDTO getPagination() { return pagination; }
    public void setPagination(PaginationDTO pagination) { this.pagination = pagination; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public static class AuthResponseBuilder {
        private final AuthResponse r = new AuthResponse();

        public AuthResponseBuilder success(boolean success) { r.setSuccess(success); return this; }
        public AuthResponseBuilder token(String token) { r.setToken(token); return this; }
        public AuthResponseBuilder user(UserDTO user) { r.setUser(user); return this; }
        public AuthResponseBuilder users(List<UserDTO> users) { r.setUsers(users); return this; }
        public AuthResponseBuilder pagination(PaginationDTO pagination) { r.setPagination(pagination); return this; }
        public AuthResponseBuilder message(String message) { r.setMessage(message); return this; }
        public AuthResponseBuilder error(String error) { r.setError(error); return this; }

        public AuthResponse build() { return r; }
    }
}
