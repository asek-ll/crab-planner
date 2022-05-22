package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.planner.CraftingPlan;
import com.appspont.sopplet.crab.planner.PlannerRecipe;
import com.appspont.sopplet.crab.container.RecipeContainer;
import com.appspont.sopplet.crab.gui.planner.widget.DragStack;
import com.appspont.sopplet.crab.gui.planner.renderer.IngredientRenderer;
import com.appspont.sopplet.crab.gui.planner.widget.InventoryWidget;
import com.appspont.sopplet.crab.planner.ingredient.PlannerFluidStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredient;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerItemStack;
import com.appspont.sopplet.crab.jei.CrabJeiPlugin;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.appspont.sopplet.crab.gui.planner.PlannerGui.NO_PRESSABLE;

public class RecipeGui extends ContainerScreen<RecipeContainer> implements JeiMouseClickInterceptor {

    private final DrawContext drawContext;
    private final IngredientRenderer ingredientRenderer;
    private final InventoryWidget results;
    private final InventoryWidget catalysts;
    private final InventoryWidget ingredients;
    private final IIngredientManager ingredientRegistry;
    private final List<InventoryWidget> inventories;
    private final Button saveButton;
    private final Button cancelButton;
    private DragStack dragStack;
    private Screen parentScreen;
    private CraftingPlan plan;

    public RecipeGui(IIngredientManager ingredientRegistry, Minecraft mc) {
        super(new RecipeContainer(), mc.player.inventory, new StringTextComponent("Recipe"));
        this.ingredientRegistry = ingredientRegistry;
        ingredientRenderer = new IngredientRenderer(this.ingredientRegistry, mc);
        drawContext = new DrawContext();

        results = new InventoryWidget(new Point(100, 100), ingredientRenderer, mc, "Result", 2);
        catalysts = new InventoryWidget(new Point(100, 160), ingredientRenderer, mc, "Catalyst", 2);
        ingredients = new InventoryWidget(new Point(100, 220), ingredientRenderer, mc, "Ingredients", 2);

        inventories = Arrays.asList(results, catalysts, ingredients);

        dragStack = new DragStack(mc, ingredientRenderer);

        saveButton = new Button(0, 0, 70 - 8, 20, new StringTextComponent("Save"), NO_PRESSABLE);
        cancelButton = new Button(0, 0, 70 - 8, 20, new StringTextComponent("Cancel"), NO_PRESSABLE);
    }

    public void setRecipe(IRecipeLayout recipeLayout) {
        setRecipe(prepareRecipe(recipeLayout));
    }

    public void setRecipe(PlannerRecipe plannerRecipe) {
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
        IRecipeManager recipeRegistry = CrabJeiPlugin.getJeiRuntime().getRecipeManager();
        List<Object> recipeCatalysts = recipeRegistry
                .getRecipeCatalysts(iRecipeLayout.getRecipeCategory(), false);

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
    public void init(Minecraft mc, int width, int height) {
        super.init(mc, width, height);
        int ySize = 240;
        int xSize = 200;
        this.leftPos = (this.width - xSize) / 2;
        this.topPos = (this.height - ySize) / 2;
        this.imageHeight = ySize;

        dragStack.setDraggedStack(null);
        dragStack.setJeiLeft(leftPos + xSize);

        int yOffset = topPos;

        results.getArea().setLocation(leftPos, yOffset);
        yOffset += results.getArea().getHeight();
        addWidget(results);

        catalysts.getArea().setLocation(leftPos, yOffset);
        yOffset += catalysts.getArea().getHeight();
        addWidget(catalysts);

        ingredients.getArea().setLocation(leftPos, yOffset);
        yOffset += ingredients.getArea().getHeight() + 2;
        addWidget(ingredients);

        int xOffset = leftPos;

        cancelButton.x = xOffset;
        cancelButton.y = yOffset;
        cancelButton.setWidth(54);
        xOffset += cancelButton.getWidth() + 6;

        saveButton.x = xOffset;
        saveButton.y = yOffset;
        saveButton.setWidth(54);
        xOffset += saveButton.getWidth() + 6;

        menu.getLayout().ifPresent(this::setRecipe);
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);

        drawContext.mouseX = mouseX;
        drawContext.mouseY = mouseY;
        drawContext.partialTicks = partialTicks;
        drawContext.hoverStack = null;
        drawContext.ms = ms;

        results.draw(drawContext);
        catalysts.draw(drawContext);
        ingredients.draw(drawContext);

        cancelButton.render(ms, mouseX, mouseY, partialTicks);
        saveButton.render(ms, mouseX, mouseY, partialTicks);

        if (drawContext.hoverStack != null) {
            final Object rawStack = drawContext.hoverStack.getRawStack();
            final IIngredientRenderer<Object> renderer = ingredientRegistry.getIngredientRenderer(rawStack);
            final List<ITextComponent> tooltip = renderer.getTooltip(rawStack, ITooltipFlag.TooltipFlags.NORMAL);
            final FontRenderer fontRenderer = renderer.getFontRenderer(minecraft, rawStack);
            GuiUtils.drawHoveringText(ms, tooltip, mouseX, mouseY, 600, 400, -1, fontRenderer);
        }

        dragStack.draw(drawContext);
    }

    @Override
    protected void renderBg(MatrixStack ms, float partial, int mouseX, int mouseY) {
        renderBackground(ms);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (cancelButton.isMouseOver(mouseX, mouseY)) {
            minecraft.setScreen(parentScreen);
            return true;
        }

        if (saveButton.isMouseOver(mouseX, mouseY)) {
            if (plan != null) {
                PlannerRecipe plannerRecipe = new PlannerRecipe(
                        compactItems(results.getIngredients()),
                        compactItems(ingredients.getIngredients()),
                        compactItems(catalysts.getIngredients()));
                plan.addRecipe(plannerRecipe);
            }
            minecraft.setScreen(parentScreen);
            return true;
        }

        PlannerIngredientStack draggedStack = dragStack.getDraggedStack();
        for (InventoryWidget inventory : inventories) {
            if (inventory.getArea().contains(mouseX, mouseY)) {
                if (draggedStack != null) {
                    inventory.getIngredients().add(draggedStack);
                    dragStack.setDraggedStack(null);
                    return true;
                }
                PlannerIngredientStack stack = inventory.getStackAt(mouseX, mouseY);
                if (stack != null) {
                    inventory.getIngredients().remove(stack);
                    dragStack.setDraggedStack(stack);
                    return true;
                }
                if (inventory.mouseClicked((int) mouseX, (int) mouseY, button)) {
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int key, int code, int m) {
        if (dragStack.keyTyped(key, code, m)) {
            return true;
        }
        return super.keyPressed(key, code, m);
    }

    @Override
    public boolean charTyped(char ch, int param1) {
        if (dragStack.charTyped(ch, param1)) {
            return true;
        }
        return super.charTyped(ch, param1);
    }

    @Override
    public void interceptMouseClick(GuiScreenEvent.MouseClickedEvent.Pre event) {
        if (dragStack.interceptMouseClick(event.getMouseX(), event.getMouseY(), event.getButton())) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        }
    }


    public void setParentScreen(Screen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void setPlan(CraftingPlan plan) {
        this.plan = plan;
    }

    @Override
    public void onClose() {
        if (parentScreen != null) {
            minecraft.setScreen(parentScreen);
        }
    }
}
