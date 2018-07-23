package com.rickytaki.login.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoResponse implements Serializable {

    private String email;
    private String name;
    private int age;
    private String street;
    private int number;
    private String zipCode;
}
