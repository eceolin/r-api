package com.recime.api.service;

import com.recime.api.entity.Recipe;
import com.recime.api.exception.ResourceNotFoundException;
import com.recime.api.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe testRecipe;

    @BeforeEach
    void setUp() {
        testRecipe = Recipe.builder()
                .id(1L)
                .title("Test Recipe")
                .description("Test Description")
                .instructions("Test Instructions")
                .vegetarian(true)
                .servings(4)
                .build();

        // Set ingredients using helper method
        testRecipe.setIngredientNames(Arrays.asList("Ingredient 1", "Ingredient 2"));
    }

    @Test
    void createRecipe_ShouldReturnSavedRecipe() {
        when(recipeRepository.save(any(Recipe.class))).thenReturn(testRecipe);

        Recipe savedRecipe = recipeService.createRecipe(testRecipe);

        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe.getTitle()).isEqualTo("Test Recipe");
        verify(recipeRepository, times(1)).save(testRecipe);
    }

    @Test
    void getRecipeById_WhenRecipeExists_ShouldReturnRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));

        Recipe foundRecipe = recipeService.getRecipeById(1L);

        assertThat(foundRecipe).isNotNull();
        assertThat(foundRecipe.getId()).isEqualTo(1L);
        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    void getRecipeById_WhenRecipeDoesNotExist_ShouldThrowException() {
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.getRecipeById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recipe not found with id: 99");

        verify(recipeRepository, times(1)).findById(99L);
    }

    @Test
    void getAllRecipes_ShouldReturnListOfRecipes() {
        List<Recipe> recipes = Arrays.asList(testRecipe,
                Recipe.builder().id(2L).title("Recipe 2").build());
        when(recipeRepository.findAll()).thenReturn(recipes);

        List<Recipe> foundRecipes = recipeService.getAllRecipes();

        assertThat(foundRecipes).hasSize(2);
        assertThat(foundRecipes.get(0).getTitle()).isEqualTo("Test Recipe");
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    void updateRecipe_ShouldReturnUpdatedRecipe() {
        Recipe updatedDetails = Recipe.builder()
                .title("Updated Recipe")
                .description("Updated Description")
                .instructions("Updated Instructions")
                .vegetarian(false)
                .servings(2)
                .build();

        // Set ingredients using helper method
        updatedDetails.setIngredientNames(Arrays.asList("New Ingredient"));

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(testRecipe);

        Recipe updatedRecipe = recipeService.updateRecipe(1L, updatedDetails);

        assertThat(updatedRecipe).isNotNull();
        verify(recipeRepository, times(1)).findById(1L);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void deleteRecipe_WhenRecipeExists_ShouldDeleteRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        doNothing().when(recipeRepository).delete(testRecipe);

        recipeService.deleteRecipe(1L);

        verify(recipeRepository, times(1)).findById(1L);
        verify(recipeRepository, times(1)).delete(testRecipe);
    }

    @Test
    void searchRecipes_ShouldReturnFilteredRecipes() {
        List<Recipe> filteredRecipes = Arrays.asList(testRecipe);
        List<String> includeIngredients = Arrays.asList("Ingredient 1");
        List<String> excludeIngredients = Arrays.asList("Unwanted Ingredient");

        when(recipeRepository.searchRecipes(true, 4, includeIngredients, excludeIngredients, null))
                .thenReturn(filteredRecipes);

        List<Recipe> results = recipeService.searchRecipes(true, 4, includeIngredients, excludeIngredients, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Test Recipe");
        verify(recipeRepository, times(1))
                .searchRecipes(true, 4, includeIngredients, excludeIngredients, null);
    }
}