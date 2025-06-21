package com.recime.api.repository;

import com.recime.api.entity.Ingredient;
import com.recime.api.entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RecipeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RecipeRepository recipeRepository;

    private Recipe vegetarianRecipe;
    private Recipe nonVegetarianRecipe;
    private Recipe pastaRecipe;

    @BeforeEach
    void setUp() {
        // Create test recipes
        vegetarianRecipe = Recipe.builder()
                .title("Vegetable Pasta")
                .description("Delicious vegetable pasta")
                .instructions("Boil pasta, add vegetables, season well")
                .vegetarian(true)
                .servings(4)
                .build();

        nonVegetarianRecipe = Recipe.builder()
                .title("Beef Stew")
                .description("Hearty beef stew")
                .instructions("Brown beef, add vegetables, simmer for hours")
                .vegetarian(false)
                .servings(6)
                .build();

        pastaRecipe = Recipe.builder()
                .title("Spaghetti Carbonara")
                .description("Classic Italian pasta")
                .instructions("Cook spaghetti, mix with eggs and cheese")
                .vegetarian(false)
                .servings(2)
                .build();

        // Save recipes
        vegetarianRecipe = entityManager.persistAndFlush(vegetarianRecipe);
        nonVegetarianRecipe = entityManager.persistAndFlush(nonVegetarianRecipe);
        pastaRecipe = entityManager.persistAndFlush(pastaRecipe);

        // Add ingredients
        addIngredient(vegetarianRecipe, "Pasta");
        addIngredient(vegetarianRecipe, "Tomato");
        addIngredient(vegetarianRecipe, "Basil");

        addIngredient(nonVegetarianRecipe, "Beef");
        addIngredient(nonVegetarianRecipe, "Potato");
        addIngredient(nonVegetarianRecipe, "Carrot");

        addIngredient(pastaRecipe, "Spaghetti");
        addIngredient(pastaRecipe, "Egg");
        addIngredient(pastaRecipe, "Cheese");

        entityManager.flush();
    }

    private void addIngredient(Recipe recipe, String ingredientName) {
        Ingredient ingredient = Ingredient.builder()
                .name(ingredientName)
                .recipe(recipe)
                .build();
        entityManager.persist(ingredient);
    }

    @Test
    void searchRecipes_WithAllNullParameters_ShouldReturnAllRecipes() {
        List<Recipe> results = recipeRepository.searchRecipes(null, null, null, null, null);
        
        assertThat(results).hasSize(3);
        assertThat(results).containsExactlyInAnyOrder(vegetarianRecipe, nonVegetarianRecipe, pastaRecipe);
    }

    @Test
    void searchRecipes_WithVegetarianTrue_ShouldReturnOnlyVegetarianRecipes() {
        List<Recipe> results = recipeRepository.searchRecipes(true, null, null, null, null);
        
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(vegetarianRecipe);
    }

    @Test
    void searchRecipes_WithVegetarianFalse_ShouldReturnOnlyNonVegetarianRecipes() {
        List<Recipe> results = recipeRepository.searchRecipes(false, null, null, null, null);
        
        assertThat(results).hasSize(2);
        assertThat(results).containsExactlyInAnyOrder(nonVegetarianRecipe, pastaRecipe);
    }

    @Test
    void searchRecipes_WithServings_ShouldReturnRecipesWithExactServings() {
        List<Recipe> results = recipeRepository.searchRecipes(null, 4, null, null, null);
        
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(vegetarianRecipe);
    }

    @Test
    void searchRecipes_WithIncludeIngredients_ShouldReturnRecipesContainingIngredient() {
        List<String> includeIngredients = Arrays.asList("pasta");
        List<Recipe> results = recipeRepository.searchRecipes(null, null, includeIngredients, null, null);
        
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(vegetarianRecipe);
    }

    @Test
    void searchRecipes_WithIncludeIngredients_CaseInsensitive_ShouldReturnRecipes() {
        List<String> includeIngredients = Arrays.asList("PASTA");
        List<Recipe> results = recipeRepository.searchRecipes(null, null, includeIngredients, null, null);
        
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(vegetarianRecipe);
    }

    @Test
    void searchRecipes_WithMultipleIncludeIngredients_ShouldReturnRecipesContainingAnyIngredient() {
        List<String> includeIngredients = Arrays.asList("beef", "egg");
        List<Recipe> results = recipeRepository.searchRecipes(null, null, includeIngredients, null, null);
        
        assertThat(results).hasSize(2);
        assertThat(results).containsExactlyInAnyOrder(nonVegetarianRecipe, pastaRecipe);
    }

    @Test
    void searchRecipes_WithExcludeIngredients_ShouldReturnRecipesNotContainingIngredient() {
        List<String> excludeIngredients = Arrays.asList("beef");
        List<Recipe> results = recipeRepository.searchRecipes(null, null, null, excludeIngredients, null);
        
        assertThat(results).hasSize(2);
        assertThat(results).containsExactlyInAnyOrder(vegetarianRecipe, pastaRecipe);
    }

    @Test
    void searchRecipes_WithExcludeIngredients_CaseInsensitive_ShouldReturnRecipes() {
        List<String> excludeIngredients = Arrays.asList("BEEF");
        List<Recipe> results = recipeRepository.searchRecipes(null, null, null, excludeIngredients, null);
        
        assertThat(results).hasSize(2);
        assertThat(results).containsExactlyInAnyOrder(vegetarianRecipe, pastaRecipe);
    }

    @Test
    void searchRecipes_WithContentToFilter_ShouldReturnRecipesWithMatchingInstructions() {
        List<Recipe> results = recipeRepository.searchRecipes(null, null, null, null, "pasta");
        
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(vegetarianRecipe);
    }

    @Test
    void searchRecipes_WithContentToFilter_CaseInsensitive_ShouldReturnRecipes() {
        List<Recipe> results = recipeRepository.searchRecipes(null, null, null, null, "PASTA");
        
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(vegetarianRecipe);
    }

    @Test
    void searchRecipes_WithIncludeAndExcludeIngredients_ShouldApplyBothFilters() {
        List<String> includeIngredients = Arrays.asList("pasta");
        List<String> excludeIngredients = Arrays.asList("beef");
        List<Recipe> results = recipeRepository.searchRecipes(null, null, includeIngredients, excludeIngredients, null);
        
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(vegetarianRecipe);
    }

    @Test
    void searchRecipes_WithAllFilters_ShouldApplyAllFilters() {
        List<String> includeIngredients = Arrays.asList("pasta");
        List<String> excludeIngredients = Arrays.asList("beef");
        List<Recipe> results = recipeRepository.searchRecipes(true, 4, includeIngredients, excludeIngredients, "Boil");
        
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(vegetarianRecipe);
    }

    @Test
    void searchRecipes_WithRestrictiveFilters_ShouldReturnEmptyList() {
        List<String> includeIngredients = Arrays.asList("nonexistent");
        List<Recipe> results = recipeRepository.searchRecipes(null, null, includeIngredients, null, null);
        
        assertThat(results).isEmpty();
    }

    @Test
    void searchRecipes_WithEmptyIncludeIngredients_ShouldReturnAllRecipes() {
        List<String> includeIngredients = Collections.emptyList();
        List<Recipe> results = recipeRepository.searchRecipes(null, null, includeIngredients, null, null);
        
        assertThat(results).hasSize(3);
    }

    @Test
    void searchRecipes_WithEmptyExcludeIngredients_ShouldReturnAllRecipes() {
        List<String> excludeIngredients = Collections.emptyList();
        List<Recipe> results = recipeRepository.searchRecipes(null, null, null, excludeIngredients, null);
        
        assertThat(results).hasSize(3);
    }

    @Test
    void searchRecipes_WithConflictingFilters_ShouldRespectExclusion() {
        List<String> includeIngredients = Arrays.asList("pasta");
        List<String> excludeIngredients = Arrays.asList("pasta");
        List<Recipe> results = recipeRepository.searchRecipes(null, null, includeIngredients, excludeIngredients, null);
        
        assertThat(results).isEmpty();
    }

    @Test
    void searchRecipes_WithPartialIngredientMatch_ShouldReturnRecipes() {
        List<String> includeIngredients = Arrays.asList("past");
        List<Recipe> results = recipeRepository.searchRecipes(null, null, includeIngredients, null, null);
        
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(vegetarianRecipe);
    }
}