package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class PlannerRecipe {
    private final List<PlannerIngredientStack> result;
    private final List<PlannerIngredientStack> ingredients;

    public PlannerRecipe(List<PlannerIngredientStack> result, List<PlannerIngredientStack> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public List<PlannerIngredientStack> getResult() {
        return result;
    }

    public List<PlannerIngredientStack> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlannerRecipe recipe = (PlannerRecipe) o;
        return Objects.equals(result, recipe.result) &&
                Objects.equals(ingredients, recipe.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, ingredients);
    }

    public static class JsonHelper implements JsonSerializer<PlannerRecipe>, JsonDeserializer<PlannerRecipe> {

        @Override
        public PlannerRecipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final List<PlannerIngredientStack> result = StackUtils.fromJsonArray(
                    jsonObject.getAsJsonArray("result"), PlannerIngredientStack.class, context);
            final List<PlannerIngredientStack> ingredients = StackUtils.fromJsonArray(
                    jsonObject.getAsJsonArray("ingredients"), PlannerIngredientStack.class, context);

            return new PlannerRecipe(result, ingredients);
        }

        @Override
        public JsonElement serialize(PlannerRecipe src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("result", StackUtils.toJsonArray(src.result, PlannerIngredientStack.class, context));
            jsonObject.add("ingredients", StackUtils.toJsonArray(src.ingredients, PlannerIngredientStack.class, context));
            return jsonObject;
        }
    }
}
