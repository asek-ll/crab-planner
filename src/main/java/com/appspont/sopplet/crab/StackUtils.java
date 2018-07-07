package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class StackUtils {

    private static int counter = 0;
    private static Map<String, String> itemCounters = new HashMap<>();

    private static IIngredientRegistry registry = null;

    private static IIngredientRegistry getRegistry() {

        if (registry == null) {
            registry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();
        }
        return registry;
    }


    public static String getItemId(ItemStack itemStack) {
        final IIngredientHelper ingredientHelper = getRegistry().getIngredientHelper(itemStack.getClass());
        final String uniqueId = ingredientHelper.getUniqueId(itemStack);
        return itemCounters.computeIfAbsent(uniqueId, id -> String.valueOf(counter++));
    }

    public static String getItemIdOld(ItemStack itemStack) {
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

    public static String getItemHash(ItemStack itemStack) {
        final Item item = itemStack.getItem();
        final int idFromItem = Item.getIdFromItem(item);

        final int itemMeta;
        if (!itemStack.getHasSubtypes() || item.isDamageable()) {
            itemMeta = 0;
        } else {
            itemMeta = itemStack.getItemDamage();
        }

        final NBTTagCompound tagCompound = itemStack.getTagCompound();
        final String nbt = tagCompound == null ? "" : tagCompound.toString();

        return idFromItem + ":" + itemMeta + ":" + nbt;
    }

    public static String getItemId(FluidStack fluidStack) {
        final String fluidName = fluidStack.getFluid().getName();
        return "fl:" + fluidName;
    }


}
