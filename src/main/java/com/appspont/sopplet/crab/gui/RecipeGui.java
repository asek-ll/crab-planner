package com.appspont.sopplet.crab.gui;

import com.appspont.sopplet.crab.CraftingPlan;
import com.appspont.sopplet.crab.PlannerRecipe;
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
import net.minecraft.client.gui.GuiTextField;
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
import java.util.*;
import java.util.List;
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
    private PlannerIngredientStack draggedStack = null;
    private final GuiTextField sizeInput;
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

        results = new InventoryWidget(new ArrayList<>(), new Rectangle(100, 100, 200, 60), ingredientRenderer, mc, "Result", 2);
        catalysts = new InventoryWidget(new ArrayList<>(), new Rectangle(100, 160, 200, 60), ingredientRenderer, mc, "Catalyst", 2);
        ingredients = new InventoryWidget(new ArrayList<>(), new Rectangle(100, 220, 200, 60), ingredientRenderer, mc, "Ingredients", 2);

        inventories = Arrays.asList(results, catalysts, ingredients);

        sizeInput = new GuiTextField(0, mc.fontRenderer, 1, 0, 18, 18);
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
        this.draggedStack = null;

        int yOffset = guiTop;
        results.getArea().setLocation(guiLeft, yOffset);
        yOffset += results.getArea().getHeight();
        catalysts.getArea().setLocation(guiLeft, yOffset);
        yOffset += catalysts.getArea().getHeight();
        ingredients.getArea().setLocation(guiLeft, yOffset);
        yOffset += ingredients.getArea().getHeight() + 2;

        int xOffset = guiLeft;

        sizeInput.x = xOffset;
        sizeInput.y = yOffset;
        sizeInput.width = 60;
        xOffset += sizeInput.width + 6;

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

        sizeInput.drawTextBox();
        cancelButton.drawButton(mc, mouseX, mouseY, partialTicks);
        saveButton.drawButton(mc, mouseX, mouseY, partialTicks);

        if (drawContext.hoverStack != null) {
            final Object rawStack = drawContext.hoverStack.getRawStack();
            final IIngredientRenderer<Object> renderer = ingredientRegistry.getIngredientRenderer(rawStack);
            final List<String> tooltip = renderer.getTooltip(mc, rawStack, ITooltipFlag.TooltipFlags.NORMAL);
            final FontRenderer fontRenderer = renderer.getFontRenderer(mc, rawStack);
            GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, 600, 400, -1, fontRenderer);
        }

        if (draggedStack != null) {
            ingredientRenderer.render(mouseX - 8, mouseY - 8, draggedStack, drawContext);
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1) {

    }

    @Override
    protected void mouseClicked(int x, int y, int p_mouseClicked_3_) throws IOException {
        for (InventoryWidget inventory : inventories) {
            if (inventory.getArea().contains(x, y)) {
                if (draggedStack != null) {
                    inventory.getIngredients().add(draggedStack);
                    setDraggedStack(null);
                } else {
                    PlannerIngredientStack stack = inventory.getStackAt(x, y);
                    if (stack != null) {
                        inventory.getIngredients().remove(stack);
                        setDraggedStack(stack);
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

    private void setDraggedStack(PlannerIngredientStack stack) {
        draggedStack = stack;
        if (stack != null) {
            sizeInput.setFocused(true);
            sizeInput.setText(String.valueOf(stack.getAmount()));
        } else {
            sizeInput.setFocused(false);
            sizeInput.setText("");
        }
    }

    private boolean interceptMouseClick() {
        if (Mouse.getEventButton() > -1) {
            if (Mouse.getEventButtonState()) {
                int x = Mouse.getEventX() * width / mc.displayWidth;
                if (x > (guiLeft + xSize) && draggedStack != null) {
                    setDraggedStack(null);
                    return true;
                }
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
                        setDraggedStack(stack);
                    }
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
    protected void keyTyped(char key, int eventKey) throws IOException {

        if (sizeInput.textboxKeyTyped(key, eventKey)) {
            if (draggedStack != null) {
                int amount;
                try {
                    amount = Math.max(1, Integer.parseInt(sizeInput.getText()));
                } catch (NumberFormatException ignored) {
                    amount = 1;
                }
                draggedStack.setAmount(amount);
            }
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
