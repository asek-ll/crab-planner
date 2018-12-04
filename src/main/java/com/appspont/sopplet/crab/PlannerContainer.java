package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.gui.planner.PlannerContainerListener;
import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredient;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerItemStack;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PlannerContainer extends Container {
    private final List<PlannerGoal> goals = Lists.newArrayList();
    private final List<CraftingRecipe> recipes = Lists.newArrayList();
    private final List<PlannerContainerListener> listeners = Lists.newArrayList();

    private final List<PlannerIngredientStack> required = Lists.newArrayList();

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return false;
    }

    public void addGoal(ItemStack stack) {
        goals.add(new PlannerGoal(new PlannerItemStack(stack), this));
        updateGoals();
        recalc();
    }

    public void addRecipe(List<PlannerIngredientStack> outputs, List<PlannerIngredientStack> ingredients) {
        final CraftingRecipe recipe = new CraftingRecipe(new PlannerRecipe(outputs, ingredients), this);
        recipes.add(recipe);
        recalc();
    }

    public List<PlannerGoal> getGoals() {
        return goals;
    }

    public List<CraftingRecipe> getRecipes() {
        return recipes;
    }

    private void invokeListener(Consumer<PlannerContainerListener> action) {
        listeners.forEach(action);
    }

    public void addListener(PlannerContainerListener listener) {
        listeners.add(listener);
    }

    private void recalc() {
        final Multimap<PlannerIngredient, PlannerRecipe> itemProducers = HashMultimap.create();

        final Map<PlannerRecipe, CraftingRecipe> craftingRecipeByRecipe = Maps.newHashMap();
        final List<PlannerRecipe> recipes = Lists.newArrayList();
        for (CraftingRecipe recipe : Lists.reverse(this.recipes)) {
            craftingRecipeByRecipe.put(recipe.getRecipe(), recipe);
            recipes.add(recipe.getRecipe());
        }


        for (PlannerRecipe recipe : recipes) {
            for (PlannerIngredientStack plannerItemStack : recipe.getResult()) {
                itemProducers.put(plannerItemStack.getIngredient(), recipe);
            }
        }

        final Multimap<PlannerRecipe, PlannerRecipe> deps = HashMultimap.create();
        for (PlannerRecipe recipe : recipes) {
            for (PlannerIngredientStack ingredient : recipe.getIngredients()) {
                deps.putAll(recipe, itemProducers.get(ingredient.getIngredient()));
            }
        }

        final List<PlannerRecipe> processedRecipes = Lists.newArrayList();
        final List<PlannerRecipe> recipesToProcess = Lists.newArrayList(recipes);

        while (!recipesToProcess.isEmpty()) {
            for (PlannerRecipe recipe : recipesToProcess) {
                final Collection<PlannerRecipe> dependent = deps.get(recipe);
                if (processedRecipes.containsAll(dependent)) {
                    processedRecipes.add(recipe);
                }
            }
            recipesToProcess.removeAll(processedRecipes);
        }

        final Map<PlannerIngredient, Integer> itemCount = Maps.newHashMap();
        for (PlannerGoal goal : goals) {
            itemCount.put(goal.getIngredient(),
                    itemCount.getOrDefault(goal.getIngredient(), 0) - goal.getAmount());
        }

        this.recipes.clear();
        for (PlannerRecipe processedRecipe : Lists.reverse(processedRecipes)) {
            int repeat = 0;
            for (PlannerIngredientStack result : processedRecipe.getResult()) {
                final Integer needItemsCount = itemCount.getOrDefault(result.getIngredient(), 0);
                if (needItemsCount < 0) {
                    final double rate = (double) needItemsCount / result.getAmount();
                    repeat = Math.max(repeat, (int) Math.ceil(-rate));
                }
            }
            final CraftingRecipe craftingRecipe = craftingRecipeByRecipe.get(processedRecipe);
            craftingRecipe.setCount(repeat);
            this.recipes.add(craftingRecipe);

            for (PlannerIngredientStack result : processedRecipe.getResult()) {
                final Integer count = itemCount.getOrDefault(result.getIngredient(), 0);
                itemCount.put(result.getIngredient(), count + result.getAmount() * repeat);
            }
            for (PlannerIngredientStack ingredient : processedRecipe.getIngredients()) {
                final Integer count = itemCount.getOrDefault(ingredient.getIngredient(), 0);
                itemCount.put(ingredient.getIngredient(), count - ingredient.getAmount() * repeat);
            }
        }

        required.clear();

        for (PlannerIngredient plannerItemStack : itemCount.keySet()) {
            final Integer count = itemCount.get(plannerItemStack);
            if (count < 0) {
                final PlannerIngredientStack copy = plannerItemStack.createStack(-count);
                required.add(copy);
            }
        }


        invokeListener(l -> l.updateRequired(required));
        invokeListener(l -> l.updateCraftingSteps(this.recipes));
    }

    public void removeGoal(PlannerGoal source) {
        goals.remove(source);
        updateGoals();
        recalc();
    }

    public void removeRecipe(CraftingRecipe craftingRecipe) {
        recipes.remove(craftingRecipe);
        recalc();
    }

    private void updateGoals() {
        for (PlannerContainerListener listener : listeners) {
            listener.updateGoals(goals);
        }
    }

    public void setGoalAmount(PlannerGoal plannerGoal, int amount) {
        final int i = goals.indexOf(plannerGoal);
        if (i >= 0) {
            goals.remove(i);
            final PlannerGoal newGoal = new PlannerGoal(plannerGoal.getIngredient().createStack(amount), this);
            goals.add(i, newGoal);
            updateGoals();
            recalc();
        }
    }


    public void save() {

    }
}
