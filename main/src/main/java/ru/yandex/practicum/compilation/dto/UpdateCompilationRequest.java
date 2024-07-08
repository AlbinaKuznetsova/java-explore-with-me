package ru.yandex.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    @Size(min = 1, message = "{validation.title.size.too_short}")
    @Size(max = 50, message = "{validation.title.size.too_long}")
    private String title;
    private Boolean pinned = false;
    private List<Integer> events = new ArrayList<>();
}
