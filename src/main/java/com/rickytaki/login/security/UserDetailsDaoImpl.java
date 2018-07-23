package com.rickytaki.login.security;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserDetailsDaoImpl implements UserDetailsDao {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<UserDetailsCustom> loadUserByUsername(String username) {

        String query = "SELECT u.email, u.password, u.name FROM user_info u WHERE u.email=? ";
        return getMapRow(username, query);
    }

    private Optional<UserDetailsCustom> getMapRow(String param, String query) {
        try {
            Optional<UserDetailsCustom> found = jdbcTemplate.queryForObject(query, (ResultSet rs, int rowNumber) -> {

                UserDetailsCustom user = new UserDetailsCustom();
                user.setUsername(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));

                return Optional.ofNullable(user);
            }, param);
            return found;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
