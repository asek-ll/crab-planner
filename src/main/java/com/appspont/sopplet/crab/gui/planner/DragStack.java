package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.planner.ingredient.PlannerFluidStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerItemStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;

public class DragStack implements Widget {
    public static final StringTextComponent EMPTY_MESSAGE = new StringTextComponent("");
    private final IngredientRenderer ingredientRenderer;
    private int jeiLeft = Integer.MAX_VALUE;
    private PlannerIngredientStack draggedStack = null;
    private final TextFieldWidget sizeInput;
    private int mouseX;

    public DragStack(Minecraft mc, IngredientRenderer ingredientRenderer) {
        this.ingredientRenderer = ingredientRenderer;
        sizeInput = new TextFieldWidget(mc.font, 1, 0, 60, 18, EMPTY_MESSAGE);
    }

    public void setJeiLeft(int jeiLeft) {
        this.jeiLeft = jeiLeft;
    }

    public void setDraggedStack(PlannerIngredientStack stack) {
        draggedStack = stack;
        if (stack != null) {
            sizeInput.setFocus(true);
            sizeInput.setValue(String.valueOf(stack.getAmount()));
        } else {
            sizeInput.setFocus(false);
            sizeInput.setMessage(EMPTY_MESSAGE);
        }
    }

    public PlannerIngredientStack getDraggedStack() {
        return draggedStack;
    }

    @Override
    public void draw(DrawContext context) {
        mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (draggedStack != null) {
            sizeInput.x = mouseX + 8;
            sizeInput.y = mouseY - 8;
            sizeInput.render(context.ms, mouseX, mouseY, context.partialTicks);
            ingredientRenderer.render(mouseX - 8, mouseY - 8, draggedStack, context);
        }
    }

    public boolean keyTyped(int eventKey, int o1, int o2) {

        if (draggedStack != null) {
            if (eventKey == 211) {
                setDraggedStack(null);
                return true;
            }
            if (sizeInput.keyPressed(eventKey, o1, o2)) {
                int amount;
                try {
                    amount = Math.max(1, Integer.parseInt(sizeInput.getValue()));
                } catch (NumberFormatException ignored) {
                    amount = 1;
                }
                draggedStack.setAmount(amount);
                return true;
            }
        }

        return false;
    }

    public boolean charTyped(char ch, int param1) {

        if (draggedStack != null && sizeInput.charTyped(ch, param1)) {
            int amount;
            try {
                amount = Math.max(1, Integer.parseInt(sizeInput.getValue()));
            } catch (NumberFormatException ignored) {
                amount = 1;
            }
            draggedStack.setAmount(amount);
            return true;
        }

        return false;
    }

    public boolean interceptMouseClick(double mouseX, double mouseY, int button) {
        if (button > -1) {
            if (mouseX > jeiLeft && draggedStack != null) {
                setDraggedStack(null);
                return true;
            }
            Object ingredientUnderMouse =
                    CrabJeiPlugin.getJeiRuntime().getIngredientListOverlay().getIngredientUnderMouse();

            if (ingredientUnderMouse == null) {
                ingredientUnderMouse = CrabJeiPlugin.getJeiRuntime().getBookmarkOverlay().getIngredientUnderMouse();
            }

            if (ingredientUnderMouse != null) {
                PlannerIngredientStack stack = null;
                if (ingredientUnderMouse instanceof ItemStack) {
                    stack = new PlannerItemStack(((ItemStack) ingredientUnderMouse).copy());
                } else if (ingredientUnderMouse instanceof FluidStack) {
                    stack = new PlannerFluidStack(((FluidStack) ingredientUnderMouse).copy());
                }
                if (stack != null) {
                    setDraggedStack(stack);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        return false;
    }
}
