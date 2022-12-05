package com.wise23.chariteed.service;

import com.wise23.chariteed.model.User;

import java.util.List;

public interface UserService {
    public void saveUser(User user);
    public List<Object> isUserPresent(User user);
}
