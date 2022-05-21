package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.gui.planner.widget.Rectangleable;

import java.awt.*;
import java.util.List;

public class WidgetContainer<T extends Rectangleable & Widget> extends RectangleWidget {
    private final List<T> child;

    public WidgetContainer(List<T> child) {
        this.child = child;
        updateBounds();
    }

    public void updateBounds() {
        if (!child.isEmpty()) {
            Rectangle rect = child.iterator().next().getArea();
            for (T t : child) {
                rect = rect.union(t.getArea());
            }
            this.rect.setBounds(rect);
        }

    }

    @Override
    public void draw(DrawContext drawContext) {
        for (T t : child) {
            t.draw(drawContext);
        }
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        for (T t : child) {
            if (t.getArea().contains(x, y)) {
                if (t.mouseClicked(x, y, button)) {
                    return true;
                }
            }
        }
        return false;
    }
}
