package com.recime.api.controller;

import com.recime.api.dto.RecipeDTO;
import com.recime.api.dto.RecipeRequest;
import com.recime.api.entity.Recipe;
import com.recime.api.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Recipe Controller", description = "Operations for managing recipes")
public class RecipeController {
    
    private final RecipeService recipeService;
    
    @PostMapping
    @Operation(summary = "Create a new recipe", description = "Creates a new recipe with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Recipe created successfully",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = RecipeDTO.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<RecipeDTO> createRecipe(@Valid @RequestBody RecipeRequest request) {
        Recipe recipe = mapToEntity(request);
        Recipe savedRecipe = recipeService.createRecipe(recipe);
        return new ResponseEntity<>(mapToDTO(savedRecipe), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a recipe by ID", description = "Returns a single recipe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recipe found",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = RecipeDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content)
    })
    public ResponseEntity<RecipeDTO> getRecipeById(@Parameter(description = "Recipe ID") @PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(mapToDTO(recipe));
    }
    
    @GetMapping
    @Operation(summary = "Get all recipes with optional filters", description = "Returns a list of all recipes or filtered recipes based on search criteria")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RecipeDTO.class)) })
    public ResponseEntity<List<RecipeDTO>> getAllRecipes(
            @Parameter(description = "Filter by vegetarian status") @RequestParam(required = false) Boolean vegetarian,
            @Parameter(description = "Filter by number of servings") @RequestParam(required = false) Integer servings,
            @Parameter(description = "Include recipes with these ingredients") @RequestParam(required = false) List<String> includeIngredients,
            @Parameter(description = "Exclude recipes with these ingredients") @RequestParam(required = false) List<String> excludeIngredients,
            @Parameter(description = "Content instructions to filter") @RequestParam(required = false) String contentInstructions) {
        
        // Convert ingredient lists to lowercase for case-insensitive matching
        List<String> lowerIncludeIngredients = includeIngredients != null ? 
            includeIngredients.stream().map(String::toLowerCase).collect(Collectors.toList()) : null;
        List<String> lowerExcludeIngredients = excludeIngredients != null ? 
            excludeIngredients.stream().map(String::toLowerCase).collect(Collectors.toList()) : null;
        
        List<Recipe> recipes;
        if (vegetarian != null || servings != null || includeIngredients != null || excludeIngredients != null || contentInstructions != null) {
            recipes = recipeService.searchRecipes(vegetarian, servings, lowerIncludeIngredients, lowerExcludeIngredients, contentInstructions);
        } else {
            recipes = recipeService.getAllRecipes();
        }
        
        List<RecipeDTO> recipeDTOs = recipes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipeDTOs);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a recipe", description = "Updates an existing recipe with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recipe updated successfully",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = RecipeDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<RecipeDTO> updateRecipe(@Parameter(description = "Recipe ID") @PathVariable Long id, 
                                                 @Valid @RequestBody RecipeRequest request) {
        Recipe recipeDetails = mapToEntity(request);
        Recipe updatedRecipe = recipeService.updateRecipe(id, recipeDetails);
        return ResponseEntity.ok(mapToDTO(updatedRecipe));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recipe", description = "Deletes a recipe by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Recipe deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    public ResponseEntity<Void> deleteRecipe(@Parameter(description = "Recipe ID") @PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    private Recipe mapToEntity(RecipeRequest request) {
        Recipe recipe = Recipe.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .instructions(request.getInstructions())
                .vegetarian(request.getVegetarian() != null ? request.getVegetarian() : false)
                .servings(request.getServings() != null ? request.getServings() : 1)
                .build();
        
        // Set ingredients using helper method
        recipe.setIngredientNames(request.getIngredients());
        return recipe;
    }
    
    private RecipeDTO mapToDTO(Recipe recipe) {
        return RecipeDTO.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .ingredients(recipe.getIngredientNames())
                .instructions(recipe.getInstructions())
                .vegetarian(recipe.getVegetarian())
                .servings(recipe.getServings())
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .build();
    }
}