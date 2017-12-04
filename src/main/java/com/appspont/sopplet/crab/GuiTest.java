package com.appspont.sopplet.crab;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiTest extends GuiScreen
{
  @Override public void drawScreen( int mouseX, int mouseY, float partialTicks )
  {
    this.drawDefaultBackground();
    super.drawScreen( mouseX, mouseY, partialTicks );
  }

  @Override
  public void initGui()
  {
    this.buttonList.add( new GuiButton( 0, this.width / 2 - 100, this.height / 2 - 24, "This is button a" ) );
    this.buttonList.add( new GuiButton( 1, this.width / 2 - 100, this.height / 2 + 4, "This is button b" ) );
  }
}
