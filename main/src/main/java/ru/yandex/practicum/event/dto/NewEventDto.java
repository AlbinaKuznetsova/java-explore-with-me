package ru.yandex.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @Size(min = 20)
    @Size(max = 2000)
    @NotBlank
    private String annotation;
    @NotNull
    private Integer category;
    @Size(min = 20)
    @Size(max = 7000)
    @NotBlank
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Future
    private LocalDateTime eventDate;
    private Location location;

    private boolean paid = false;
    @PositiveOrZero
    private Integer participantLimit = 0;
    private boolean requestModeration = true;
    @Size(min = 3)
    @Size(max = 120)
    @NotBlank
    private String title;
}
