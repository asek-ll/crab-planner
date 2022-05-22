package com.appspont.sopplet.crab.planner;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CraftingRecipe {
    private final PlannerRecipe recipe;
    private final CraftingPlan parent;
    private int count = 1;

    public CraftingRecipe(PlannerRecipe recipe, CraftingPlan plannerContainer) {
        this.recipe = recipe;
        this.parent = plannerContainer;
    }

    public PlannerRecipe getRecipe() {
        return recipe;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void remove() {
        if (parent != null) {
            parent.removeRecipe(this);
        }
    }

    public static class JsonHelper implements JsonSerializer<CraftingRecipe> {

        @Override
        public JsonElement serialize(CraftingRecipe src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("recipe", context.serialize(src.getRecipe(), PlannerRecipe.class));
            jsonObject.addProperty("count", src.getCount());
            return jsonObject;
        }
    }
}
