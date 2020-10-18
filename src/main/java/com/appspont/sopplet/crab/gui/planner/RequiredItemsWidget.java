package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.Lists;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

public class RequiredItemsWidget extends RectangleWidget {
    private final IngredientRenderer ingredientRenderer;
    private List<PlannerIngredientStack> items = Lists.newArrayList();

    public RequiredItemsWidget(IngredientRenderer ingredientRenderer) {
        this.ingredientRenderer = ingredientRenderer;
    }

    @Override
    public void draw(DrawContext context) {


        int size = (this.width / 18);
        int i = 0;
        for (PlannerIngredientStack itemStack : items) {
            int y = i / size;
            int x = i % size;
            ingredientRenderer.render(this.x + x * 18, this.y + y * 18, itemStack, context);
            i += 1;
        }
    }

    public void setItems(List<PlannerIngredientStack> items) {
        this.items = items;
    }

    private PlannerIngredientStack getiItemForIndex(int x, int y) {
        int i = (x - this.x) / 18;
        int j = (y - this.y) / 18;
        int index = (this.width / 18) * j + i;
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
            final IFocus<Object> focus = jeiRuntime.getRecipeRegistry().createFocus(IFocus.Mode.OUTPUT, item.getIngredient().getRawStack());
            jeiRuntime.getRecipesGui().show(focus);
            return true;
        }
        return false;
    }
}
