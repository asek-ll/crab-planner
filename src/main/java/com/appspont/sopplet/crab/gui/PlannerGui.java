package com.appspont.sopplet.crab.gui;

import com.appspont.sopplet.crab.CraftingRecipe;
import com.appspont.sopplet.crab.PlannerContainer;
import com.appspont.sopplet.crab.gui.planner.*;
import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.ingredients.Ingredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class PlannerGui extends GuiContainer implements PlannerContainerListener {

    private final IngredientRenderer ingredientRenderer;
    private final RecipeSelectorGui recipeSelectorGui = new RecipeSelectorGui();
    private final PlannerContainer plannerContainer;
    private final Goals goals;
    private final WidgetContainer<RectangleWidget> widgetWidgetContainer;
    private final int backgroundColor;
    private final DrawContext drawContext;
    private Tooltip tooltip = null;
    private boolean tooltipInvoked = false;
    private final CraftingStepsWidget craftingSteps;
    private final RequiredItemsWidget requiredItems;
    private final IIngredientRegistry ingredientRegistry;

    @Override
    public void updateCraftingSteps(List<CraftingRecipe> recipes) {
        craftingSteps.setRecipes(recipes);
    }

    @Override
    public void updateRequired(List<PlannerIngredientStack> stacks) {
        requiredItems.setItems(stacks);
    }

    @Override
    public void updateGoals(List<PlannerGoal> plannerGoals) {
        goals.setGoals(plannerGoals);
    }


    public PlannerGui(PlannerContainer plannerContainer) {
        super(plannerContainer);
        mc = Minecraft.getMinecraft();
        backgroundColor = new Color(0, 0, 0, 128).getRGB();
        ingredientRegistry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();
//                ingredientRegistry.getIngredientRenderer(ItemStack.class);
        ingredientRenderer = new IngredientRenderer(ingredientRegistry, mc);
        this.plannerContainer = plannerContainer;

        plannerContainer.addListener(this);
        requiredItems = new RequiredItemsWidget(ingredientRenderer);
        craftingSteps = new CraftingStepsWidget(ingredientRenderer, mc);
        goals = new Goals(mc, ingredientRenderer);
        widgetWidgetContainer = new WidgetContainer<>(ImmutableList.of(
                requiredItems
        ));

        drawContext = new DrawContext();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawContext.mouseX = mouseX;
        drawContext.mouseY = mouseY;
        drawContext.partialTicks = partialTicks;
        drawContext.hoverStack = null;

        tooltipInvoked = tooltip != null && tooltip.isHover(mouseX, mouseY) && !tooltip.isRemoved();

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

        if (recipeSelectorGui.isFocused()) {
            recipeSelectorGui.drawScreen(mouseX, mouseY, partialTicks);
        }


        if (tooltipInvoked) {
            tooltip.draw(mouseX, mouseY);
        } else {
            tooltip = null;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticks, int x, int y) {
        drawRect(guiLeft, guiTop, xSize, ySize, backgroundColor);
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/crafting_table.png"));
//        int i = (this.wQAQAidth - this.xSize) / 2;
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
        craftingSteps.setDimensions(xSize, 150, 100, 250);
        goals.setDimensions(xSize / 2, 100, 2, 96);

        requiredItems.setBounds(0, 250, xSize, 150);

        widgetWidgetContainer.updateBounds();
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
                goals.handleMouseInput();
                craftingSteps.handleMouseInput();
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

        if (widgetWidgetContainer.contains(x, y)) {
            widgetWidgetContainer.mouseClicked(x, y, button);
        }
//        recipeSelectorGui.setFocused(true);
    }

    @Override
    protected void keyTyped(char key, int eventKey) throws IOException {
        super.keyTyped(key, eventKey);
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
