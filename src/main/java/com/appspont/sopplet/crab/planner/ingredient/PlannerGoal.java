package com.appspont.sopplet.crab.planner.ingredient;

import com.appspont.sopplet.crab.planner.CraftingPlan;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class PlannerGoal {
    private final PlannerIngredientStack stack;
    private final CraftingPlan parent;

    public PlannerGoal(PlannerIngredientStack stack, CraftingPlan parent) {
        this.stack = stack;
        this.parent = parent;
    }

    public PlannerIngredient<?> getIngredient() {
        return stack.getIngredient();
    }

    public int getAmount() {
        return stack.getAmount();
    }

    public PlannerIngredientStack getIngredientStack() {
        return stack;
    }

    public void remove() {
        if (parent != null) {
            parent.removeGoal(this);
        }
    }

    public void setAmount(int amount) {
        if (amount > 0) {
            parent.setGoalAmount(this, amount);
        }
    }

    public static class JsonHelper implements JsonSerializer<PlannerGoal> {

        @Override
        public JsonElement serialize(PlannerGoal src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("stack", context.serialize(src.stack, PlannerIngredientStack.class));
            return jsonObject;
        }
    }
}
