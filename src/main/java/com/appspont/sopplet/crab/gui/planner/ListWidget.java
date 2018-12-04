package com.appspont.sopplet.crab.gui.planner;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;

import java.util.List;

public class ListWidget<T extends RectangleWidget> extends GuiSlot implements Widget {

    protected final List<T> childWidgets = Lists.newArrayList();
    private DrawContext context;

    public ListWidget(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
        super(mc, width, height, top, bottom, slotHeight);
    }

    @Override
    protected int getSize() {
        return childWidgets.size();
    }

    @Override
    protected void elementClicked(int index, boolean selected, int mouseX, int mouseY) {
        final Widget goal = childWidgets.get(index);
        goal.mouseClicked(mouseX, mouseY, 0);
    }

    @Override
    protected boolean isSelected(int i) {
        return false;
    }

    @Override
    protected void drawBackground() {

    }

    @Override
    protected void drawSlot(int index, int x, int y, int height, int mouseX, int mouseY, float partialTicks) {
        if (y + 18 > this.top && y < this.bottom) {
            final RectangleWidget goal = childWidgets.get(index);
            goal.setLocation(x, y);
            goal.draw(context);
        }
    }

    @Override
    protected int getScrollBarX() {
        return this.width - 4;
    }

    @Override
    public int getListWidth() {
        return this.width;
    }

    @Override
    protected void overlayBackground(int p_overlayBackground_1_, int p_overlayBackground_2_, int p_overlayBackground_3_, int p_overlayBackground_4_) {
    }

    @Override
    public void draw(DrawContext context) {
        this.context = context;
        drawScreen(context.mouseX, context.mouseY, context.partialTicks);
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        return false;
    }
}
