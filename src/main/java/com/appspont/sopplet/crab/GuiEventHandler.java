package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.config.KeyBindings;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class GuiEventHandler
{
  @SubscribeEvent
  public void onGuiKeyboardEvent( GuiScreenEvent.KeyboardInputEvent.Post event )
  {
    int eventKey = Keyboard.getEventKey();

    if ( KeyBindings.SHOW_PLANNER.isActiveAndMatches( eventKey ) )
    {
      if (event.isCancelable()) {
        event.setCanceled( true );
      }
    }
  }
}
