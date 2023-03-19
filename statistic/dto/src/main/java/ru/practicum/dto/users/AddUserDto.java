package ru.practicum.dto.users;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddUserDto {
    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    String name;
    @NotBlank(message = "Field: email. Error: must not be blank. Value: null")
    @Email
    String email;
}