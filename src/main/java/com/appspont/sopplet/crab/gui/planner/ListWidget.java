package com.appspont.sopplet.crab.gui.planner;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import org.lwjgl.input.Mouse;

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

    @Override
    public void handleMouseInput() {
        if (this.isMouseYWithinSlotBounds(this.mouseY)) {
            int i2;
            int j2;
            int k2;
            int l2;
            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
                i2 = left + (this.width - this.getListWidth()) / 2;
                j2 = left + (this.width + this.getListWidth()) / 2;
                k2 = this.mouseY - this.top - this.headerPadding + (int) this.amountScrolled - 4;
                l2 = k2 / this.slotHeight;
                if (l2 < this.getSize() && this.mouseX >= i2 && this.mouseX <= j2 && l2 >= 0 && k2 >= 0) {
                    this.selectedElement = l2;

                    final Widget goal = childWidgets.get(l2);
                    if (goal.mouseClicked(mouseX, mouseY, 0)) {
                        return;
                    }

                } else if (this.mouseX >= i2 && this.mouseX <= j2 && k2 < 0) {
                    this.clickedHeader(this.mouseX - i2, this.mouseY - this.top + (int) this.amountScrolled - 4);
                }
            }
        }

        super.handleMouseInput();
    }

    @Override
    public void setDimensions(int width, int height, int top, int bottom) {
        super.setDimensions(width, height, top, bottom);
        for (int i = 0; i < childWidgets.size(); i++) {
            final T t = childWidgets.get(i);
            t.width = width;
        }
    }
}
