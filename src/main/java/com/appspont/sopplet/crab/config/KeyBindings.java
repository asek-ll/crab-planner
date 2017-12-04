package com.appspont.sopplet.crab.config;

import com.appspont.sopplet.crab.CrabDumperMod;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class KeyBindings {
    private static final String CATEGORY_NAME = CrabDumperMod.MOD_ID + " (" + CrabDumperMod.MOD_ID + ')';

    public static final KeyBinding SHOW_PLANNER
            = new KeyBinding("key.crab.showPlanner", KeyConflictContext.GUI, Keyboard.KEY_L, CATEGORY_NAME);

    private static final List<KeyBinding> ALL_KEY_BINDINGS = ImmutableList.of(
            SHOW_PLANNER
    );

    public static void init() {
        for (KeyBinding keyBinding : ALL_KEY_BINDINGS) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

}
