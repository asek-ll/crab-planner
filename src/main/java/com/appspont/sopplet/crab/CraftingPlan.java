package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.gui.planner.CraftingPlanListeners;
import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredient;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CraftingPlan {
    private String name = "Plan";
    private final List<PlannerGoal> goals = Lists.newArrayList();
    private final List<CraftingRecipe> recipes = Lists.newArrayList();
    private final List<PlannerIngredientStack> required = Lists.newArrayList();

    private final List<CraftingPlanListeners> listeners = Lists.newArrayList();

    public void addGoal(PlannerIngredientStack stack) {
        goals.add(new PlannerGoal(stack, this));
        updateGoals();
        recalc();
    }

    public void addRecipe(PlannerRecipe plannerRecipe) {
        final CraftingRecipe recipe = new CraftingRecipe(plannerRecipe, this);
        recipes.add(recipe);
        recalc();
    }

    public List<PlannerGoal> getGoals() {
        return goals;
    }

    public List<CraftingRecipe> getRecipes() {
        return recipes;
    }

    private void invokeListener(Consumer<CraftingPlanListeners> action) {
        listeners.forEach(action);
    }

    public void addListener(CraftingPlanListeners listener) {
        listeners.clear();
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
            boolean locked = true;
            for (PlannerRecipe recipe : recipesToProcess) {
                final Collection<PlannerRecipe> dependent = deps.get(recipe);
                if (processedRecipes.containsAll(dependent)) {
                    processedRecipes.add(recipe);
                    locked = false;
                }
            }
            recipesToProcess.removeAll(processedRecipes);
            if (locked) {
                break;
            }
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
        for (CraftingPlanListeners listener : listeners) {
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

    public List<PlannerIngredientStack> getRequired() {
        return required;
    }

    public void save() {

    }

    public void removeListener(CraftingPlanListeners listener) {
        listeners.remove(listener);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static class JsonHelper implements JsonSerializer<CraftingPlan>, JsonDeserializer<CraftingPlan> {

        @Override
        public CraftingPlan deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final CraftingPlan craftingPlan = new CraftingPlan();

            final JsonArray goalElements = jsonObject.getAsJsonArray("goals");
            final ArrayList<PlannerGoal> goals = new ArrayList<>(goalElements.size());

            for (JsonElement goalsElement : goalElements) {
                final PlannerIngredientStack stack = context.deserialize(goalsElement.getAsJsonObject().get("stack"), PlannerIngredientStack.class);
                goals.add(new PlannerGoal(stack, craftingPlan));
            }

            craftingPlan.goals.addAll(goals);

            final JsonArray recipeElements = jsonObject.getAsJsonArray("recipes");
            final ArrayList<CraftingRecipe> recipes = new ArrayList<>(recipeElements.size());

            for (JsonElement recipeElement : recipeElements) {
                final JsonObject recipeObject = recipeElement.getAsJsonObject();
                final PlannerRecipe recipe = context.deserialize(recipeObject.get("recipe"), PlannerRecipe.class);
                final int count = recipeObject.get("count").getAsInt();

                final CraftingRecipe craftingRecipe = new CraftingRecipe(recipe, craftingPlan);
                craftingRecipe.setCount(count);
                recipes.add(craftingRecipe);
            }

            craftingPlan.recipes.addAll(recipes);

            final List<PlannerIngredientStack> required = StackUtils.fromJsonArray(jsonObject.getAsJsonArray("required"), PlannerIngredientStack.class, context);

            craftingPlan.required.addAll(required);

            return craftingPlan;
        }

        @Override
        public JsonElement serialize(CraftingPlan src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("goals", StackUtils.toJsonArray(src.goals, PlannerGoal.class, context));
            jsonObject.add("recipes", StackUtils.toJsonArray(src.recipes, CraftingRecipe.class, context));
            jsonObject.add("required", StackUtils.toJsonArray(src.required, PlannerIngredientStack.class, context));
            return jsonObject;
        }
    }
}
