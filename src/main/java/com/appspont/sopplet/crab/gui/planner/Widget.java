package com.appspont.sopplet.crab.gui.planner;

public interface Widget {
    void draw(DrawContext context);

    boolean mouseClicked(int x, int y, int button);
}
