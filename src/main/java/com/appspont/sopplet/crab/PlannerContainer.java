package com.appspont.sopplet.crab;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import java.util.List;

public class PlannerContainer extends Container {
    private final List<ItemStack> goals = Lists.newArrayList();

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return false;
    }

    public void addGoal(ItemStack stack) {
        goals.add(stack);
    }

    public void addRecipe(List<ItemStack> outputs, List<ItemStack> ingredients) {

    }
}
