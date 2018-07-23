package com.rickytaki.login.security;

import java.util.Optional;

public interface UserDetailsDao {

    public Optional<UserDetailsCustom> loadUserByUsername (String username);
}
