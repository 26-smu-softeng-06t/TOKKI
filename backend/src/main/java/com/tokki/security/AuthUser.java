package com.tokki.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthUser extends AbstractAuthenticationToken {

    private final String uid;
    private final String role;

    public AuthUser(String uid) {
        this(uid, "user");
    }

    public AuthUser(String uid, String role) {
        super(authorities(role));
        this.uid = uid;
        this.role = role;
        setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> authorities(String role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    public String getUid() {
        return uid;
    }

    public String getRole() {
        return role;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return uid;
    }
}
