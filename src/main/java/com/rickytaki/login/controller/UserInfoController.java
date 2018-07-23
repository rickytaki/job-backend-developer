package com.rickytaki.login.controller;

import com.rickytaki.login.model.UserInfo;
import com.rickytaki.login.response.UserInfoResponse;
import com.rickytaki.login.service.UserInfoService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
public class UserInfoController {

    @NonNull
    private final UserInfoService service;

    @PostMapping (path = "/create", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser (@RequestBody UserInfo userInfo) {
        service.save(userInfo);
    }

    @GetMapping (value = "/findByName/{name}", produces = "application/json")
    public ResponseEntity<UserInfoResponse> findByName (@PathVariable String name) {
        UserInfoResponse user = service.findByName(name);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/findByEmail/{email:.+}", produces = "application/json")
    public ResponseEntity<UserInfoResponse> findByEmail (@PathVariable String email) {
        UserInfoResponse userInfo = service.findByEmail(email);
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
}
