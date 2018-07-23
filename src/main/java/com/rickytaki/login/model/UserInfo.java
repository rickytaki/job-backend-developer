package com.rickytaki.login.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfo implements Serializable {

    private String email;
    private String name;
    private String password;
    private int age;
    private String street;
    private int number;
    private String zipCode;

}
