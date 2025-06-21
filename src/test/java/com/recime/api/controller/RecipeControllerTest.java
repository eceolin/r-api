package com.recime.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recime.api.dto.RecipeRequest;
import com.recime.api.entity.Recipe;
import com.recime.api.exception.ResourceNotFoundException;
import com.recime.api.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private RecipeService recipeService;
    
    private Recipe testRecipe;
    private RecipeRequest testRequest;
    
    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        testRecipe = Recipe.builder()
                .id(1L)
                .title("Test Recipe")
                .description("Test Description")
                .instructions("Test Instructions")
                .vegetarian(true)
                .servings(4)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        // Set ingredients using helper method
        testRecipe.setIngredientNames(Arrays.asList("Ingredient 1", "Ingredient 2"));
        
        testRequest = RecipeRequest.builder()
                .title("Test Recipe")
                .description("Test Description")
                .ingredients(Arrays.asList("Ingredient 1", "Ingredient 2"))
                .instructions("Test Instructions")
                .vegetarian(true)
                .servings(4)
                .build();
    }
    
    @Test
    void createRecipe_ShouldReturnCreatedRecipe() throws Exception {
        when(recipeService.createRecipe(any(Recipe.class))).thenReturn(testRecipe);
        
        mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Recipe")))
                .andExpect(jsonPath("$.vegetarian", is(true)))
                .andExpect(jsonPath("$.servings", is(4)));
        
        verify(recipeService, times(1)).createRecipe(any(Recipe.class));
    }
    
    @Test
    void createRecipe_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        RecipeRequest invalidRequest = RecipeRequest.builder()
                .title("")  // Empty title
                .ingredients(Arrays.asList())  // Empty ingredients
                .build();
        
        mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verify(recipeService, never()).createRecipe(any(Recipe.class));
    }
    
    @Test
    void getRecipeById_WhenRecipeExists_ShouldReturnRecipe() throws Exception {
        when(recipeService.getRecipeById(1L)).thenReturn(testRecipe);
        
        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Recipe")));
        
        verify(recipeService, times(1)).getRecipeById(1L);
    }
    
    @Test
    void getRecipeById_WhenRecipeDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(recipeService.getRecipeById(99L))
                .thenThrow(new ResourceNotFoundException("Recipe not found"));
        
        mockMvc.perform(get("/api/recipes/99"))
                .andExpect(status().isNotFound());
        
        verify(recipeService, times(1)).getRecipeById(99L);
    }

    @Test
    void getRecipeById_WhenRecipeThrowUnexpectedException_ShouldReturnInternalServerError() throws Exception {
        when(recipeService.getRecipeById(99L))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/recipes/99"))
                .andExpect(status().isInternalServerError());

        verify(recipeService, times(1)).getRecipeById(99L);
    }
    
    @Test
    void getAllRecipes_ShouldReturnListOfRecipes() throws Exception {
        List<Recipe> recipes = Arrays.asList(testRecipe, 
                Recipe.builder().id(2L).title("Recipe 2").build());
        when(recipeService.getAllRecipes()).thenReturn(recipes);
        
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Test Recipe")))
                .andExpect(jsonPath("$[1].title", is("Recipe 2")));
        
        verify(recipeService, times(1)).getAllRecipes();
    }
    
    @Test
    void updateRecipe_ShouldReturnUpdatedRecipe() throws Exception {
        when(recipeService.updateRecipe(eq(1L), any(Recipe.class))).thenReturn(testRecipe);
        
        mockMvc.perform(put("/api/recipes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Recipe")));
        
        verify(recipeService, times(1)).updateRecipe(eq(1L), any(Recipe.class));
    }
    
    @Test
    void deleteRecipe_ShouldReturnNoContent() throws Exception {
        doNothing().when(recipeService).deleteRecipe(1L);
        
        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isNoContent());
        
        verify(recipeService, times(1)).deleteRecipe(1L);
    }
    
    @Test
    void searchRecipes_WithAllFilters_ShouldReturnFilteredRecipes() throws Exception {
        List<Recipe> filteredRecipes = Arrays.asList(testRecipe);
        List<String> includeIngredients = Arrays.asList("ingredient 1");
        List<String> excludeIngredients = Arrays.asList("ingredient 3");
        
        when(recipeService.searchRecipes(true, 4, includeIngredients, excludeIngredients, "Test"))
                .thenReturn(filteredRecipes);
        
        mockMvc.perform(get("/api/recipes")
                .param("vegetarian", "true")
                .param("servings", "4")
                .param("includeIngredients", "Ingredient 1")
                .param("excludeIngredients", "Ingredient 3")
                .param("contentInstructions", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Recipe")));
        
        verify(recipeService, times(1))
                .searchRecipes(true, 4, includeIngredients, excludeIngredients, "Test");
    }
    
    @Test
    void searchRecipes_WithNoFilters_ShouldReturnAllRecipes() throws Exception {
        List<Recipe> allRecipes = Arrays.asList(testRecipe);
        when(recipeService.getAllRecipes()).thenReturn(allRecipes);
        
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        
        verify(recipeService, times(1)).getAllRecipes();
        verify(recipeService, never()).searchRecipes(any(), any(), any(), any(), any());
    }
}