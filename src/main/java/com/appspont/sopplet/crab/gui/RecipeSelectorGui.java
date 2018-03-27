package com.appspont.sopplet.crab.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class RecipeSelectorGui extends GuiScreen {
    public RecipeSelectorGui() {
        mc = Minecraft.getMinecraft();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
