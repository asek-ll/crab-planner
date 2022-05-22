package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.config.KeyBindings;
import com.appspont.sopplet.crab.gui.planner.JeiMouseClickInterceptor;
import com.appspont.sopplet.crab.gui.planner.PlannerGui;
import com.appspont.sopplet.crab.planner.CraftingPlan;
import com.appspont.sopplet.crab.jei.CrabJeiPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(CrabDumperMod.MOD_ID)
@Mod.EventBusSubscriber
public class CrabDumperMod {
    public static final String MOD_ID = "crab";

    public CrabDumperMod() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientStart());
    }

    private void clientStart() {
        MinecraftForge.EVENT_BUS.register(new RegisterCommandsEventListener());
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, false,
                GuiScreenEvent.MouseClickedEvent.Pre.class, this::onPreMouseClicked);

        KeyBindings.init();

        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false,
                InputEvent.KeyInputEvent.class, this::onPreGuiKeyPressedEvent);
    }

    public final void onPreMouseClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {
        Screen gui = event.getGui();
        if (gui instanceof JeiMouseClickInterceptor) {
            ((JeiMouseClickInterceptor) gui).interceptMouseClick(event);
        }
    }

    public final void onPreGuiKeyPressedEvent(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().screen == null) {
            InputMappings.Input input = InputMappings.getKey(event.getKey(), event.getScanCode());
            if (KeyBindings.SHOW_PLANNER.isActiveAndMatches(input)) {
                final CraftingPlan currentPlan = CrabJeiPlugin.getPlanStoreManager().getCurrentPlan();
                final PlannerGui plannerGui = new PlannerGui(currentPlan);
                Minecraft.getInstance().setScreen(plannerGui);
            }
        }
    }
}
