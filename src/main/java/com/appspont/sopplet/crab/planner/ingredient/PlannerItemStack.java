package com.appspont.sopplet.crab.planner.ingredient;

import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class PlannerItemStack extends PlannerIngredientStack {
    private final ItemStack itemStack;

    public PlannerItemStack(ItemStack itemStack) {
        super(new PlannerItem(itemStack));
        this.itemStack = itemStack;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack.getItem(), itemStack.getCount(), itemStack.getItemDamage());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStack is1 = itemStack;
        ItemStack is2 = ((PlannerItemStack) o).itemStack;

        if (is1 == is2) {
            return true;
        }
        if (is1 == null || is2 == null) {
            return false;
        }

        return is1.getCount() == is2.getCount() &&
                CrabJeiPlugin.getModRegistry().getJeiHelpers().getStackHelper().isEquivalent(is1, is2);
    }

    @Override
    public int getAmount() {
        return itemStack.getCount();
    }
}
