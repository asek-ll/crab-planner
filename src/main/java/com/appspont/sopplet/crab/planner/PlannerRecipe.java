package com.appspont.sopplet.crab.planner;

import com.appspont.sopplet.crab.StackUtils;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class PlannerRecipe {
    private final List<PlannerIngredientStack> result;
    private final List<PlannerIngredientStack> ingredients;
    private final List<PlannerIngredientStack> catalysts;

    public PlannerRecipe(List<PlannerIngredientStack> result, List<PlannerIngredientStack> ingredients, List<PlannerIngredientStack> catalysts) {
        this.result = result;
        this.ingredients = ingredients;
        this.catalysts = catalysts;
    }

    public List<PlannerIngredientStack> getResult() {
        return result;
    }

    public List<PlannerIngredientStack> getIngredients() {
        return ingredients;
    }

    public List<PlannerIngredientStack> getCatalysts() {
        return catalysts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlannerRecipe that = (PlannerRecipe) o;
        return Objects.equals(result, that.result) &&
                Objects.equals(ingredients, that.ingredients) &&
                Objects.equals(catalysts, that.catalysts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, ingredients, catalysts);
    }

    public static class JsonHelper implements JsonSerializer<PlannerRecipe>, JsonDeserializer<PlannerRecipe> {

        @Override
        public PlannerRecipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final List<PlannerIngredientStack> result = StackUtils.fromJsonArray(
                    jsonObject.getAsJsonArray("result"), PlannerIngredientStack.class, context);

            final List<PlannerIngredientStack> ingredients = StackUtils.fromJsonArray(
                    jsonObject.getAsJsonArray("ingredients"), PlannerIngredientStack.class, context);

            final List<PlannerIngredientStack> catalysts = StackUtils.fromJsonArray(
                    jsonObject.getAsJsonArray("catalysts"), PlannerIngredientStack.class, context);

            return new PlannerRecipe(result, ingredients, catalysts);
        }

        @Override
        public JsonElement serialize(PlannerRecipe src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("result", StackUtils.toJsonArray(src.result, PlannerIngredientStack.class, context));
            jsonObject.add("ingredients", StackUtils.toJsonArray(src.ingredients, PlannerIngredientStack.class, context));
            jsonObject.add("catalysts", StackUtils.toJsonArray(src.catalysts, PlannerIngredientStack.class, context));
            return jsonObject;
        }
    }
}
