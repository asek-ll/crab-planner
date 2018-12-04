package com.appspont.sopplet.crab.planner.ingredient;

public abstract class PlannerIngredientStack {
    protected final PlannerIngredient ingredient;

    protected PlannerIngredientStack(PlannerIngredient ingredient) {
        this.ingredient = ingredient;
    }

    public PlannerIngredient getIngredient() {
        return ingredient;
    }

    public abstract int getAmount();
}
