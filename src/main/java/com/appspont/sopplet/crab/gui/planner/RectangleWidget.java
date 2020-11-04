package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.gui.planner.widget.Rectangleable;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class RectangleWidget extends Rectangle implements Widget, Rectangleable {
    protected Minecraft mc = Minecraft.getMinecraft();

    public void draw(DrawContext context) {
    }

    public boolean mouseClicked(int x, int y, int button) {
        return false;
    }

    @Override
    public Rectangle getArea() {
        return this;
    }
}
