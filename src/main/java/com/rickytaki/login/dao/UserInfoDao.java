package com.rickytaki.login.dao;

import com.rickytaki.login.model.UserInfo;

import java.util.Optional;

public interface UserInfoDao {

    public void save (UserInfo userInfo);
    public Optional<UserInfo> findByName (String name);
    public Optional<UserInfo> findByEmail(String s);
}
