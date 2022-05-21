package com.appspont.sopplet.crab.command;

import com.appspont.sopplet.crab.RecipeExporter;
import com.appspont.sopplet.crab.gui.GuiItemIconDumper;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.mojang.brigadier.CommandDispatcher;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrabCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("crab")
                .executes(commandContext -> execute(commandContext.getSource())));
    }

    public static int execute(CommandSource source) {
        Entity entity = source.getEntity();
        if (entity instanceof PlayerEntity) {
            final RecipeExporter recipeExporter = new RecipeExporter();
            recipeExporter.dumpRecipes(CrabJeiPlugin.getJeiRuntime().getRecipeManager());

            final GuiItemIconDumper guiItemIconDumper = new GuiItemIconDumper(32);
            Minecraft.getInstance().setScreen(guiItemIconDumper);
            return 1;
        }
        return 0;
    }
}
