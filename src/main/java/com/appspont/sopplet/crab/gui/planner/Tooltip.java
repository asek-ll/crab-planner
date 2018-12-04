package com.appspont.sopplet.crab.gui.planner;

import net.minecraftforge.fml.client.config.HoverChecker;

public abstract class Tooltip<T> {
    protected final T source;
    private final HoverChecker hoverChecker;
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;
    protected boolean removed = false;

    protected Tooltip(T source, int x, int y, int width, int height) {
        this.source = source;
        this.hoverChecker = new HoverChecker(y, y + height, x, x + width, 0);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isHover(int x, int y) {
        return hoverChecker.checkHover(x, y);
    }

    public boolean isSameCaller(Object caller) {
        return source.equals(caller);
    }

    public boolean isRemoved() {
        return removed;
    }

    public abstract void draw(int x, int y);

    public abstract void handleClick(int x, int y, int button);
}
