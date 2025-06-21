package com.recime.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    private String description;
    
    @NotEmpty(message = "At least one ingredient is required")
    private List<String> ingredients;
    
    @NotBlank(message = "Instructions are required")
    private String instructions;
    
    private Boolean vegetarian;
    
    @Min(value = 1, message = "Servings must be at least 1")
    private Integer servings;
}