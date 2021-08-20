package com.appspont.sopplet.crab.gui;

import com.appspont.sopplet.crab.CraftingPlan;
import com.appspont.sopplet.crab.PlannerRecipe;
import com.appspont.sopplet.crab.gui.planner.DragStack;
import com.appspont.sopplet.crab.gui.planner.DrawContext;
import com.appspont.sopplet.crab.gui.planner.IngredientRenderer;
import com.appspont.sopplet.crab.gui.planner.widget.InventoryWidget;
import com.appspont.sopplet.crab.planner.ingredient.PlannerFluidStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredient;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerItemStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeGui extends GuiContainer {

    private final DrawContext drawContext;
    private final IngredientRenderer ingredientRenderer;
    private final InventoryWidget results;
    private final InventoryWidget catalysts;
    private final InventoryWidget ingredients;
    private final IIngredientRegistry ingredientRegistry;
    private final List<InventoryWidget> inventories;
    private final GuiButton saveButton;
    private final GuiButton cancelButton;
    private IRecipeLayout recipeLayout;
    private DragStack dragStack;
    private GuiScreen parentScreen;
    private CraftingPlan plan;

    public RecipeGui(IIngredientRegistry ingredientRegistry, Minecraft mc) {
        super(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer entityPlayer) {
                return false;
            }
        });
        this.ingredientRegistry = ingredientRegistry;
        ingredientRenderer = new IngredientRenderer(this.ingredientRegistry, mc);
        drawContext = new DrawContext();

        results = new InventoryWidget(new Point(100, 100), ingredientRenderer, mc, "Result", 2);
        catalysts = new InventoryWidget(new Point(100, 160), ingredientRenderer, mc, "Catalyst", 2);
        ingredients = new InventoryWidget(new Point(100, 220), ingredientRenderer, mc, "Ingredients", 2);

        inventories = Arrays.asList(results, catalysts, ingredients);

        dragStack = new DragStack(mc, ingredientRenderer);

        this.guiLeft = (width - xSize) / 2;
        saveButton = new GuiButton(1, 0, 0, 70 - 8, 20, "Save");
        cancelButton = new GuiButton(1, 0, 0, 70 - 8, 20, "Cancel");
    }

    public void setRecipe(IRecipeLayout recipeLayout) {
        this.recipeLayout = recipeLayout;
        PlannerRecipe plannerRecipe = prepareRecipe(recipeLayout);
        results.setStacks(plannerRecipe.getResult());
        catalysts.setStacks(plannerRecipe.getCatalysts());
        ingredients.setStacks(plannerRecipe.getIngredients());
    }

    public static PlannerRecipe prepareRecipe(IRecipeLayout iRecipeLayout) {
        final List<PlannerIngredientStack> outputs = Lists.newArrayList();
        final List<PlannerIngredientStack> ingredients = Lists.newArrayList();

        final IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        final Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();
        for (IGuiIngredient<ItemStack> itemStackIGuiIngredient : guiIngredients.values()) {
            final List<ItemStack> allIngredients = itemStackIGuiIngredient.getAllIngredients();
            if (!allIngredients.isEmpty()) {
                final ItemStack displayedIngredient = allIngredients.get(0);
                if (itemStackIGuiIngredient.isInput()) {
                    ingredients.add(new PlannerItemStack(displayedIngredient));
                } else {
                    outputs.add(new PlannerItemStack(displayedIngredient));
                }
            }
        }

        final Collection<? extends IGuiIngredient<FluidStack>> guiFluidStack =
                iRecipeLayout.getFluidStacks().getGuiIngredients().values();

        for (IGuiIngredient<FluidStack> fluidStackIGuiIngredient : guiFluidStack) {
            final List<FluidStack> allIngredients = fluidStackIGuiIngredient.getAllIngredients();
            if (!allIngredients.isEmpty()) {
                final PlannerFluidStack fluidStack = new PlannerFluidStack(allIngredients.get(0));
                if (fluidStackIGuiIngredient.isInput()) {
                    ingredients.add(fluidStack);
                } else {
                    outputs.add(fluidStack);
                }
            }
        }
        IRecipeRegistry recipeRegistry = CrabJeiPlugin.getJeiRuntime().getRecipeRegistry();
        List<Object> recipeCatalysts = recipeRegistry.getRecipeCatalysts(iRecipeLayout.getRecipeCategory());
        List<PlannerIngredientStack> catalysts = new ArrayList<>();

        for (Object recipeCatalyst : recipeCatalysts) {
            if (recipeCatalyst instanceof ItemStack) {
                catalysts.add(new PlannerItemStack((ItemStack) recipeCatalyst));
                break;
            }
        }

        return new PlannerRecipe(compactItems(outputs), compactItems(ingredients), catalysts);
    }

    private static List<PlannerIngredientStack> compactItems(Collection<PlannerIngredientStack> ingredientStacks) {
        final Multiset<PlannerIngredient> compacted = HashMultiset.create();
        for (PlannerIngredientStack ingredient : ingredientStacks) {
            compacted.add(ingredient.getIngredient(), ingredient.getAmount());
        }

        return compacted.entrySet().stream()
                .map(e -> e.getElement().createStack(e.getCount()))
                .collect(Collectors.toList());
    }

    @Override
    public void initGui() {
        super.initGui();
        ySize = 240;
        xSize = 200;
        this.guiLeft = (width - xSize) / 2;
        this.guiTop = (height - ySize) / 2;
        dragStack.setDraggedStack(null);
        dragStack.setJeiLeft(guiLeft + xSize);

        int yOffset = guiTop;
        results.getArea().setLocation(guiLeft, yOffset);
        yOffset += results.getArea().getHeight();
        catalysts.getArea().setLocation(guiLeft, yOffset);
        yOffset += catalysts.getArea().getHeight();
        ingredients.getArea().setLocation(guiLeft, yOffset);
        yOffset += ingredients.getArea().getHeight() + 2;

        int xOffset = guiLeft;

        cancelButton.x = xOffset;
        cancelButton.y = yOffset;
        cancelButton.width = 54;
        xOffset += cancelButton.width + 6;

        saveButton.x = xOffset;
        saveButton.y = yOffset;
        saveButton.width = 54;
        xOffset += saveButton.width + 6;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        if (recipeLayout == null) {
            return;
        }

        drawContext.mouseX = mouseX;
        drawContext.mouseY = mouseY;
        drawContext.partialTicks = partialTicks;
        drawContext.hoverStack = null;

        results.draw(drawContext);
        catalysts.draw(drawContext);
        ingredients.draw(drawContext);

        cancelButton.drawButton(mc, mouseX, mouseY, partialTicks);
        saveButton.drawButton(mc, mouseX, mouseY, partialTicks);

        if (drawContext.hoverStack != null) {
            final Object rawStack = drawContext.hoverStack.getRawStack();
            final IIngredientRenderer<Object> renderer = ingredientRegistry.getIngredientRenderer(rawStack);
            final List<String> tooltip = renderer.getTooltip(mc, rawStack, ITooltipFlag.TooltipFlags.NORMAL);
            final FontRenderer fontRenderer = renderer.getFontRenderer(mc, rawStack);
            GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, 600, 400, -1, fontRenderer);
        }

        dragStack.draw(drawContext);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1) {

    }

    @Override
    protected void mouseClicked(int x, int y, int p_mouseClicked_3_) throws IOException {
        PlannerIngredientStack draggedStack = dragStack.getDraggedStack();
        for (InventoryWidget inventory : inventories) {
            if (inventory.getArea().contains(x, y)) {
                if (draggedStack != null) {
                    inventory.getIngredients().add(draggedStack);
                    dragStack.setDraggedStack(null);
                } else {
                    PlannerIngredientStack stack = inventory.getStackAt(x, y);
                    if (stack != null) {
                        inventory.getIngredients().remove(stack);
                        dragStack.setDraggedStack(stack);
                    } else if (inventory.mouseClicked(x, y, p_mouseClicked_3_)) {
                        return;
                    }
                }
                return;
            }
        }
        if (cancelButton.isMouseOver()) {
            mc.displayGuiScreen(parentScreen);
            return;
        }

        if (saveButton.isMouseOver()) {
            if (plan != null) {
                PlannerRecipe plannerRecipe = new PlannerRecipe(
                        compactItems(results.getIngredients()),
                        compactItems(ingredients.getIngredients()),
                        compactItems(catalysts.getIngredients()));
                plan.addRecipe(plannerRecipe);
            }
            mc.displayGuiScreen(parentScreen);
            return;
        }

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
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        for (InventoryWidget inventory : inventories) {
            inventory.handleMouseInput();
        }
    }

    @Override
    protected void keyTyped(char key, int eventKey) throws IOException {
        if (dragStack.keyTyped(key, eventKey)) {
            return;
        }

        super.keyTyped(key, eventKey);
    }

    public void setParentScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void setPlan(CraftingPlan plan) {
        this.plan = plan;
    }
}
