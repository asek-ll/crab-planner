package com.appspont.sopplet.crab.gui.planner.widget;

import com.appspont.sopplet.crab.gui.planner.DrawContext;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractList;

import java.awt.*;

public abstract class RectangleWidget extends AbstractList.AbstractListEntry<RectangleWidget> implements Widget, Rectangleable {
    protected final Rectangle rect;
    protected Minecraft mc = Minecraft.getInstance();

    public RectangleWidget(Rectangle rect) {
        this.rect = rect;
    }

    public RectangleWidget() {
        this.rect = new Rectangle();
    }

    public void draw(DrawContext context) {
    }

    public boolean mouseClicked(int x, int y, int button) {
        return false;
    }

    @Override
    public Rectangle getArea() {
        return rect;
    }

    @Override
    public void render(MatrixStack ms, int idx, int top, int left, int width, int height, int mouseX, int mouseY, boolean isOver, float partial) {
        rect.setBounds(left, top, width, height);
        DrawContext ctx;
        if (this.list instanceof ListWidget) {
            ctx = ((ListWidget<?>) this.list).getContext();
        } else {
            ctx = new DrawContext();
            ctx.mouseX = mouseX;
            ctx.mouseY = mouseY;
            ctx.ms = ms;
            ctx.partialTicks = partial;

        }
        draw(ctx);
    }

}
