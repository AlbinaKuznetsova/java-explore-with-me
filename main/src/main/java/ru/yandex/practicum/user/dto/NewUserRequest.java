package ru.yandex.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @Size(min = 6, message = "{validation.email.size.too_short}")
    @Size(max = 254, message = "{validation.email.size.too_long}")
    @NotBlank
    @Email
    private String email;
    @Size(min = 2, message = "{validation.name.size.too_short}")
    @Size(max = 250, message = "{validation.name.size.too_long}")
    @NotBlank
    private String name;
}
