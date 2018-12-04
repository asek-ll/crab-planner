package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.Lists;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.recipe.IFocus;

import java.util.List;

public class RequiredItemsWidget extends RectangleWidget {
    private final IngredientRenderer ingredientRenderer;
    private List<PlannerIngredientStack> items = Lists.newArrayList();

    public RequiredItemsWidget(IngredientRenderer ingredientRenderer) {
        this.ingredientRenderer = ingredientRenderer;
    }

    @Override
    public void draw(DrawContext context) {
        int x = this.x;
        for (PlannerIngredientStack itemStack : items) {
            ingredientRenderer.render(x, y, itemStack, context);
            x += 18;
        }
    }

    public void setItems(List<PlannerIngredientStack> items) {
        this.items = items;
    }

    private PlannerIngredientStack getiItemForIndex(int index) {
        if (index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        final PlannerIngredientStack item = getiItemForIndex(x / 18);
        if (item != null) {
            final IJeiRuntime jeiRuntime = CrabJeiPlugin.getJeiRuntime();
            final IFocus<Object> focus = jeiRuntime.getRecipeRegistry().createFocus(IFocus.Mode.OUTPUT, item.getIngredient().getRawStack());
            jeiRuntime.getRecipesGui().show(focus);
            return true;
        }
        return false;
    }
}
