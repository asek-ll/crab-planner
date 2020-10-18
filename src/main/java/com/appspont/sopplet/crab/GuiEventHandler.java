package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.config.KeyBindings;
import com.appspont.sopplet.crab.gui.PlannerGui;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class GuiEventHandler {
    @SubscribeEvent
    public void onGuiKeyboardEvent(InputEvent.KeyInputEvent event) {
        int eventKey = Keyboard.getEventKey();

        if (KeyBindings.SHOW_PLANNER.isActiveAndMatches(eventKey)) {
            final CraftingPlan currentPlan = CrabJeiPlugin.getPlanStoreManager().getCurrentPlan();
            final PlannerGui plannerGui = new PlannerGui(currentPlan);
            Minecraft.getMinecraft().displayGuiScreen(plannerGui);
        }
    }
}
