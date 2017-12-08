package com.appspont.sopplet.crab;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class PlannerCommand implements ICommand {
    @Override
    @Nonnull
    public String getCommandName() {
        return "crab";
    }

    @Override
    @Nonnull
    public String getCommandUsage(@Nonnull ICommandSender iCommandSender) {
        return "crab <thing>";
    }

    @Override
    @Nonnull
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(@Nonnull MinecraftServer minecraftServer,
                        @Nonnull ICommandSender iCommandSender,
                        @Nonnull String[] strings) throws CommandException {

        final Entity commandSenderEntity = iCommandSender.getCommandSenderEntity();
        if (commandSenderEntity instanceof EntityPlayer) {
//            final PlannerGui plannerGui = new PlannerGui(new PlannerContainer());
//            Minecraft.getMinecraft().displayGuiScreen(plannerGui);

            final GuiItemIconDumper guiItemIconDumper = new GuiItemIconDumper(32);
            Minecraft.getMinecraft().displayGuiScreen(guiItemIconDumper);
        }

        iCommandSender.addChatMessage(new TextComponentString("Hello world"));
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer minecraftServer, @Nonnull ICommandSender iCommandSender) {
        return true;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletionOptions(@Nonnull MinecraftServer minecraftServer,
                                                @Nonnull ICommandSender iCommandSender,
                                                @Nonnull String[] strings, @Nullable BlockPos blockPos) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] strings, int i) {
        return false;
    }

    @Override
    public int compareTo(@Nonnull ICommand o) {
        return 0;
    }
}
