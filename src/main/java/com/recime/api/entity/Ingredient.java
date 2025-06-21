package com.recime.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredients", indexes = {
    @Index(name = "idx_ingredient_name", columnList = "name"),
    @Index(name = "idx_ingredient_recipe_id", columnList = "recipe_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingredients_seq")
    @SequenceGenerator(name = "ingredients_seq", sequenceName = "ingredients_seq", allocationSize = 50)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    
    public Ingredient(String name, Recipe recipe) {
        this.name = name;
        this.recipe = recipe;
    }
}