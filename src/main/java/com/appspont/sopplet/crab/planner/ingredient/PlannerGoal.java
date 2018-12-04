package com.appspont.sopplet.crab.planner.ingredient;

import com.appspont.sopplet.crab.PlannerContainer;

public class PlannerGoal {
    private final PlannerIngredientStack stack;
    private final PlannerContainer parent;

    public PlannerGoal(PlannerIngredientStack stack, PlannerContainer parent) {
        this.stack = stack;
        this.parent = parent;
    }

    public PlannerIngredient getIngredient() {
        return stack.getIngredient();
    }

    public int getAmount() {
        return stack.getAmount();
    }

    public PlannerIngredientStack getIngredientStack() {
        return stack;
    }

    public void remove() {
        if (parent != null) {
            parent.removeGoal(this);
        }
    }

    public void setAmount(int amount) {
        if (amount > 0) {
            parent.setGoalAmount(this, amount);
        }
    }
}
