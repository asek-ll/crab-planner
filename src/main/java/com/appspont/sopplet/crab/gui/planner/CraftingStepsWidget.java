package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.CraftingRecipe;
import net.minecraft.client.Minecraft;

import java.util.List;

public class CraftingStepsWidget extends ListWidget<CraftStepWidget> {
    private final IngredientRenderer ingredientRenderer;

    public CraftingStepsWidget(IngredientRenderer ingredientRenderer, Minecraft mc) {
        super(mc, 0, 0, 0, 0, 20);
        this.ingredientRenderer = ingredientRenderer;
    }

    public void setRecipes(List<CraftingRecipe> recipes) {
        clearEntries();
        for (CraftingRecipe recipe : recipes) {
            final CraftStepWidget craftStep = new CraftStepWidget(recipe, ingredientRenderer);
            addEntry(craftStep);
        }
    }
}
