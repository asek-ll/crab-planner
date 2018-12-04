package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import net.minecraft.client.gui.GuiButton;

public class GoalWidget extends RectangleWidget {

    private final IngredientRenderer ingredientRenderer;
    private final PlannerGoal goal;
    private final GuiButton removeButton;
    private final GuiButton increaseCount;
    private final GuiButton decreaseCOunt;

    public GoalWidget(PlannerGoal goal, IngredientRenderer ingredientRenderer) {
        this.ingredientRenderer = ingredientRenderer;
        this.goal = goal;
        removeButton = new GuiButton(1, x + 4, y + 4, 20, 20, "x");
        increaseCount = new GuiButton(1, x + 4, y + 4, 20, 20, "+");
        decreaseCOunt = new GuiButton(1, x + 4, y + 4, 20, 20, "-");
    }

    @Override
    public void draw(DrawContext context) {
        int x = this.x;

        removeButton.y = y;
        removeButton.x = x;
        removeButton.drawButton(mc, context.mouseX, context.mouseY, context.partialTicks);

        x += 24;


        ingredientRenderer.render(x, y, goal.getIngredientStack(), context);

        x += 24;

        increaseCount.y = y;
        increaseCount.x = x;
        increaseCount.drawButton(mc, context.mouseX, context.mouseY, context.partialTicks);

        x += 20;

        decreaseCOunt.y = y;
        decreaseCOunt.x = x;
        decreaseCOunt.drawButton(mc, context.mouseX, context.mouseY, context.partialTicks);
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        if (removeButton.isMouseOver()) {
            goal.remove();
            return true;
        }
        if (increaseCount.isMouseOver()) {
            goal.setAmount(goal.getAmount() + 1);
        }
        if (decreaseCOunt.isMouseOver()) {
            goal.setAmount(goal.getAmount() - 1);
        }
        return false;
    }
}
