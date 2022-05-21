package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.command.CrabCommand;
import com.appspont.sopplet.crab.command.PlanCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegisterCommandsEventListener {

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        CrabCommand.register(event.getDispatcher());
        PlanCommand.register(event.getDispatcher());
    }

}
