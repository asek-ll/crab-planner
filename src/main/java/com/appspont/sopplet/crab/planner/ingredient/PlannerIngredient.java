package com.appspont.sopplet.crab.planner.ingredient;

public abstract class PlannerIngredient<T> {
    protected final T stack;

    protected PlannerIngredient(T stack) {
        this.stack = stack;
    }

    public abstract PlannerIngredientStack createStack(int amount);

    public T getRawStack() {
        return stack;
    }
}
