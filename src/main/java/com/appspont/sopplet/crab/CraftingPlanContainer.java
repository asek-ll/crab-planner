package com.appspont.sopplet.crab;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class CraftingPlanContainer extends Container {
    private CraftingPlan plan;

    public CraftingPlanContainer(CraftingPlan plan) {
        this.plan = plan;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return false;
    }

    public CraftingPlan getPlan() {
        return plan;
    }

    public void setPlan(CraftingPlan plan) {
        this.plan = plan;
    }
}
