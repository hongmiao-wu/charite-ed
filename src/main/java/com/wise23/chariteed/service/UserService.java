package com.wise23.chariteed.service;

import com.wise23.chariteed.model.User;

public interface UserService {
    public void saveUser(User user);

    public User getUser(String email);
}
