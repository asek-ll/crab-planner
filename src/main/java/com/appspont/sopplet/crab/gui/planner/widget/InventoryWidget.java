package com.appspont.sopplet.crab.gui.planner.widget;

import com.appspont.sopplet.crab.gui.planner.DrawContext;
import com.appspont.sopplet.crab.gui.planner.IngredientRenderer;
import com.appspont.sopplet.crab.gui.planner.Widget;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.List;

public class InventoryWidget extends Gui implements Widget, Rectangleable {

    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    private final List<PlannerIngredientStack> ingredients;
    private final Rectangle area;
    private final IngredientRenderer ingredientRenderer;
    private final Minecraft mc;
    private final String name;
    private final GuiButton scrollUpButton;
    private final GuiButton scrollDownButton;
    private int inventoryRows = 2;
    private int inventoryColumns = 9;
    private int scrolledBy = 0;

    public InventoryWidget(List<PlannerIngredientStack> ingredients,
                           Rectangle area,
                           IngredientRenderer ingredientRenderer,
                           Minecraft mc,
                           String name,
                           int inventoryRows) {
        this.ingredients = ingredients;
        this.area = area;
        this.ingredientRenderer = ingredientRenderer;
        this.mc = mc;
        this.name = name;
        this.inventoryRows = inventoryRows;

        scrollUpButton = new GuiButton(1, 0, 0, 18, 18, "^");
        scrollDownButton = new GuiButton(1, 0, 0, 18, 18, "\\/");
    }

    @Override
    public void draw(DrawContext context) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
//        mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
        this.drawTexturedModalRect(area.x, area.y, 0, 0, area.width, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(area.x, area.y + inventoryRows * 18 + 17, 0, 214, area.width, 8);

        mc.fontRenderer.drawString(name, area.x + 7, area.y + 6, 4210752);

        int ingredientLeft = area.x + 8;
        int ingredientTop = area.y + 18;

        int from = scrolledBy * inventoryColumns;
        int to = Math.min(from + inventoryRows * inventoryColumns, ingredients.size());

        int i = 0;
        if (to > from) {
            while (from + i < to) {
                int y = i / inventoryColumns;
                int x = i % inventoryColumns;
                int x1 = ingredientLeft + x * 18;
                int y1 = ingredientTop + y * 18;
                ingredientRenderer.render(x1, y1, ingredients.get(from + i), context);

                if (context.mouseX > x1 && context.mouseX < x1 + 18 && context.mouseY > y1 && context.mouseY < y1 + 18) {
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.colorMask(true, true, true, false);
                    this.drawGradientRect(x1, y1, x1 + 16, y1 + 16, -2130706433, -2130706433);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                }


                i += 1;
            }
        }

        if (scrolledBy > 0) {
            scrollUpButton.enabled = true;
            scrollUpButton.y = area.y;
            scrollUpButton.x = ingredientLeft + inventoryColumns * 18 + 6;
            scrollUpButton.drawButton(mc, context.mouseX, context.mouseY, context.partialTicks);
        } else {
            scrollUpButton.enabled = false;
        }

        int rows = (int)Math.ceil((double) ingredients.size() / inventoryColumns);
        if (rows - scrolledBy > inventoryRows) {
            scrollDownButton.enabled = true;
            scrollDownButton.y = ingredientTop + inventoryRows * 18 - 14;
            scrollDownButton.x = ingredientLeft + inventoryColumns * 18 + 6;
            scrollDownButton.drawButton(mc, context.mouseX, context.mouseY, context.partialTicks);
        } else {
            scrollDownButton.enabled = false;
        }

//        int rgb = Color.RED.getRGB();
//        drawHorizontalLine(area.x, area.x + area.width, area.y, rgb);
//        drawVerticalLine(area.x, area.y, area.y + area.height, rgb);
//        drawHorizontalLine(area.x, area.x + area.width, area.y + area.height, rgb);
//        drawVerticalLine(area.x + area.width, area.y, area.y + area.height, rgb);
    }

    @Override
    public Rectangle getArea() {
        return area;
    }

    public List<PlannerIngredientStack> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        if (scrollUpButton.isMouseOver() && scrolledBy > 0 && scrollUpButton.enabled) {
            scrolledBy -= 1;
            return true;
        }
        if (scrollDownButton.isMouseOver() && scrollDownButton.enabled) {
            int rows = (int)Math.ceil((double) ingredients.size() / inventoryColumns);
            if (rows - scrolledBy > inventoryRows) {
                scrolledBy += 1;
            }
            return true;
        }
        return false;
    }

    public void setStacks(List<PlannerIngredientStack> result) {
        ingredients.clear();
        ingredients.addAll(result);
    }

    public PlannerIngredientStack getStackAt(int x, int y) {
        int ingredientLeft = area.x + 8;
        int ingredientTop = area.y + 18;

        int column = (x - ingredientLeft) / 18;
        int row = (y - ingredientTop) / 18;

        if (column < inventoryColumns && row < inventoryRows) {
            int index = (row + scrolledBy) * inventoryColumns + column;
            if (index < ingredients.size()) {
                return ingredients.get(index);
            }
        }

        return null;
    }
}
