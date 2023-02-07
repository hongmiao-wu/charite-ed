package com.wise23.chariteed.token;

import com.wise23.chariteed.model.UserData;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="refresh_token")
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "refresh_id", unique = true, nullable = false)
    Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserData user;
}
