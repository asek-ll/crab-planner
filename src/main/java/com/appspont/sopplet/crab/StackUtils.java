package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackUtils {

    private static int counter = 0;
    private static Map<String, String> itemCounters = new HashMap<>();

    private static IIngredientManager registry = null;

    private static IIngredientManager getRegistry() {

        if (registry == null) {
            registry = CrabJeiPlugin.getJeiRuntime().getIngredientManager();
        }
        return registry;
    }


    public static String getItemId(ItemStack itemStack) {
        final IIngredientHelper ingredientHelper = getRegistry().getIngredientHelper(itemStack.getClass());
        final String uniqueId = ingredientHelper.getUniqueId(itemStack);
        return itemCounters.computeIfAbsent(uniqueId, id -> String.valueOf(counter++));
    }

    public static String getItemId(FluidStack fluidStack) {
        final String fluidName = fluidStack.getFluid().getRegistryName().getNamespace();
        return "fl:" + fluidName;
    }


    public static <T> JsonArray toJsonArray(Iterable<T> items, Class<T> type, JsonSerializationContext context) {
        final JsonArray jsonElements = new JsonArray();
        for (T item : items) {
            jsonElements.add(context.serialize(item, type));
        }
        return jsonElements;
    }

    public static <T> List<T> fromJsonArray(JsonArray array, Class<T> type, JsonDeserializationContext context) {
        final ArrayList<T> result = new ArrayList<>(array.size());
        for (JsonElement jsonElement : array) {
            final T deserialize = context.deserialize(jsonElement, type);
            if (deserialize != null) {
                result.add(deserialize);
            }
        }
        return result;
    }
}
