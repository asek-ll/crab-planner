package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.planner.ingredient.PlannerFluidStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerItemStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class DragStack implements Widget {
    private final IngredientRenderer ingredientRenderer;
    private int jeiLeft = Integer.MAX_VALUE;
    private PlannerIngredientStack draggedStack = null;
    private final GuiTextField sizeInput;
    private int mouseX;

    public DragStack(Minecraft mc, IngredientRenderer ingredientRenderer) {
        this.ingredientRenderer = ingredientRenderer;
        sizeInput = new GuiTextField(0, mc.fontRenderer, 1, 0, 60, 18);
    }

    public void setJeiLeft(int jeiLeft) {
        this.jeiLeft = jeiLeft;
    }

    public void setDraggedStack(PlannerIngredientStack stack) {
        draggedStack = stack;
        if (stack != null) {
            sizeInput.setFocused(true);
            sizeInput.setText(String.valueOf(stack.getAmount()));
        } else {
            sizeInput.setFocused(false);
            sizeInput.setText("");
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
            sizeInput.drawTextBox();
            ingredientRenderer.render(mouseX - 8, mouseY - 8, draggedStack, context);
        }
    }

    public boolean keyTyped(char key, int eventKey) throws IOException {

        if (draggedStack != null) {
            if (eventKey == 211) {
                setDraggedStack(null);
                return true;
            }
            if (sizeInput.textboxKeyTyped(key, eventKey)) {
                int amount;
                try {
                    amount = Math.max(1, Integer.parseInt(sizeInput.getText()));
                } catch (NumberFormatException ignored) {
                    amount = 1;
                }
                draggedStack.setAmount(amount);
                return true;
            }
        }

        return false;
    }

    public boolean interceptMouseClick() {
        if (Mouse.getEventButton() > -1) {
            if (Mouse.getEventButtonState()) {
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
        }

        return false;
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        return false;
    }
}
