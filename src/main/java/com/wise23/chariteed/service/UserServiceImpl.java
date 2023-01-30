package com.wise23.chariteed.service;

import com.wise23.chariteed.model.PractitionerData;
import com.wise23.chariteed.model.User;
import com.wise23.chariteed.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByFirstName(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("USER_NOT_FOUND", email)));
    }

    @Override
    public User getUser(String email) {
        return userRepository.getUserByEmail(email);
    }
}
