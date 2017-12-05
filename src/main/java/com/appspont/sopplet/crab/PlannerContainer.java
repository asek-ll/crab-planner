package com.appspont.sopplet.crab;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class PlannerContainer extends Container {
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return false;
    }
}
