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
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventoryWidget extends Gui implements Widget, Rectangleable {

    private static final ResourceLocation CREATIVE_INVENTORY_TAB_ITEMS = new ResourceLocation("textures/gui/container/creative_inventory/tab_items.png");
    private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");

    private final List<PlannerIngredientStack> ingredients;
    private final Rectangle area;
    private final IngredientRenderer ingredientRenderer;
    private final Minecraft mc;
    private final String name;
    private int inventoryRows = 2;
    private int inventoryColumns = 9;
    private int scrolledBy = 0;
    protected int mouseX;
    protected int mouseY;

    public InventoryWidget(Point pos,
                           IngredientRenderer ingredientRenderer,
                           Minecraft mc,
                           String name,
                           int inventoryRows) {
        this.ingredients = new ArrayList<>();
        this.area = new Rectangle(pos);
        this.area.setSize(194, inventoryRows * 18 + 21);
        this.ingredientRenderer = ingredientRenderer;
        this.mc = mc;
        this.name = name;
        this.inventoryRows = inventoryRows;
    }

    @Override
    public void draw(DrawContext context) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        mouseX = context.mouseX;
        mouseY = context.mouseY;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TAB_ITEMS);
//        mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
        this.drawTexturedModalRect(area.x, area.y, 0, 0, area.width, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(area.x, area.y + inventoryRows * 18 + 17, 0, 130, area.width, 4);

//        GlStateManager.enableLighting();
//        GlStateManager.enableDepth();

        mc.fontRenderer.drawString(name, area.x + 7, area.y + 6, 4210752);

        int ingredientLeft = area.x + 9;
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

        this.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);

        int rows = (int) Math.ceil((double) ingredients.size() / inventoryColumns);
        int excessRows = rows - inventoryRows;
        boolean hasScroll = excessRows > 0;
        double ratio = hasScroll ? (double) scrolledBy / excessRows : 0.0;
        int offset = (int) ((inventoryRows * 18 - 15) * ratio);
        this.drawTexturedModalRect(area.x + 175, area.y + 18 + offset, 232 + (hasScroll ? 0 : 12), 0, 12, 15);

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

    public void handleMouseInput() throws IOException {
        if (!area.contains(mouseX, mouseY)) {
            return;
        }
        int i = Mouse.getEventDWheel();
        if (i != 0) {
            int rows = (int) Math.ceil((double) ingredients.size() / inventoryColumns);
            int excessRows = rows - inventoryRows;
            if (excessRows > 0) {
                if (i < 0) {
                    if (rows - scrolledBy > inventoryRows) {
                        scrolledBy += 1;
                    }
                } else {
                    if (scrolledBy > 0) {
                        scrolledBy -= 1;
                    }
                }
            }
        }
    }
}
