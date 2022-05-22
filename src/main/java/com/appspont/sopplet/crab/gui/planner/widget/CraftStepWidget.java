package com.appspont.sopplet.crab.gui.planner.widget;

import com.appspont.sopplet.crab.gui.planner.DrawContext;
import com.appspont.sopplet.crab.planner.CraftingRecipe;
import com.appspont.sopplet.crab.planner.PlannerRecipe;
import com.appspont.sopplet.crab.gui.planner.renderer.IngredientRenderer;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class CraftStepWidget extends RectangleWidget implements INestedGuiEventHandler {
    private final CraftingRecipe recipe;
    private final IngredientRenderer ingredientRenderer;
    private final Button removeButton;

    public CraftStepWidget(CraftingRecipe recipe,
                           IngredientRenderer ingredientRenderer) {
        this.recipe = recipe;
        this.ingredientRenderer = ingredientRenderer;
        removeButton = new Button(rect.x + 4, rect.y + 4, 20, 20, new StringTextComponent("x"), btn -> recipe.remove());
    }

    @Override
    public void draw(DrawContext context) {
        int x = rect.x;
        removeButton.x = x;
        removeButton.y = rect.y;
        removeButton.render(context.ms, context.mouseX, context.mouseY, context.partialTicks);

        x += 24;

        String count = IngredientRenderer.formatItemAmount(recipe.getCount());
        mc.font.drawShadow(context.ms, count, x + 1, rect.y + 5, Color.white.getRGB());

        if (recipe.getCount() > 10_000 &&
                context.mouseX > rect.x + 24 &&
                context.mouseX < rect.x + 48 &&
                context.mouseY > rect.y &&
                context.mouseY < rect.y + 20
        ) {
            context.hoverText = ImmutableList.of(new StringTextComponent(String.valueOf(recipe.getCount())));
        }


        x += 24;

        for (PlannerIngredientStack itemStack : recipe.getRecipe().getResult()) {
            ingredientRenderer.render(x, rect.y, itemStack, context);
            x += 18;
        }
        x += 18;

        if (!recipe.getRecipe().getCatalysts().isEmpty()) {
            for (PlannerIngredientStack itemStack : recipe.getRecipe().getCatalysts()) {
                ingredientRenderer.render(x, rect.y, itemStack, context);
                x += 18;
            }
            x += 18;
        }

        for (PlannerIngredientStack itemStack : recipe.getRecipe().getIngredients()) {
            ingredientRenderer.render(x, rect.y, itemStack, context);
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
    public List<? extends IGuiEventListener> children() {
        return Arrays.asList(removeButton);
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean p_231037_1_) {

    }

    @Nullable
    @Override
    public IGuiEventListener getFocused() {
        return null;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener p_231035_1_) {

    }
}
