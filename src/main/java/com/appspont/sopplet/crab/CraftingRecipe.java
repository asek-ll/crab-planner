package com.appspont.sopplet.crab;

public class CraftingRecipe {
    private final PlannerRecipe recipe;
    private final PlannerContainer parent;
    private int count = 1;

    public CraftingRecipe(PlannerRecipe recipe, PlannerContainer plannerContainer) {
        this.recipe = recipe;
        this.parent = plannerContainer;
    }

    public PlannerRecipe getRecipe() {
        return recipe;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void remove() {
        if (parent != null) {
            parent.removeRecipe(this);
        }
    }
}
