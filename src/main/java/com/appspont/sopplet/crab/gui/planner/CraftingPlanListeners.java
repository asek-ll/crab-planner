package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.CraftingRecipe;
import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;

import java.util.List;

public interface CraftingPlanListeners {
    void updateCraftingSteps(List<CraftingRecipe> recipes);

    void updateRequired(List<PlannerIngredientStack> stacks);

    void updateGoals(List<PlannerGoal> stacks);
}
