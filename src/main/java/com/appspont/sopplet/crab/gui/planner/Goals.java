package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import net.minecraft.client.Minecraft;

import java.util.List;

public class Goals extends ListWidget<GoalWidget> {

    private final IngredientRenderer ingredientRenderer;

    public Goals(Minecraft mc, IngredientRenderer ingredientRenderer) {
        super(mc, 124, 96, 2, 100, 20);
        this.ingredientRenderer = ingredientRenderer;
    }

    public void setGoals(List<PlannerGoal> plannerGoals) {
        childWidgets.clear();
        for (PlannerGoal goal : plannerGoals) {
            final GoalWidget goalWidget = new GoalWidget(goal, ingredientRenderer);
            goalWidget.setSize(width, slotHeight);
            childWidgets.add(goalWidget);
        }
    }
}
