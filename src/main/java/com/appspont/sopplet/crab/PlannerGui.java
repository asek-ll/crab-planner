package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class PlannerGui extends GuiContainer {

    public PlannerGui(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticks, int x, int y) {
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/crafting_table.png"));
//        int i = (this.width - this.xSize) / 2;
//        int j = (this.height - this.ySize) / 2;
//        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void initGui() {
        this.guiLeft = 0;
        this.guiTop = 0;
        this.ySize = this.height;
        this.xSize = this.width - 100;
//        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 - 24, "This is button a"));
//        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + 4, "This is button b"));
    }

    private boolean interceptMouseClick() {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;


        if (Mouse.getEventButton() > -1) {
            if (Mouse.getEventButtonState()) {
                final ItemStack stackUnderMouse = CrabJeiPlugin.getJeiRuntime().getItemListOverlay().getStackUnderMouse();
                if (stackUnderMouse != null) {

                    final IIngredientRegistry ingredientRegistry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();

                    final IIngredientRenderer<ItemStack> ingredientRenderer = ingredientRegistry.getIngredientRenderer(ItemStack.class);

                    mc.getRenderItem().renderItemAndEffectIntoGUI(null, stackUnderMouse, this.width / 2, this.height / 2);
//                    ingredientRenderer.render(mc, this.width / 2, this.height/2, stackUnderMouse);

                    return true;
                }
            }
        }

        return false;
    }

    public void handleInput() throws IOException {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                if (!interceptMouseClick()) {
                    if (!MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre(this))) {
                        this.handleMouseInput();
                        if (this.equals(this.mc.currentScreen)) {
                            MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Post(this));
                        }
                    }
                }
            }
        }

        if (Keyboard.isCreated()) {
            while (Keyboard.next()) {
                if (!MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre(this))) {
                    this.handleKeyboardInput();
                    if (this.equals(this.mc.currentScreen)) {
                        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post(this));
                    }
                }
            }
        }
    }
}
