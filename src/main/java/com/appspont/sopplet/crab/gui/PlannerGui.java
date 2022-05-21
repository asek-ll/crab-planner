package com.appspont.sopplet.crab.gui;

import com.appspont.sopplet.crab.CraftingPlan;
import com.appspont.sopplet.crab.CraftingRecipe;
import com.appspont.sopplet.crab.PlanStoreManager;
import com.appspont.sopplet.crab.container.CraftingPlanContainer;
import com.appspont.sopplet.crab.gui.planner.*;
import com.appspont.sopplet.crab.gui.planner.widget.InventoryWidget;
import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PlannerGui extends ContainerScreen<CraftingPlanContainer> implements CraftingPlanListeners,
        JeiMouseClickInterceptor {

    public static Button.IPressable NO_PRESSABLE = btn -> {
    };

    public static Button.ITooltip NO_TOOLTIP = new Button.ITooltip() {

        @Override
        public void onTooltip(Button p_onTooltip_1_, MatrixStack p_onTooltip_2_, int p_onTooltip_3_, int p_onTooltip_4_) {

        }
    };

    private CraftingPlan plan;
    private final InventoryWidget goals;
    private final WidgetContainer<InventoryWidget> widgetWidgetContainer;
    private final int backgroundColor;
    private final DrawContext drawContext;
    private final CraftingStepsWidget craftingSteps;
    private final InventoryWidget requiredItems;
    private final PlanItemsWidget planItems;
    private final IIngredientManager ingredientRegistry;
    private final TextFieldWidget fileNameTextField;
    private final Button saveGuiButton;
    private final Button resetGuiButton;
    private final PlanStoreManager planStoreManager;
    private final DragStack dragStack;
    private final InventoryWidget inventory;
    private RecipeGui recipeGui;

    @Override
    public void updateCraftingSteps(List<CraftingRecipe> recipes) {
        craftingSteps.setRecipes(recipes);
    }

    @Override
    public void updateRequired(List<PlannerIngredientStack> stacks) {
        requiredItems.setStacks(stacks);
    }

    public void updateInventory(List<PlannerIngredientStack> stacks) {
        inventory.setStacks(stacks);
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
        super(new CraftingPlanContainer(plan), Minecraft.getInstance().player.inventory, new StringTextComponent("Planner"));
//        super(new CraftingPlanContainer(plan));
        minecraft = Minecraft.getInstance();
        planStoreManager = CrabJeiPlugin.getPlanStoreManager();
        backgroundColor = new Color(0, 0, 0, 128).getRGB();
        ingredientRegistry = CrabJeiPlugin.getJeiRuntime().getIngredientManager();
        IngredientRenderer ingredientRenderer = new IngredientRenderer(ingredientRegistry, minecraft);

        requiredItems = new InventoryWidget(new Point(0, 0), ingredientRenderer, minecraft, "Required", 4);
        inventory = new InventoryWidget(new Point(0, 0), ingredientRenderer, minecraft, "Inventory", 4);
        craftingSteps = new CraftingStepsWidget(ingredientRenderer, minecraft);
        goals = new InventoryWidget(new Point(0, 0), ingredientRenderer, minecraft, "Target", 2);
        planItems = new PlanItemsWidget(minecraft, this);
        widgetWidgetContainer = new WidgetContainer<>(ImmutableList.of(
                requiredItems,
                inventory
        ));
        dragStack = new DragStack(minecraft, ingredientRenderer);

        drawContext = new DrawContext();

        fileNameTextField = new TextFieldWidget(minecraft.font, 1, 0, 18, 18,
                new StringTextComponent("Btn"));

        saveGuiButton = new Button(0, 0, 50 - 8, 20, new StringTextComponent("Save"), NO_PRESSABLE, NO_TOOLTIP);
        resetGuiButton = new Button(0, 0, 50 - 8, 20, new StringTextComponent("Reset"), NO_PRESSABLE, NO_TOOLTIP);

        setPlan(plan);
    }


    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        drawContext.mouseX = mouseX;
        drawContext.mouseY = mouseY;
        drawContext.partialTicks = partialTicks;
        drawContext.hoverStack = null;
        drawContext.hoverText = null;
        drawContext.ms = ms;

        renderBg(ms, partialTicks, mouseX, mouseY);

//        this.drawDefaultBackground();

//        super.drawScreen(mouseX, mouseY, partialTicks);

        goals.draw(drawContext);
        craftingSteps.draw(drawContext);

        widgetWidgetContainer.draw(drawContext);

        fileNameTextField.render(ms, mouseX, mouseX, partialTicks);
        saveGuiButton.render(ms, mouseX, mouseY, partialTicks);
        resetGuiButton.render(ms, mouseX, mouseY, partialTicks);
        planItems.draw(drawContext);


        if (drawContext.hoverStack != null) {
            final Object rawStack = drawContext.hoverStack.getRawStack();
            final IIngredientRenderer<Object> renderer = ingredientRegistry.getIngredientRenderer(rawStack);
            final List<ITextComponent> tooltip = renderer.getTooltip(rawStack, ITooltipFlag.TooltipFlags.NORMAL);
            tooltip.add(new StringTextComponent(
                    TextFormatting.GRAY + "Amount: " + drawContext.hoverStack.getAmount()));
            final FontRenderer fontRenderer = renderer.getFontRenderer(minecraft, rawStack);
            GuiUtils.drawHoveringText(ms, tooltip, mouseX, mouseY, 600, 400, -1, fontRenderer);
//            GlStateManager.disableLighting();
        } else if (drawContext.hoverText != null) {
            GuiUtils.drawHoveringText(ms, drawContext.hoverText, mouseX, mouseY, 600, 400, -1, minecraft.font);
        }


        dragStack.draw(drawContext);
    }

    @Override
    protected void renderBg(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        renderBackground(ms);
    }

    //    @Override
//    protected void drawGuiContainerBackgroundLayer(float ticks, int x, int y) {
//        drawRect(guiLeft, guiTop, xSize, ySize, backgroundColor);
//    }
//
    @Override
    public void init(Minecraft mc, int width, int height) {
        super.init(mc, width, height);
        int guiLeft = 100;
        this.leftPos = guiLeft;
        this.topPos = 0;
        this.imageHeight = height;

        goals.getArea().setLocation(guiLeft, 20);
        planItems.updateSize(goals.getArea().width, 60, 20, 84);
        planItems.setLeftPos(guiLeft + goals.getArea().width);
        planItems.setRenderTopAndBottom(false);

        int xSize = goals.getArea().width + planItems.getWidth();
        this.imageWidth = xSize;

        craftingSteps.updateSize(xSize, 160, 86, 250);
        craftingSteps.setLeftPos(guiLeft);
        craftingSteps.setRenderTopAndBottom(false);

        requiredItems.getArea().setLocation(guiLeft, 250);
        inventory.getArea().setLocation(guiLeft + requiredItems.getArea().width, 250);

        widgetWidgetContainer.updateBounds();

        fileNameTextField.setX(guiLeft + xSize / 2);
        fileNameTextField.setWidth(xSize / 2 - 70);

        saveGuiButton.x = guiLeft + xSize - saveGuiButton.getWidth();
        resetGuiButton.x = guiLeft;

        dragStack.setDraggedStack(null);
        dragStack.setJeiLeft(guiLeft + xSize);

        addWidget(fileNameTextField);
        addWidget(planItems);
        addWidget(craftingSteps);
    }

    private RecipeGui getRecipeGui() {
        if (recipeGui == null) {
            IIngredientManager ingredientRegistry = CrabJeiPlugin.getJeiRuntime().getIngredientManager();
            recipeGui = new RecipeGui(ingredientRegistry, minecraft);
        }
        return recipeGui;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
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
            return true;
        }

        if (widgetWidgetContainer.getArea().contains(x, y)) {
            if (widgetWidgetContainer.mouseClicked(x, y, button)) {
                return true;
            }
            PlannerIngredientStack stack = requiredItems.getStackAt(x, y);
            if (stack != null) {
                final IJeiRuntime jeiRuntime = CrabJeiPlugin.getJeiRuntime();
                final IFocus<Object> focus = jeiRuntime.getRecipeManager().createFocus(IFocus.Mode.OUTPUT, stack.getIngredient().getRawStack());
                RecipeGui gui = getRecipeGui();
                gui.setPlan(plan);
                gui.setParentScreen(this);
                minecraft.setScreen(gui);
                jeiRuntime.getRecipesGui().show(focus);
                if (minecraft.screen == gui) {
                    minecraft.setScreen(this);
                }
                return true;
            }
        }

        if (saveGuiButton.isMouseOver(x, y)) {
            savePlan();
            return true;
        }
        if (resetGuiButton.isMouseOver(x, y)) {
            resetPlan();
            return true;
        }

        if (inventory.getArea().contains(x, y)) {
            PlannerIngredientStack draggedStack = dragStack.getDraggedStack();
            if (draggedStack != null) {
                inventory.getIngredients().add(draggedStack);
                dragStack.setDraggedStack(null);
                plan.addInventoryStack(draggedStack);
            } else {
                PlannerIngredientStack stack = inventory.getStackAt(x, y);
                if (stack != null) {
                    inventory.getIngredients().remove(stack);
                    plan.getInventory().stream()
                            .filter(i -> i.equals(stack))
                            .findFirst()
                            .ifPresent(plan::removeInventoryItem);
                    dragStack.setDraggedStack(stack);
                }

            }
            return true;
        }

        return super.mouseClicked(x, y, button);
    }

    @Override
    public void interceptMouseClick(GuiScreenEvent.MouseClickedEvent.Pre event) {
        if (dragStack.interceptMouseClick(event.getMouseX(), event.getMouseY(), event.getButton())) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        }
    }

    //    public void handleInput() throws IOException {
//        if (Mouse.isCreated()) {
//            while (Mouse.next()) {
//                if (!dragStack.interceptMouseClick()) {
//                    if (!MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre(this))) {
//                        this.handleMouseInput();
//                        if (this.equals(this.mc.currentScreen)) {
//                            MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Post(this));
//                        }
//                    }
//                }
//                goals.handleMouseInput();
//                craftingSteps.handleMouseInput();
//                planItems.handleMouseInput();
//                requiredItems.handleMouseInput();
//                inventory.handleMouseInput();
//            }
//        }
//
//        if (Keyboard.isCreated()) {
//            while (Keyboard.next()) {
//                if (!MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre(this))) {
//                    this.handleKeyboardInput();
//                    if (this.equals(this.mc.currentScreen)) {
//                        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post(this));
//                    }
//                }
//            }
//        }
//    }

//    @Override
//    protected void mouseClicked(int x, int y, int button) {
//        fileNameTextField.mouseClicked(x, y, button);
//
//            return;
//        }
//    }

    private void resetPlan() {
        setPlan(new CraftingPlan());
    }

    private void savePlan() {
        try {
            plan.setName(fileNameTextField.getValue());
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
//        ((CraftingPlanContainer) inventorySlots).setPlan(plan);
        plan.addListener(this);

        updateGoals(plan.getGoals());
        updateCraftingSteps(plan.getRecipes());
        updateRequired(plan.getRequired());
        updateInventory(plan.getInventory());
        updatePlanItems();

        fileNameTextField.setValue(plan.getName());
        fileNameTextField.setCursorPosition(0);
    }

    @Override
    public boolean keyPressed(int key, int code, int m) {
        if (dragStack.keyTyped(key, code, m)) {
            return true;
        }
        return screenKeyPressed(key, code, m);
    }


    private boolean screenKeyPressed(int key, int scan, int other) {
        if (key == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else if (key == 258) {
            boolean flag = !hasShiftDown();
            if (!this.changeFocus(flag)) {
                this.changeFocus(flag);
            }
            return false;
        } else {
            return this.getFocused() != null && this.getFocused().keyPressed(key, scan, other);
        }
    }

    @Override
    public boolean charTyped(char ch, int param1) {
        if (dragStack.charTyped(ch, param1)) {
            return true;
        }
        return super.charTyped(ch, param1);
    }


    //
//    @Override
//    protected void keyTyped(char key, int eventKey) throws IOException {
//
//        if (fileNameTextField.textboxKeyTyped(key, eventKey)) {
//            plan.setName(fileNameTextField.getText());
//            return;
//        }
//
//        if (dragStack.keyTyped(key, eventKey)) {
//            return;
//        }
//
////        if (stack.guiTextField.isFocused()) {
////
////            final char c = Character.toUpperCase(key);
////            if (c < 32 || (c >= '0' && c <= '9')) {
////                stack.guiTextField.textboxKeyTyped(key, eventKey);
////                try {
////                    final int i = Integer.parseInt(stack.guiTextField.getText());
////                    stack.stack.setCount(i);
////                } catch (NumberFormatException ignored) {
////                }
////            }
////        }
//        super.keyTyped(key, eventKey);
//    }
}
