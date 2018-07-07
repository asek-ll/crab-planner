package com.appspont.sopplet.crab;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.ingredients.Ingredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RecipeExporter {

    private final File baseDir;

    public RecipeExporter() {
        baseDir = new File(".", "dumps");
        baseDir.mkdirs();
    }


    public void dumpRecipes(IRecipeRegistry recipeRegistry) {
        final List<IRecipeCategory> recipeCategories = recipeRegistry.getRecipeCategories();

        final JsonArray recipeCategoriesJson = new JsonArray();
        for (IRecipeCategory recipeCategory : recipeCategories) {
            final List<IRecipeWrapper> recipeWrappers = recipeRegistry.getRecipeWrappers(recipeCategory);
            final JsonArray recipes = new JsonArray();
            for (IRecipeWrapper recipeWrapper : recipeWrappers) {
                recipes.add(recipe2JsonObject(recipeWrapper));
            }

            final JsonObject recipeCategoryJson = new JsonObject();
            recipeCategoryJson.addProperty("title", recipeCategory.getTitle());
            recipeCategoryJson.addProperty("mod", recipeCategory.getModName());
            recipeCategoryJson.addProperty("mod", recipeCategory.getUid());
            recipeCategoryJson.add("recipes", recipes);

            recipeCategoriesJson.add(recipeCategoryJson);
        }

        writeToFile("recipes.json", recipeCategoriesJson.toString());
    }

    private void writeToFile(String name, String content) {

        final File file = new File(baseDir, name);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            final PrintWriter printWriter = new PrintWriter(file);
            printWriter.println(content);
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static JsonObject itemStack2JsonObject(ItemStack itemStack) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("size", itemStack.getCount());
        jsonObject.addProperty("sid", StackUtils.getItemId(itemStack));

        return jsonObject;
    }

    private static JsonObject fluidStack2JsonObject(FluidStack fluidStack) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("size", fluidStack.amount);
        jsonObject.addProperty("sid", StackUtils.getItemId(fluidStack));

        return jsonObject;
    }

    public static JsonObject recipe2JsonObject(IRecipeWrapper recipeWrapper) {
        final Ingredients ingredients = new Ingredients();
        recipeWrapper.getIngredients(ingredients);

        final JsonObject jsonObject = new JsonObject();

        jsonObject.add("result", ingredients2JsonObjects(ingredients::getOutputs));
        jsonObject.add("ingredients", ingredients2JsonObjects(ingredients::getInputs));

        return jsonObject;
    }

    private static <T> JsonArray ingredients2JsonObjects(Function<Class<? extends T>, List<List<T>>> provider) {
        final JsonArray resultArray = new JsonArray();
        for (Class ingredientClass : CONVENTERS.keySet()) {
            final Function<Object, JsonObject> jsonObjectFunction = CONVENTERS.get(ingredientClass);
            final List<List> ingredientOfType = provider.apply(ingredientClass);
            for (List input : ingredientOfType) {
                if (!input.isEmpty()) {
                    final JsonArray inputJsonArray = new JsonArray();
                    for (Object o : input) {
                        if (o != null) {
                            final JsonObject element = jsonObjectFunction.apply(o);
                            inputJsonArray.add(element);
                        }
                    }
                    if (inputJsonArray.size() > 0) {
                        resultArray.add(inputJsonArray);
                    }
                }
            }
        }
        return resultArray;
    }

    private static final Map<Class, Function<Object, JsonObject>> CONVENTERS = ImmutableMap.of(
            ItemStack.class, x -> RecipeExporter.itemStack2JsonObject((ItemStack) x),
            FluidStack.class, x -> RecipeExporter.fluidStack2JsonObject((FluidStack) x)
    );
}
