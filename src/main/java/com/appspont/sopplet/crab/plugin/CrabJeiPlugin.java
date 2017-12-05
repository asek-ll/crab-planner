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

import java.io.*;
import java.util.List;

@JEIPlugin
public class CrabJeiPlugin extends BlankModPlugin {

    private final File baseDir;

    public CrabJeiPlugin() {
        baseDir = new File(".", "dumps/crafting_handler");
        baseDir.mkdirs();
    }

    @Override
    public void register(IModRegistry registry) {
        final IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();

        recipeTransferRegistry.addUniversalRecipeTransferHandler(new PlannerContainerTransferHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        dumpRecipes(jeiRuntime.getRecipeRegistry());
        dumpItems(jeiRuntime.getItemListOverlay());
    }

    private void dumpItems(IItemListOverlay itemListOverlay) {
        final ImmutableList<ItemStack> visibleStacks = itemListOverlay.getVisibleStacks();

        final JsonArray items = new JsonArray();
        for (ItemStack stack : visibleStacks) {
            final JsonObject jsonObject = new JsonObject();
            final Item item = stack.getItem();
            final int idFromItem = Item.getIdFromItem(item);

            jsonObject.addProperty("name", item.getUnlocalizedName());
            jsonObject.addProperty("id", idFromItem);
            jsonObject.addProperty("meta", item.getDamage(stack));
            jsonObject.addProperty("sid", StackUtils.getItemId(stack));
            jsonObject.addProperty("displayName", item.getItemStackDisplayName(stack));

            items.add(jsonObject);
        }
        writeToFile("items.json", items.toString());
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
}
