package com.appspont.sopplet.crab.gui.planner;

import net.minecraftforge.client.event.GuiScreenEvent;

public interface JeiMouseClickInterceptor {
    void interceptMouseClick(GuiScreenEvent.MouseClickedEvent.Pre event);
}
