package com.appspont.sopplet.crab.command;

import com.appspont.sopplet.crab.planner.CraftingPlan;
import com.appspont.sopplet.crab.gui.planner.PlannerGui;
import com.appspont.sopplet.crab.jei.CrabJeiPlugin;
import com.mojang.brigadier.CommandDispatcher;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PlanCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("planner")
                .executes(commandContext -> execute(commandContext.getSource())));
    }

    public static int execute(CommandSource source) {
        Entity entity = source.getEntity();
        if (entity instanceof PlayerEntity) {
            final CraftingPlan currentPlan = CrabJeiPlugin.getPlanStoreManager().getCurrentPlan();
            final PlannerGui plannerGui = new PlannerGui(currentPlan);
            Minecraft.getInstance().setScreen(plannerGui);
            source.sendSuccess(new StringTextComponent("Planner start"), false);
            return 1;
        }
        return 0;
    }

}
