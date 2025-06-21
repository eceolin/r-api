package com.recime.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipes_seq")
    @SequenceGenerator(name = "recipes_seq", sequenceName = "recipes_seq", allocationSize = 50)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Ingredient> ingredients = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String instructions;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean vegetarian = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer servings = 1;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods for backward compatibility
    public List<String> getIngredientNames() {
        if (this.ingredients == null) {
            return new ArrayList<>();
        }
        return ingredients.stream()
                .map(Ingredient::getName)
                .collect(Collectors.toList());
    }
    
    public void setIngredientNames(List<String> ingredientNames) {
        if (this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }
        this.ingredients.clear();
        if (ingredientNames != null) {
            ingredientNames.forEach(name -> {
                Ingredient ingredient = new Ingredient(name, this);
                this.ingredients.add(ingredient);
            });
        }
    }
    
    public void addIngredient(String name) {
        if (this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }
        this.ingredients.add(new Ingredient(name, this));
    }
    
    public void removeIngredient(String name) {
        if (this.ingredients != null) {
            this.ingredients.removeIf(ingredient -> ingredient.getName().equals(name));
        }
    }
}