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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceEdgeCasesTest {

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
                .vegetarian(false)
                .servings(4)
                .build();
        testRecipe.setIngredientNames(Arrays.asList("Ingredient 1", "Ingredient 2"));
    }

    @Test
    void searchRecipes_WithAllNullParameters_ShouldReturnAllRecipes() {
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe);
        when(recipeRepository.searchRecipes(null, null, null, null, null))
                .thenReturn(expectedRecipes);

        List<Recipe> results = recipeService.searchRecipes(null, null, null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(testRecipe);
        verify(recipeRepository, times(1)).searchRecipes(null, null, null, null, null);
    }

    @Test
    void searchRecipes_WithEmptyIncludeIngredientsList_ShouldCallRepository() {
        List<String> emptyList = Collections.emptyList();
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe);
        when(recipeRepository.searchRecipes(null, null, emptyList, null, null))
                .thenReturn(expectedRecipes);

        List<Recipe> results = recipeService.searchRecipes(null, null, emptyList, null, null);

        assertThat(results).hasSize(1);
        verify(recipeRepository, times(1)).searchRecipes(null, null, emptyList, null, null);
    }

    @Test
    void searchRecipes_WithEmptyExcludeIngredientsList_ShouldCallRepository() {
        List<String> emptyList = Collections.emptyList();
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe);
        when(recipeRepository.searchRecipes(null, null, null, emptyList, null))
                .thenReturn(expectedRecipes);

        List<Recipe> results = recipeService.searchRecipes(null, null, null, emptyList, null);

        assertThat(results).hasSize(1);
        verify(recipeRepository, times(1)).searchRecipes(null, null, null, emptyList, null);
    }

    @Test
    void searchRecipes_WithEmptyContentToFilter_ShouldCallRepository() {
        String emptyContent = "";
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe);
        when(recipeRepository.searchRecipes(null, null, null, null, emptyContent))
                .thenReturn(expectedRecipes);

        List<Recipe> results = recipeService.searchRecipes(null, null, null, null, emptyContent);

        assertThat(results).hasSize(1);
        verify(recipeRepository, times(1)).searchRecipes(null, null, null, null, emptyContent);
    }

    @Test
    void searchRecipes_WithNoMatchingResults_ShouldReturnEmptyList() {
        List<String> includeIngredients = Arrays.asList("nonexistent");
        when(recipeRepository.searchRecipes(null, null, includeIngredients, null, null))
                .thenReturn(Collections.emptyList());

        List<Recipe> results = recipeService.searchRecipes(null, null, includeIngredients, null, null);

        assertThat(results).isEmpty();
        verify(recipeRepository, times(1)).searchRecipes(null, null, includeIngredients, null, null);
    }

    @Test
    void updateRecipe_WithNullDescription_ShouldUpdateWithNull() {
        Recipe existingRecipe = Recipe.builder()
                .id(1L)
                .title("Existing Recipe")
                .description("Existing Description")
                .instructions("Existing Instructions")
                .vegetarian(true)
                .servings(2)
                .build();

        Recipe updateData = Recipe.builder()
                .title("Updated Recipe")
                .description(null)
                .instructions("Updated Instructions")
                .vegetarian(false)
                .servings(4)
                .build();
        updateData.setIngredientNames(Arrays.asList("New Ingredient"));

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

        Recipe result = recipeService.updateRecipe(1L, updateData);

        assertThat(existingRecipe.getTitle()).isEqualTo("Updated Recipe");
        assertThat(existingRecipe.getDescription()).isNull();
        assertThat(existingRecipe.getInstructions()).isEqualTo("Updated Instructions");
        assertThat(existingRecipe.getVegetarian()).isFalse();
        assertThat(existingRecipe.getServings()).isEqualTo(4);
        verify(recipeRepository, times(1)).save(existingRecipe);
    }

    @Test
    void updateRecipe_WithEmptyIngredientsList_ShouldUpdateWithEmptyList() {
        Recipe existingRecipe = Recipe.builder()
                .id(1L)
                .title("Existing Recipe")
                .build();
        existingRecipe.setIngredientNames(Arrays.asList("Existing Ingredient"));

        Recipe updateData = Recipe.builder()
                .title("Updated Recipe")
                .build();
        updateData.setIngredientNames(Collections.emptyList());

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

        recipeService.updateRecipe(1L, updateData);

        assertThat(existingRecipe.getIngredientNames()).isEmpty();
        verify(recipeRepository, times(1)).save(existingRecipe);
    }

    @Test
    void createRecipe_WithNullIngredients_ShouldCreateRecipe() {
        Recipe newRecipe = Recipe.builder()
                .title("New Recipe")
                .description("New Description")
                .instructions("New Instructions")
                .vegetarian(true)
                .servings(2)
                .build();
        // Don't set ingredients (null)

        when(recipeRepository.save(any(Recipe.class))).thenReturn(newRecipe);

        Recipe result = recipeService.createRecipe(newRecipe);

        assertThat(result).isEqualTo(newRecipe);
        verify(recipeRepository, times(1)).save(newRecipe);
    }

    @Test
    void deleteRecipe_WithValidId_ShouldDeleteRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));

        recipeService.deleteRecipe(1L);

        verify(recipeRepository, times(1)).findById(1L);
        verify(recipeRepository, times(1)).delete(testRecipe);
    }

    @Test
    void getRecipeById_WithNonExistentId_ShouldThrowResourceNotFoundException() {
        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.getRecipeById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recipe not found with id: 999");

        verify(recipeRepository, times(1)).findById(999L);
    }

    @Test
    void updateRecipe_WithNonExistentId_ShouldThrowResourceNotFoundException() {
        Recipe updateData = Recipe.builder()
                .title("Updated Recipe")
                .build();

        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(999L, updateData))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recipe not found with id: 999");

        verify(recipeRepository, times(1)).findById(999L);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void deleteRecipe_WithNonExistentId_ShouldThrowResourceNotFoundException() {
        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteRecipe(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recipe not found with id: 999");

        verify(recipeRepository, times(1)).findById(999L);
        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

    @Test
    void searchRecipes_WithVeryLargeServingsNumber_ShouldCallRepository() {
        Integer largeServings = Integer.MAX_VALUE;
        List<Recipe> expectedRecipes = Collections.emptyList();
        when(recipeRepository.searchRecipes(null, largeServings, null, null, null))
                .thenReturn(expectedRecipes);

        List<Recipe> results = recipeService.searchRecipes(null, largeServings, null, null, null);

        assertThat(results).isEmpty();
        verify(recipeRepository, times(1)).searchRecipes(null, largeServings, null, null, null);
    }

    @Test
    void searchRecipes_WithZeroServings_ShouldCallRepository() {
        Integer zeroServings = 0;
        List<Recipe> expectedRecipes = Collections.emptyList();
        when(recipeRepository.searchRecipes(null, zeroServings, null, null, null))
                .thenReturn(expectedRecipes);

        List<Recipe> results = recipeService.searchRecipes(null, zeroServings, null, null, null);

        assertThat(results).isEmpty();
        verify(recipeRepository, times(1)).searchRecipes(null, zeroServings, null, null, null);
    }
}