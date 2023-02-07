package com.wise23.chariteed.token;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginDTO {
    @NotBlank
    private String firstname;
    @NotBlank
    private String password;
}
