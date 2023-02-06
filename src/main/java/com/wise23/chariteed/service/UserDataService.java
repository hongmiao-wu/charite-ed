package com.wise23.chariteed.service;

import com.wise23.chariteed.model.UserData;
import com.wise23.chariteed.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDataService implements UserDetailsService {

//    @Autowired
//    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserDataRepository userDataRepository;


    public UserData saveUserData(UserData user) {
//        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        String encodedPassword = passwordEncoder().encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userDataRepository.save(user);
    }

    public UserData getUserDataByEmail(String email) throws UsernameNotFoundException {
        return userDataRepository.findByFirstName(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("USER_NOT_FOUND", email)));
    }

    public UserData getUserData(String email) {
        return userDataRepository.getUserDataByEmail(email);
    }

    public UserData deleteByFullNameAndMobile(String firstName, String lastName, String mobile) {
        return userDataRepository.deleteByFullNameAndMobile(firstName, lastName, mobile);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userDataRepository.findByFirstName(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("USER_NOT_FOUND", email)));
    }

    public boolean userExists(UserData user) {
        return userDataRepository.existsByEmailAndMobile(user.getEmail(), user.getMobile());
    }

    public UserData findById(Long id) {
        return userDataRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("USER_NOT_FOUND", id)));
    }

    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
