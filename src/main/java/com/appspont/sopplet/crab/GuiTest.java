package com.appspont.sopplet.crab;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTest extends GuiContainer {

    public GuiTest(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticks, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/crafting_table.png"));
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 - 24, "This is button a"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + 4, "This is button b"));
    }
}
