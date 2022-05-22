package com.appspont.sopplet.crab.container;

import com.appspont.sopplet.crab.planner.CraftingPlan;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CraftingPlanContainer extends Container {
    private CraftingPlan plan;

    public CraftingPlanContainer(CraftingPlan plan) {
        super(ContainerType.GENERIC_3x3, -1);
        this.plan = plan;
    }

    public CraftingPlan getPlan() {
        return plan;
    }

    public void setPlan(CraftingPlan plan) {
        this.plan = plan;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return false;
    }
}
