package com.ITQGroup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "login", nullable = false)
    @NotNull(message = "Login can't be null")
    @Size(min = 4, max = 60, message = "Login length must be in range >= 4 & <= 60")
    private String login;

    @Column(name = "password", nullable = false)
    @NotNull(message = "Password can't be null")
    @Size(min = 8, max = 100, message = "Password length must be in range >= 8 & <= 100")
    private String password;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "Firstname can't be null or blank")
    @Size(min = 2, message = "Firstname min length must be 2 symbols at least")
    private String firstname;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Lastname can't be null or blank")
    @Size(min = 2, message = "Lastname min length must be 2 symbols at least")
    private String lastname;
}
