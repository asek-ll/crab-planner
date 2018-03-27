package com.appspont.sopplet.crab.gui;

import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class PlannerGui extends GuiContainer {

    private final List<Goal> stacks = Lists.newArrayList();
    private final IIngredientRenderer<ItemStack> ingredientRenderer;

    private class Goal {
        final ItemStack stack;
        private final GuiTextField guiTextField;
        private final HoverChecker hoverChecker;

        public Goal(ItemStack stack) {
            this.stack = stack;
            guiTextField = new GuiTextField(0, mc.fontRenderer, 0, 0, 18, 18);
            hoverChecker = new HoverChecker(0, 0, 0, 0, 0);
            stack.setCount(9);
        }

        void render(int x, int y) {
            ingredientRenderer.render(mc, x, y, stack);
            guiTextField.y = y + 18;
            guiTextField.x = x;
            guiTextField.drawTextBox();
            hoverChecker.updateBounds(y, y + 36, x, x + 18);
        }
    }

    public PlannerGui(Container inventorySlotsIn) {
        super(inventorySlotsIn);
        final IIngredientRegistry ingredientRegistry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();
        ingredientRenderer = ingredientRegistry.getIngredientRenderer(ItemStack.class);
        mc = Minecraft.getMinecraft();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawTargets();
    }

    private void drawTargets() {
        int x = 2;
        for (Goal stack : stacks) {
            stack.render(x, 2);
            x += 18;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticks, int x, int y) {
        drawRect(guiLeft, guiTop, xSize, ySize, Color.lightGray.getRGB());
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
//
//                    final IIngredientRegistry ingredientRegistry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();
//
//                    final IIngredientRenderer<ItemStack> ingredientRenderer = ingredientRegistry.getIngredientRenderer(ItemStack.class);
//
//                    mc.getRenderItem().renderItemAndEffectIntoGUI(null, stackUnderMouse, this.width / 2, this.height / 2);
//                    ingredientRenderer.render(mc, this.width / 2, this.height / 2, stackUnderMouse);
//
//                    final GuiIngredientFast guiIngredientFast = new GuiIngredientFast(20, 20, 5);
//
//                    guiIngredientFast.setIngredient(stackUnderMouse);
//
//                    guiIngredientFast.renderItemAndEffectIntoGUI();

                    stacks.add(new Goal(stackUnderMouse));

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

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        for (Goal stack : stacks) {
            stack.guiTextField.setFocused(stack.hoverChecker.checkHover(x, y));
        }
    }

    @Override
    protected void keyTyped(char key, int eventKey) throws IOException {
        for (Goal stack : stacks) {
            if (stack.guiTextField.isFocused()) {
                stack.guiTextField.textboxKeyTyped(key, eventKey);
            }
        }
    }
}
