package com.appspont.sopplet.crab;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import java.util.List;

public class PlannerContainer extends Container {
    private final List<ItemStack> goals = Lists.newArrayList();
    private final List<Recipe> recipes = Lists.newArrayList();

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return false;
    }

    public void addGoal(ItemStack stack) {
        goals.add(stack);
    }

    public void addRecipe(List<ItemStack> outputs, List<ItemStack> ingredients) {
        recipes.add(new Recipe(outputs, ingredients));
    }

    public List<ItemStack> getGoals() {
        return goals;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public static class Recipe {
        private final List<ItemStack> result;
        private final List<ItemStack> ingredients;

        public Recipe(List<ItemStack> result, List<ItemStack> ingredients) {
            this.result = result;
            this.ingredients = ingredients;
        }

        public List<ItemStack> getResult() {
            return result;
        }

        public List<ItemStack> getIngredients() {
            return ingredients;
        }
    }
}
