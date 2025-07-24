package ru.practicum.shareit.gateway.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    private long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
