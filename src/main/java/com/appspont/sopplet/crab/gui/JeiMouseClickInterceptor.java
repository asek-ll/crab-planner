package com.appspont.sopplet.crab.gui;

import net.minecraftforge.client.event.GuiScreenEvent;

public interface JeiMouseClickInterceptor {
    void interceptMouseClick(GuiScreenEvent.MouseClickedEvent.Pre event);
}
