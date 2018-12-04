package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;

import java.util.List;
import java.util.Objects;

public class PlannerRecipe {
    private final List<PlannerIngredientStack> result;
    private final List<PlannerIngredientStack> ingredients;

    public PlannerRecipe(List<PlannerIngredientStack> result, List<PlannerIngredientStack> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public List<PlannerIngredientStack> getResult() {
        return result;
    }

    public List<PlannerIngredientStack> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlannerRecipe recipe = (PlannerRecipe) o;
        return Objects.equals(result, recipe.result) &&
                Objects.equals(ingredients, recipe.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, ingredients);
    }
}
