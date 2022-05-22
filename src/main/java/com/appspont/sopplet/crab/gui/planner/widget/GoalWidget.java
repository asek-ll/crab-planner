package com.appspont.sopplet.crab.gui.planner.widget;

import com.appspont.sopplet.crab.gui.planner.DrawContext;
import com.appspont.sopplet.crab.gui.planner.renderer.IngredientRenderer;
import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class GoalWidget extends RectangleWidget {

    private final IngredientRenderer ingredientRenderer;
    private final PlannerGoal goal;
    private final Button removeButton;
    private final Button increaseCount;
    private final Button decreaseCOunt;

    public GoalWidget(PlannerGoal goal, IngredientRenderer ingredientRenderer) {
        this.ingredientRenderer = ingredientRenderer;
        this.goal = goal;
        removeButton = new Button(rect.x + 4, rect.y + 4, 20, 20, new StringTextComponent("x"), btn -> goal.remove());
        increaseCount = new Button(rect.x + 4, rect.y + 4, 20, 20, new StringTextComponent("+"), btn -> goal.setAmount(goal.getAmount() + 1));
        decreaseCOunt = new Button(rect.x + 4, rect.y + 4, 20, 20, new StringTextComponent("-"), btn -> goal.setAmount(goal.getAmount() - 1));
    }

    @Override
    public void draw(DrawContext context) {
        int x = rect.x;

        removeButton.y = rect.y;
        removeButton.x = x;
        removeButton.render(context.ms, context.mouseX, context.mouseY, context.partialTicks);

        x += 24;


        ingredientRenderer.render(x, rect.y, goal.getIngredientStack(), context);

        x += 24;

        increaseCount.y = rect.y;
        increaseCount.x = x;
        increaseCount.render(context.ms, context.mouseX, context.mouseY, context.partialTicks);

        x += 20;

        decreaseCOunt.y = rect.y;
        decreaseCOunt.x = x;
        decreaseCOunt.render(context.ms, context.mouseX, context.mouseY, context.partialTicks);
    }
}
