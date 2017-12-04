package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.block.PlannerBlock;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

@Mod( modid = CrabDumperMod.MOD_ID, version = CrabDumperMod.VERSION )
public class CrabDumperMod
{
  public static final String MOD_ID = "crab";
  public static final String VERSION = "1.0";

  public static final Block PLANNER_BLOCK = new PlannerBlock();

  @SidedProxy( clientSide = "com.appspont.sopplet.crab.ClientProxy", serverSide = "com.appspont.sopplet.crab.Proxy" )
  static private Proxy proxy;

  @EventHandler
  public void preInit( FMLPreInitializationEvent event )
  {
    proxy.preInit( event );
  }

  @EventHandler
  public void init( FMLInitializationEvent event )
  {
    proxy.init( event );
  }

  @EventHandler
  public void serverStart( FMLServerStartingEvent event )
  {
    event.registerServerCommand( new ExportCommand() );
  }
}
