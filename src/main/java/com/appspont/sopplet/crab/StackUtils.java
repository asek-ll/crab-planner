package com.appspont.sopplet.crab;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.ingredients.Ingredients;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class StackUtils {
    private static List<String> modsList = new ArrayList<String>();

    public static String getItemId(ItemStack itemStack) {
        final Item item = itemStack.getItem();
        final int idFromItem = Item.getIdFromItem(item);

        final int itemMeta;
        if (!itemStack.getHasSubtypes() || item.isDamageable()) {
            itemMeta = 0;
        } else {
            itemMeta = itemStack.getItemDamage();
        }

        return idFromItem + ":" + itemMeta;
    }

    public static String getItemId(FluidStack fluidStack) {
        final String fluidName = fluidStack.getFluid().getName();
        return "fl:" + fluidName;
    }

    private static JsonObject itemStack2JsonObject(ItemStack itemStack) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("size", itemStack.getCount());
        jsonObject.addProperty("sid", getItemId(itemStack));

        return jsonObject;
    }

    private static JsonArray itemStacks2JsonObject(List<ItemStack> itemStacks) {
        final JsonArray jsonArray = new JsonArray();

        for (ItemStack itemStack : itemStacks) {
            jsonArray.add(itemStack2JsonObject(itemStack));
        }

        return jsonArray;
    }

    public static JsonObject fluidStackToJsonObject(FluidStack stack) {
        if (stack == null) {
            return null;
        }

        final JsonObject stackInfo = new JsonObject();

        stackInfo.addProperty("size", stack.amount);
        stackInfo.addProperty("sid", getItemId(stack));


        final JsonArray items = new JsonArray();

        items.add(stackInfo);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.add("items", items);

        return jsonObject;
    }

    public static JsonArray fluidStacks2JsonArray(List<FluidStack> stacks) {
        final JsonArray stacksInfo = new JsonArray();
        for (FluidStack stack : stacks) {
            stacksInfo.add(fluidStackToJsonObject(stack));
        }
        return stacksInfo;
    }

    public static JsonObject recipe2JsonObject(IRecipeWrapper recipeWrapper) {
        final Ingredients ingredients = new Ingredients();
        recipeWrapper.getIngredients(ingredients);

        final List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);
        final List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);

        final JsonObject jsonObject = new JsonObject();

        jsonObject.add("result", tacksList2JsonObject(outputs));

        jsonObject.add("ingredients", tacksList2JsonObject(inputs));

        return jsonObject;
    }

    private static JsonArray tacksList2JsonObject(List<List<ItemStack>> inputs) {
        final JsonArray inputJsonArray = new JsonArray();
        for (List<ItemStack> input : inputs) {
            final JsonArray element = itemStacks2JsonObject(input);
            if (element.size() > 0) {
                inputJsonArray.add(element);
            }
        }
        return inputJsonArray;
    }

}
