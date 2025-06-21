package com.recime.api.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDTO {
    
    private Long id;
    private String title;
    private String description;
    private List<String> ingredients;
    private String instructions;
    private Boolean vegetarian;
    private Integer servings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}