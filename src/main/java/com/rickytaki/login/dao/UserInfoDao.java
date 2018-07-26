package com.rickytaki.login.dao;

import com.rickytaki.login.model.UserInfo;

import java.util.Optional;

public interface UserInfoDao {

    void save (UserInfo userInfo);
    Optional<UserInfo> findByName (String name);
    Optional<UserInfo> findByEmail(String s);
}
