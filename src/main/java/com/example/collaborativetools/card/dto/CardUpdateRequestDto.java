package com.example.collaborativetools.card.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CardUpdateRequestDto {
    private String title;
    private String description;
    private String backgroundColor;
    private Integer sequence;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dueDate;

}
