package com.appspont.sopplet.crab.gui.planner.widget;

import com.appspont.sopplet.crab.gui.planner.DrawContext;
import com.appspont.sopplet.crab.gui.planner.renderer.IngredientRenderer;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.jei.CrabJeiPlugin;
import com.google.common.collect.Lists;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.List;

public class RequiredItemsWidget extends RectangleWidget {
    private final IngredientRenderer ingredientRenderer;
    private List<PlannerIngredientStack> items = Lists.newArrayList();

    public RequiredItemsWidget(IngredientRenderer ingredientRenderer) {
        this.ingredientRenderer = ingredientRenderer;
    }

    @Override
    public void draw(DrawContext context) {


        int size = (rect.width / 18);
        int i = 0;
        for (PlannerIngredientStack itemStack : items) {
            int y = i / size;
            int x = i % size;
            ingredientRenderer.render(rect.x + x * 18, rect.y + y * 18, itemStack, context);
            i += 1;
        }
    }

    public void setItems(List<PlannerIngredientStack> items) {
        this.items = items;
    }

    private PlannerIngredientStack getiItemForIndex(int x, int y) {
        int i = (x - rect.x) / 18;
        int j = (y - rect.y) / 18;
        int index = (rect.width / 18) * j + i;
        if (index < items.size() && index >= 0) {
            return items.get(index);
        }
        return null;
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        final PlannerIngredientStack item = getiItemForIndex(x, y);
        if (item != null) {
            final IJeiRuntime jeiRuntime = CrabJeiPlugin.getJeiRuntime();
            final IFocus<Object> focus = jeiRuntime.getRecipeManager().createFocus(IFocus.Mode.OUTPUT, item.getIngredient().getRawStack());
            jeiRuntime.getRecipesGui().show(focus);
            return true;
        }
        return false;
    }
}