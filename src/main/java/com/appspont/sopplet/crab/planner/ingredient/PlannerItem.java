package com.appspont.sopplet.crab.planner.ingredient;

import com.appspont.sopplet.crab.jei.CrabJeiPlugin;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class PlannerItem extends PlannerIngredient<ItemStack> {
    public PlannerItem(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack.getItem(), stack.getDamageValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStack is1 = stack;
        ItemStack is2 = ((PlannerItem) o).stack;

        if (is1 == is2) {
            return true;
        }
        if (is1 == null || is2 == null) {
            return false;
        }

        return CrabJeiPlugin.getJeiHelpers().getStackHelper().isEquivalent(is1, is2, UidContext.Ingredient);
    }

    @Override
    public PlannerIngredientStack createStack(int amount) {
        final ItemStack copy = stack.copy();
        copy.setCount(amount);
        return new PlannerItemStack(copy);
    }

    @Override
    public int getAmount() {
        return stack.getCount();
    }
}

