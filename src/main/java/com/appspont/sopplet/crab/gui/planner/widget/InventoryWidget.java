package com.appspont.sopplet.crab.gui.planner.widget;

import com.appspont.sopplet.crab.gui.planner.DrawContext;
import com.appspont.sopplet.crab.gui.planner.renderer.IngredientRenderer;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryWidget extends AbstractGui implements Widget, Rectangleable {

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
//        GlStateManager.disableLighting();
//        GlStateManager.disableDepth();
//        GlStateManager.disableAlpha();
//        GlStateManager.disableBlend();

        mouseX = context.mouseX;
        mouseY = context.mouseY;

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bind(CREATIVE_INVENTORY_TAB_ITEMS);
        this.blit(context.ms, area.x, area.y, 0, 0, area.width, this.inventoryRows * 18 + 17);
        this.blit(context.ms, area.x, area.y + inventoryRows * 18 + 17, 0, 130, area.width, 4);

        mc.font.draw(context.ms, name, area.x + 7, area.y + 6, 4210752);

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
//                    GlStateManager.disableLighting();
//                    GlStateManager.disableDepth();
//                    GlStateManager.colorMask(true, true, true, false);
//                    this.drawGradientRect(x1, y1, x1 + 16, y1 + 16, -2130706433, -2130706433);
                    this.fillGradient(context.ms, x1, y1, x1 + 16, y1 + 16, -2130706433, -2130706433);
//                    GlStateManager.colorMask(true, true, true, true);
//                    GlStateManager.enableLighting();
//                    GlStateManager.enableDepth();
                }


                i += 1;
            }
        }

//        GlStateManager.disableLighting();
//        GlStateManager.disableDepth();
//        GlStateManager.disableAlpha();
//        GlStateManager.disableBlend();


        this.mc.getTextureManager().bind(CREATIVE_INVENTORY_TABS);

        int rows = (int) Math.ceil((double) ingredients.size() / inventoryColumns);
        int excessRows = rows - inventoryRows;
        boolean hasScroll = excessRows > 0;
        double ratio = hasScroll ? (double) scrolledBy / excessRows : 0.0;
        int offset = (int) ((inventoryRows * 18 - 15) * ratio);
        this.blit(context.ms, area.x + 175, area.y + 18 + offset, 232 + (hasScroll ? 0 : 12), 0, 12, 15);

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

    public PlannerIngredientStack getStackAt(double x, double y) {
        int ingredientLeft = area.x + 8;
        int ingredientTop = area.y + 18;

        int column = (int)((x - ingredientLeft) / 18);
        int row = (int)((y - ingredientTop) / 18);

        if (column < inventoryColumns && row < inventoryRows) {
            int index = (row + scrolledBy) * inventoryColumns + column;
            if (index < ingredients.size()) {
                return ingredients.get(index);
            }
        }

        return null;
    }

    @Override
    public boolean mouseScrolled(double scrollX, double scrollY, double scrollDelta) {
        if (scrollDelta != 0) {
            int rows = (int) Math.ceil((double) ingredients.size() / inventoryColumns);
            int excessRows = rows - inventoryRows;
            if (excessRows > 0) {
                if (scrollDelta < 0) {
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
        return Widget.super.mouseScrolled(scrollX, scrollY, scrollDelta);
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        return getArea().contains(x, y);
    }
}
