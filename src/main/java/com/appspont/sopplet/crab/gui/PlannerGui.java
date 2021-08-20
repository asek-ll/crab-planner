package com.appspont.sopplet.crab.gui;

import com.appspont.sopplet.crab.CraftingPlan;
import com.appspont.sopplet.crab.CraftingPlanContainer;
import com.appspont.sopplet.crab.CraftingRecipe;
import com.appspont.sopplet.crab.PlanStoreManager;
import com.appspont.sopplet.crab.gui.planner.*;
import com.appspont.sopplet.crab.gui.planner.widget.InventoryWidget;
import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class PlannerGui extends GuiContainer implements CraftingPlanListeners {

    private CraftingPlan plan;
    private final InventoryWidget goals;
    private final WidgetContainer<InventoryWidget> widgetWidgetContainer;
    private final int backgroundColor;
    private final DrawContext drawContext;
    private final CraftingStepsWidget craftingSteps;
    private final InventoryWidget requiredItems;
    private final PlanItemsWidget planItems;
    private final IIngredientRegistry ingredientRegistry;
    private final GuiTextField fileNameTextField;
    private final GuiButton saveGuiButton;
    private final PlanStoreManager planStoreManager;
    private final DragStack dragStack;

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
//        goals.setGoals(plannerGoals);
        goals.setStacks(plannerGoals.stream().map(PlannerGoal::getIngredientStack).collect(Collectors.toList()));
    }

    public void updatePlanItems() {
        planItems.setNames(planStoreManager.getPlanNames());
    }

    public PlannerGui(CraftingPlan plan) {
        super(new CraftingPlanContainer(plan));
        mc = Minecraft.getMinecraft();
        planStoreManager = CrabJeiPlugin.getPlanStoreManager();
        backgroundColor = new Color(0, 0, 0, 128).getRGB();
        ingredientRegistry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();
        IngredientRenderer ingredientRenderer = new IngredientRenderer(ingredientRegistry, mc);

        requiredItems = new InventoryWidget(new Point(0, 0), ingredientRenderer, mc, "Required", 4);
        craftingSteps = new CraftingStepsWidget(ingredientRenderer, mc);
        goals = new InventoryWidget(new Point(0, 0), ingredientRenderer, mc, "Target", 2);
        planItems = new PlanItemsWidget(mc, this);
        widgetWidgetContainer = new WidgetContainer<>(ImmutableList.of(
                requiredItems
        ));
        dragStack = new DragStack(mc, ingredientRenderer);

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

//        super.drawScreen(mouseX, mouseY, partialTicks);

        goals.draw(drawContext);
//        if (true) return;
        craftingSteps.draw(drawContext);

        widgetWidgetContainer.draw(drawContext);

        if (drawContext.hoverStack != null) {
            final Object rawStack = drawContext.hoverStack.getRawStack();
            final IIngredientRenderer<Object> renderer = ingredientRegistry.getIngredientRenderer(rawStack);
            final List<String> tooltip = renderer.getTooltip(mc, rawStack, ITooltipFlag.TooltipFlags.NORMAL);
            tooltip.add(TextFormatting.GRAY + "Amount: " + drawContext.hoverStack.getAmount());
            final FontRenderer fontRenderer = renderer.getFontRenderer(mc, rawStack);
            GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, 600, 400, -1, fontRenderer);
            GlStateManager.disableLighting();
        }

        fileNameTextField.drawTextBox();
        saveGuiButton.drawButton(mc, mouseX, mouseY, partialTicks);
        planItems.draw(drawContext);

        dragStack.draw(drawContext);
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
        goals.getArea().setLocation(guiLeft, 20);

        requiredItems.getArea().setLocation(guiLeft, 250);

        planItems.setDimensions(xSize / 2, 60, 20, 84);
        planItems.left = guiLeft + xSize / 2;
        planItems.right = guiLeft + xSize;

        widgetWidgetContainer.updateBounds();

        fileNameTextField.x = guiLeft + xSize / 2;
        fileNameTextField.width = xSize / 2 - 70;

        saveGuiButton.x = guiLeft + xSize - 70;

        dragStack.setDraggedStack(null);
        dragStack.setJeiLeft(guiLeft + xSize);
    }

    public void handleInput() throws IOException {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                if (!dragStack.interceptMouseClick()) {
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
                requiredItems.handleMouseInput();
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
            return;
        }
        if (goals.getArea().contains(x, y)) {
            PlannerIngredientStack draggedStack = dragStack.getDraggedStack();
            if (draggedStack != null) {
                goals.getIngredients().add(draggedStack);
                dragStack.setDraggedStack(null);
                plan.addGoal(draggedStack);
            } else {
                PlannerIngredientStack stack = goals.getStackAt(x, y);
                if (stack != null) {
                    goals.getIngredients().remove(stack);
                    plan.getGoals().stream()
                            .filter(g -> g.getIngredientStack().equals(stack))
                            .findFirst()
                            .ifPresent(plan::removeGoal);
                    dragStack.setDraggedStack(stack);
                }

            }
            return;
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

        if (dragStack.keyTyped(key, eventKey)) {
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