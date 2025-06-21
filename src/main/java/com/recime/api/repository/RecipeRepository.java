package com.recime.api.repository;

import com.recime.api.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {


    @Query("SELECT DISTINCT r FROM Recipe r LEFT JOIN r.ingredients i " +
           "WHERE (:vegetarian IS NULL OR r.vegetarian = :vegetarian) " +
           "AND (:servings IS NULL OR r.servings = :servings) " +
           "AND (:includeIngredients IS NULL OR EXISTS (SELECT 1 FROM r.ingredients ing WHERE LOWER(ing.name) IN (:includeIngredients))) " +
           "AND (:excludeIngredients IS NULL OR NOT EXISTS (SELECT 1 FROM r.ingredients ing2 WHERE LOWER(ing2.name) IN (:excludeIngredients)))" +
            "AND (:contentToFilter IS NULL OR r.instructions LIKE CONCAT('%', :contentToFilter, '%'))")
    List<Recipe> searchRecipes(@Param("vegetarian") Boolean vegetarian,
                              @Param("servings") Integer servings,
                              @Param("includeIngredients") List<String> includeIngredients,
                              @Param("excludeIngredients") List<String> excludeIngredients,
                               @Param("contentToFilter") String contentToFilter);
}