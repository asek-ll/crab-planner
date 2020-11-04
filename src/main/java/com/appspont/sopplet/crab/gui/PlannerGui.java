package com.appspont.sopplet.crab.gui;

import com.appspont.sopplet.crab.CraftingPlan;
import com.appspont.sopplet.crab.CraftingPlanContainer;
import com.appspont.sopplet.crab.CraftingRecipe;
import com.appspont.sopplet.crab.PlanStoreManager;
import com.appspont.sopplet.crab.gui.planner.*;
import com.appspont.sopplet.crab.gui.planner.widget.InventoryWidget;
import com.appspont.sopplet.crab.planner.ingredient.PlannerFluidStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerItemStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class PlannerGui extends GuiContainer implements CraftingPlanListeners {

    private CraftingPlan plan;
    private final Goals goals;
    private final WidgetContainer<InventoryWidget> widgetWidgetContainer;
    private final int backgroundColor;
    private final DrawContext drawContext;
    private final CraftingStepsWidget craftingSteps;
//    private final RequiredItemsWidget requiredItems;
    private final InventoryWidget requiredItems;
    private final PlanItemsWidget planItems;
    private final IIngredientRegistry ingredientRegistry;
    private final GuiTextField fileNameTextField;
    private final GuiButton saveGuiButton;
    private final PlanStoreManager planStoreManager;

    @Override
    public void updateCraftingSteps(List<CraftingRecipe> recipes) {
        craftingSteps.setRecipes(recipes);
    }

    @Override
    public void updateRequired(List<PlannerIngredientStack> stacks) {
        requiredItems.setStacks(stacks);
    }

    @Override
    public void updateGoals(List<PlannerGoal> plannerGoals) {
        goals.setGoals(plannerGoals);
    }

    private void updatePlanItems() {
        planItems.setNames(planStoreManager.getPlanNames());
    }

    public PlannerGui(CraftingPlan plan) {
        super(new CraftingPlanContainer(plan));
        mc = Minecraft.getMinecraft();
        planStoreManager = CrabJeiPlugin.getPlanStoreManager();
        backgroundColor = new Color(0, 0, 0, 128).getRGB();
        ingredientRegistry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();
        IngredientRenderer ingredientRenderer = new IngredientRenderer(ingredientRegistry, mc);

        requiredItems = new InventoryWidget(new ArrayList<>(), new Rectangle(0, 0, 100, 100),ingredientRenderer,mc, "Required", 4);
//        requiredItems = new RequiredItemsWidget(ingredientRenderer);
        craftingSteps = new CraftingStepsWidget(ingredientRenderer, mc);
        goals = new Goals(mc, ingredientRenderer);
        planItems = new PlanItemsWidget(mc, this);
        widgetWidgetContainer = new WidgetContainer<>(ImmutableList.of(
                requiredItems
        ));

        drawContext = new DrawContext();

        fileNameTextField = new GuiTextField(0, mc.fontRenderer, 1, 0, 18, 18);

        saveGuiButton = new GuiButton(1, 0, 0, 70 - 8, 20, "Save");
        setPlan(plan);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawContext.mouseX = mouseX;
        drawContext.mouseY = mouseY;
        drawContext.partialTicks = partialTicks;
        drawContext.hoverStack = null;

        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        goals.draw(drawContext);
        craftingSteps.draw(drawContext);

        widgetWidgetContainer.draw(drawContext);

        if (drawContext.hoverStack != null) {
            final Object rawStack = drawContext.hoverStack.getRawStack();
            final IIngredientRenderer<Object> renderer = ingredientRegistry.getIngredientRenderer(rawStack);
            final List<String> tooltip = renderer.getTooltip(mc, rawStack, ITooltipFlag.TooltipFlags.NORMAL);
            final FontRenderer fontRenderer = renderer.getFontRenderer(mc, rawStack);
            GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, 600, 400, -1, fontRenderer);
        }

        fileNameTextField.drawTextBox();
        saveGuiButton.drawButton(mc, mouseX, mouseY, partialTicks);
        planItems.draw(drawContext);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticks, int x, int y) {
        drawRect(guiLeft, guiTop, xSize, ySize, backgroundColor);
    }

    @Override
    public void initGui() {
        this.guiLeft = 100;
        this.guiTop = 0;
        this.ySize = this.height;
        this.xSize = this.width - 200;
        craftingSteps.setDimensions(xSize, 160, 86, 250);
        craftingSteps.left = guiLeft;
        goals.setDimensions(xSize / 2, 80, 0, 84);
        goals.left=guiLeft;

        requiredItems.getArea().setBounds(guiLeft, 250, 200, 150);

        planItems.setDimensions(xSize / 2, 60, 20, 84);
        planItems.left = guiLeft + xSize / 2;
        planItems.right = guiLeft + xSize;

        widgetWidgetContainer.updateBounds();

        fileNameTextField.x = guiLeft + xSize / 2;
        fileNameTextField.width = xSize / 2 - 70;

        saveGuiButton.x = guiLeft + xSize - 70;
    }

    private boolean interceptMouseClick() {
        if (Mouse.getEventButton() > -1) {
            if (Mouse.getEventButtonState()) {
                final Object ingredientUnderMouse =
                        CrabJeiPlugin.getJeiRuntime().getIngredientListOverlay().getIngredientUnderMouse();

                if (ingredientUnderMouse != null) {
                    PlannerIngredientStack stack = null;
                    if (ingredientUnderMouse instanceof ItemStack) {
                        stack = new PlannerItemStack((ItemStack) ingredientUnderMouse);
                    } else if (ingredientUnderMouse instanceof FluidStack) {
                        stack = new PlannerFluidStack((FluidStack) ingredientUnderMouse);
                    }
                    if (stack != null) {
                        plan.addGoal(stack);
                        return true;
                    }
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
                goals.handleMouseInput();
                craftingSteps.handleMouseInput();
                planItems.handleMouseInput();
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
    protected void mouseClicked(int x, int y, int button) {
        if (widgetWidgetContainer.contains(x, y)) {
            if (widgetWidgetContainer.mouseClicked(x, y, button)) {
                return;
            }
            PlannerIngredientStack stack = requiredItems.getStackAt(x, y);
            if (stack != null) {
                final IJeiRuntime jeiRuntime = CrabJeiPlugin.getJeiRuntime();
                final IFocus<Object> focus = jeiRuntime.getRecipeRegistry().createFocus(IFocus.Mode.OUTPUT, stack.getIngredient().getRawStack());
                jeiRuntime.getRecipesGui().show(focus);
                return;
            }
        }
        fileNameTextField.mouseClicked(x, y, button);
        if (saveGuiButton.isMouseOver()) {
            savePlan();
        }
    }

    private void savePlan() {
        try {
            planStoreManager.savePlan(plan);
            updatePlanItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlan(CraftingPlan plan) {
        if (this.plan != null) {
            this.plan.removeListener(this);
        }
        this.plan = plan;
        ((CraftingPlanContainer) inventorySlots).setPlan(plan);
        plan.addListener(this);

        updateGoals(plan.getGoals());
        updateCraftingSteps(plan.getRecipes());
        updateRequired(plan.getRequired());
        updatePlanItems();

        fileNameTextField.setText(plan.getName());
        fileNameTextField.setCursorPositionZero();
    }

    @Override
    protected void keyTyped(char key, int eventKey) throws IOException {

        if (fileNameTextField.textboxKeyTyped(key, eventKey)) {
            plan.setName(fileNameTextField.getText());
            return;
        }

//        if (stack.guiTextField.isFocused()) {
//
//            final char c = Character.toUpperCase(key);
//            if (c < 32 || (c >= '0' && c <= '9')) {
//                stack.guiTextField.textboxKeyTyped(key, eventKey);
//                try {
//                    final int i = Integer.parseInt(stack.guiTextField.getText());
//                    stack.stack.setCount(i);
//                } catch (NumberFormatException ignored) {
//                }
//            }
//        }
        super.keyTyped(key, eventKey);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

}