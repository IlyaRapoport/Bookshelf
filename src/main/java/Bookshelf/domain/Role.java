package Bookshelf.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, ADMIN,STATISTIC;

    @Override
    public String getAuthority() {
        return name();
    }
}
