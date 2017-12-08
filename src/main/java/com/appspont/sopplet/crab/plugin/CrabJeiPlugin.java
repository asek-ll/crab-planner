package com.appspont.sopplet.crab.plugin;

import com.appspont.sopplet.crab.PlannerContainerTransferHandler;
import com.appspont.sopplet.crab.StackUtils;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@JEIPlugin
public class CrabJeiPlugin extends BlankModPlugin {

    private final File baseDir;
    private static IJeiRuntime jeiRuntime;
    private static IModRegistry modRegistry;

    public CrabJeiPlugin() {
        baseDir = new File(".", "dumps");
        baseDir.mkdirs();
    }

    @Override
    public void register(IModRegistry registry) {
        final IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
        recipeTransferRegistry.addUniversalRecipeTransferHandler(new PlannerContainerTransferHandler());
        modRegistry = registry;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        dumpRecipes(jeiRuntime.getRecipeRegistry());
        CrabJeiPlugin.jeiRuntime = jeiRuntime;
    }

    private void dumpRecipes(IRecipeRegistry recipeRegistry) {
        final List<IRecipeCategory> recipeCategories = recipeRegistry.getRecipeCategories();

        final JsonArray recipes = new JsonArray();
        for (IRecipeCategory recipeCategory : recipeCategories) {
            final List<IRecipeWrapper> recipeWrappers = recipeRegistry.getRecipeWrappers(recipeCategory);
            for (IRecipeWrapper recipeWrapper : recipeWrappers) {
                recipes.add(StackUtils.recipe2JsonObject(recipeWrapper));
            }
        }

        writeToFile("recipes.json", recipes.toString());
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

    public static IJeiRuntime getJeiRuntime() {
        return jeiRuntime;
    }

    public static IModRegistry getModRegistry() {
        return modRegistry;
    }
}
