package com.wise23.chariteed.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @SequenceGenerator(name = "users_sequence", sequenceName = "users_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_sequence")
    private Long id;

    private String patientID;
    private String condition;

    @Lob
    private Blob file;

    @NotNull(message = "First Name cannot be empty")
    @Column(name = "first_name")
    private String firstName;

    @NotNull(message = "Last Name cannot be empty")
    @Column(name = "last_name")
    private String lastName;

    @NotNull(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email address")
    @Column(name = "email", unique = true)
    private String email;

    @NotNull(message = "Password cannot be empty")
    @Length(min = 7, message = "Password should be atleast 7 characters long")
    @Column(name = "password")
    private String password;

    @Column(name = "mobile", unique = true)
    @Length(min = 10, message = "Password should be atleast 10 number long")
    private String mobile;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "locked")
    private Boolean locked = false;

    @Column(name = "enabled")
    private Boolean enabled = true;

    public User(String firstName, String lastName, String email, String password, String mobile, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.role = role;
    }

    // For creating a patient user
    public User(String firstName, String lastName, String email, String password, String mobile, Role role,
            String patientID, Blob file, String condition) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.role = role;
        this.patientID = patientID;
        this.file = file;
        this.condition = condition;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
