package com.appspont.sopplet.crab.gui.planner;

import net.minecraft.client.gui.IGuiEventListener;

public interface Widget extends IGuiEventListener {
    void draw(DrawContext context);

    boolean mouseClicked(int x, int y, int button);
}
