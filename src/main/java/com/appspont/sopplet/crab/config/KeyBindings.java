package com.appspont.sopplet.crab.config;

import com.appspont.sopplet.crab.CrabDumperMod;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.List;

public class KeyBindings {
    private static final String CATEGORY_NAME = CrabDumperMod.MOD_ID + " (" + CrabDumperMod.MOD_ID + ')';

    public static final KeyBinding SHOW_PLANNER
            = new KeyBinding("key.crab.showPlanner", KeyConflictContext.IN_GAME, InputMappings.Type.SCANCODE, 65, CATEGORY_NAME);

    private static final List<KeyBinding> ALL_KEY_BINDINGS = ImmutableList.of(
            SHOW_PLANNER
    );

    public static void init() {
        for (KeyBinding keyBinding : ALL_KEY_BINDINGS) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

}
