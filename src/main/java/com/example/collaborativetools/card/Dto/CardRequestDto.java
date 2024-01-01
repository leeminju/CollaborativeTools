package com.example.collaborativetools.card.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CardRequestDto {

    @NotBlank(message = "카드 제목을 입력해 주세요")
    private String title;


}
