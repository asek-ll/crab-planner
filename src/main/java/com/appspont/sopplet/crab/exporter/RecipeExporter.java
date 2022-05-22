package com.appspont.sopplet.crab.exporter;

import com.appspont.sopplet.crab.StackUtils;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
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


    public void dumpRecipes(IRecipeManager recipeManager) {
        final List<IRecipeCategory<?>> recipeCategories = recipeManager.getRecipeCategories(null, false);

        final JsonArray recipeCategoriesJson = new JsonArray();
        for (IRecipeCategory<?> recipeCategory : recipeCategories) {
            final List<?> recipeWrappers = recipeManager.getRecipes(recipeCategory, null, false);
            final JsonArray recipes = new JsonArray();
            for (Object recipeWrapper : recipeWrappers) {
                recipes.add(recipe2JsonObject(recipeWrapper));
            }

            final JsonObject recipeCategoryJson = new JsonObject();
            recipeCategoryJson.addProperty("title", recipeCategory.getTitle());
            recipeCategoryJson.addProperty("mod", recipeCategory.getUid().getNamespace());
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

        jsonObject.addProperty("size", fluidStack.getAmount());
        jsonObject.addProperty("sid", StackUtils.getItemId(fluidStack));

        return jsonObject;
    }

    public static <T> JsonObject recipe2JsonObject(T recipeWrapper) {
//        final Ingredients ingredients = new Ingredients();
//        recipeWrapper.getIngredients(ingredients);

        final JsonObject jsonObject = new JsonObject();

//        jsonObject.add("result", ingredients2JsonObjects(ingredients::getOutputs));
//        jsonObject.add("ingredients", ingredients2JsonObjects(ingredients::getInputs));

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
