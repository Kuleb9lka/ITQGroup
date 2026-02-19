package com.ITQGroup.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotNull(message = "Login can't be null")
    @Size(min = 4, max = 60, message = "Login length must be in range >= 4 & <= 60")
    private String login;

    @NotNull(message = "Password can't be null")
    @Size(min = 8, max = 100, message = "Password length must be in range >= 8 & <= 100")
    private String password;

    @NotBlank(message = "Firstname can't be null or blank")
    @Size(min = 2, message = "Firstname min length must be 2 symbols at least")
    private String firstname;

    @NotBlank(message = "Lastname can't be null or blank")
    @Size(min = 2, message = "Lastname min length must be 2 symbols at least")
    private String lastname;
}
