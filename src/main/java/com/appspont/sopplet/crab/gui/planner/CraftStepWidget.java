package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.CraftingRecipe;
import com.appspont.sopplet.crab.PlannerRecipe;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public class CraftStepWidget extends RectangleWidget {
    private final CraftingRecipe recipe;
    private final IngredientRenderer ingredientRenderer;
    private final GuiButton removeButton;

    public CraftStepWidget(CraftingRecipe recipe,
                           IngredientRenderer ingredientRenderer) {
        this.recipe = recipe;
        this.ingredientRenderer = ingredientRenderer;
        removeButton = new GuiButton(1, x + 4, y + 4, 20, 20, "x");
    }

    @Override
    public void draw(DrawContext context) {
        int x = this.x;
        removeButton.x = x;
        removeButton.y = y;
        removeButton.drawButton(mc, context.mouseX, context.mouseY, context.partialTicks);

        x += 24;

        mc.fontRenderer.drawStringWithShadow(String.valueOf(recipe.getCount()), x + 1, y + 5, Color.white.getRGB());

        x += 18;

        for (PlannerIngredientStack itemStack : recipe.getRecipe().getResult()) {
            ingredientRenderer.render(x, y, itemStack, context);
            x += 18;
        }
        x += 18;
        for (PlannerIngredientStack itemStack : recipe.getRecipe().getIngredients()) {
            ingredientRenderer.render(x, y, itemStack, context);
            x += 18;
        }
    }

    private PlannerIngredientStack getiItemForIndex(int index) {
        final PlannerRecipe recipe = this.recipe.getRecipe();

        if (index < recipe.getResult().size()) {
            return recipe.getResult().get(index);
        }
        index -= recipe.getResult().size() + 1;
        if (index >= 0 && index < recipe.getIngredients().size()) {
            return recipe.getIngredients().get(index);
        }
        return null;
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        if (removeButton.isMouseOver()) {
            recipe.remove();
            return true;
        }
        return false;
    }
}
