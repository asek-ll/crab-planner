package com.appspont.sopplet.crab.command;

import com.appspont.sopplet.crab.PlannerContainer;
import com.appspont.sopplet.crab.gui.PlannerGui;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PlanCommand implements ICommand {
    @Override
    public String getName() {
        return "planner";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "planner";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
        final Entity commandSenderEntity = iCommandSender.getCommandSenderEntity();
        if (commandSenderEntity instanceof EntityPlayer) {
            final PlannerGui plannerGui = new PlannerGui(new PlannerContainer());
            Minecraft.getMinecraft().displayGuiScreen(plannerGui);
        }
        iCommandSender.sendMessage(new TextComponentString("Planner start"));
    }

    @Override
    public boolean checkPermission(MinecraftServer minecraftServer, ICommandSender iCommandSender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings, @Nullable BlockPos blockPos) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] strings, int i) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
