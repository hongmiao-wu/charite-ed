package com.wise23.chariteed.token;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import com.wise23.chariteed.model.Role;

@Getter
@Setter
public class SignupDTO {
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 10)
    private String mobile;
    @NotBlank
    @Size(min = 7)
    private String password;
    private Role role;
}
