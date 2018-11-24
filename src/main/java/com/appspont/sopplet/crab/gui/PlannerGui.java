package com.appspont.sopplet.crab.gui;

import com.appspont.sopplet.crab.PlannerContainer;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.Lists;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.ingredients.Ingredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
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
    private final RecipeSelectorGui recipeSelectorGui = new RecipeSelectorGui();
    private final PlannerContainer plannerContainer;
    private final Goals goals;
    private Tooltip tooltip = null;
    private boolean tooltipInvoked = false;

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

    private abstract class Tooltip<T> {
        final T source;
        private final HoverChecker hoverChecker;
        final int x;
        final int y;
        final int width;
        final int height;

        private Tooltip(T source, int x, int y, int width, int height) {
            this.source = source;
            this.hoverChecker = new HoverChecker(y, y + height, x, x + width, 0);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        boolean isHover(int x, int y) {
            return hoverChecker.checkHover(x, y);
        }

        boolean isSameCaller(Object caller) {
            return source.equals(caller);
        }

        abstract void draw(int x, int y);

        public abstract void handleClick(int x, int y, int button);
    }

    private class GoalTooltip extends Tooltip<ItemStack> {

        private final GuiButton delete;

        private GoalTooltip(ItemStack source, int x, int y) {
            super(source, x, y, 100, 100);
            delete = new GuiButton(1, x + 4, y + 4, 100 - 8, 20, "Delete");
        }

        @Override
        void draw(int x, int y) {
            drawRect(this.x, this.y, this.x + width, this.y + height, Color.red.getRGB());
            delete.drawButton(Minecraft.getMinecraft(), x, y, 0);
        }

        @Override
        public void handleClick(int x, int y, int button) {
            if (delete.isMouseOver()) {
                plannerContainer.getGoals().remove(source);
            }
        }
    }

    public class Goals extends GuiSlot {

        public Goals(Minecraft mc) {
            super(mc, 100, 200, 2, 200, 0);
        }

        @Override
        protected int getSize() {
            return plannerContainer.getGoals().size();
        }

        @Override
        protected void elementClicked(int i, boolean b, int i1, int i2) {

        }

        @Override
        protected boolean isSelected(int i) {
            return false;
        }

        @Override
        protected void drawBackground() {

        }

        @Override
        protected void drawSlot(int i, int i1, int i2, int i3, int i4, int i5, float v) {

        }
    }


    public PlannerGui(PlannerContainer plannerContainer) {
        super(plannerContainer);
        final IIngredientRegistry ingredientRegistry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();
        ingredientRenderer = ingredientRegistry.getIngredientRenderer(ItemStack.class);
        mc = Minecraft.getMinecraft();
        this.plannerContainer = plannerContainer;
        goals = new Goals(mc);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        tooltipInvoked = tooltip != null && tooltip.isHover(mouseX, mouseY);

        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
//        drawTargets(mouseX, mouseY);
        goals.drawScreen(mouseX, mouseY, partialTicks);

        drawRecipes(mouseX, mouseY);

        if (recipeSelectorGui.isFocused()) {
            recipeSelectorGui.drawScreen(mouseX, mouseY, partialTicks);
        }


        if (tooltipInvoked) {
            tooltip.draw(mouseX, mouseY);
        } else {
            tooltip = null;
        }
    }

    private void drawRecipes(int mouseX, int mouseY) {
        int y = 100;
        for (PlannerContainer.Recipe recipe : plannerContainer.getRecipes()) {
            int x = 2;
            for (ItemStack itemStack : recipe.getResult()) {
                ingredientRenderer.render(mc, x, y, itemStack);
                x += 18;
            }
            x += 18;
            for (ItemStack itemStack : recipe.getIngredients()) {
                ingredientRenderer.render(mc, x, y, itemStack);
                x += 18;
            }
            y += 18;
        }
    }

    private void drawTargets(int mouseX, int mouseY) {
        int y = 2;
        for (ItemStack goal : plannerContainer.getGoals()) {
            ingredientRenderer.render(mc, 2, y, goal);

            if (!tooltipInvoked && mouseX >= 2 && mouseX <= 2 + 16 && mouseY >= y && mouseY <= y + 16) {
                tooltipInvoked = true;
                if (tooltip == null || !tooltip.isSameCaller(goal)) {
                    tooltip = new GoalTooltip(goal, 2 + 16, y);
                }
            }

            y += 18;
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
        recipeSelectorGui.width = 100;
        recipeSelectorGui.height = 100;
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

//                    stacks.add(new Goal(stackUnderMouse));
                    plannerContainer.addGoal(stackUnderMouse);

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
        if (tooltip != null) {
            tooltip.handleClick(x, y, button);
        }
        for (Goal stack : stacks) {
            stack.guiTextField.setFocused(stack.hoverChecker.checkHover(x, y));
        }
//        recipeSelectorGui.setFocused(true);
    }

    @Override
    protected void keyTyped(char key, int eventKey) throws IOException {
        for (Goal stack : stacks) {
            if (stack.guiTextField.isFocused()) {

                final char c = Character.toUpperCase(key);
                if (c < 32 || (c >= '0' && c <= '9')) {
                    stack.guiTextField.textboxKeyTyped(key, eventKey);
                    try {
                        final int i = Integer.parseInt(stack.guiTextField.getText());
                        stack.stack.setCount(i);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
    }

    private List<Ingredients> getStackIngredients(ItemStack stack) {

        final List<Ingredients> ingredientList = Lists.newArrayList();
        final IRecipeRegistry recipeRegistry = CrabJeiPlugin.getJeiRuntime().getRecipeRegistry();
        final List<IRecipeCategory> recipeCategories = recipeRegistry.getRecipeCategories();
        for (IRecipeCategory recipeCategory : recipeCategories) {
            final List<IRecipeWrapper> recipeWrappers =
                    recipeRegistry.getRecipeWrappers(recipeCategory, recipeRegistry.createFocus(IFocus.Mode.OUTPUT, stack));
            for (IRecipeWrapper recipeWrapper : recipeWrappers) {
                Ingredients ingredients = new Ingredients();
                recipeWrapper.getIngredients(ingredients);
                ingredientList.add(ingredients);
            }
        }

        return ingredientList;
    }
}
