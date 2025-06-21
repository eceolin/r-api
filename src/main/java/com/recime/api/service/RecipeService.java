package com.recime.api.service;

import com.recime.api.entity.Recipe;
import com.recime.api.exception.ResourceNotFoundException;
import com.recime.api.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    
    public Recipe createRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    @Transactional(readOnly = true)
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    
    public Recipe updateRecipe(Long id, Recipe recipeDetails) {
        Recipe recipe = getRecipeById(id);
        
        recipe.setTitle(recipeDetails.getTitle());
        recipe.setDescription(recipeDetails.getDescription());
        recipe.setIngredientNames(recipeDetails.getIngredientNames());
        recipe.setInstructions(recipeDetails.getInstructions());
        recipe.setVegetarian(recipeDetails.getVegetarian());
        recipe.setServings(recipeDetails.getServings());
        
        return recipeRepository.save(recipe);
    }
    
    public void deleteRecipe(Long id) {
        Recipe recipe = getRecipeById(id);
        recipeRepository.delete(recipe);
    }
    
    @Transactional(readOnly = true)
    public List<Recipe> searchRecipes(Boolean vegetarian, Integer servings, 
                                     List<String> includeIngredients,
                                     List<String> excludeIngredients,
                                      String contentToFilter) {
        
        return recipeRepository.searchRecipes(vegetarian, servings, includeIngredients, excludeIngredients, contentToFilter);
    }
}