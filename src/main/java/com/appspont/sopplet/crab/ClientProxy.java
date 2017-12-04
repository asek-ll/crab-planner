package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.config.KeyBindings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends Proxy
{
  @Override public void init( FMLInitializationEvent event )
  {
    KeyBindings.init();
    MinecraftForge.EVENT_BUS.register( new GuiEventHandler() );
  }
}
