package com.appspont.sopplet.crab.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class PlannerBlock extends Block
{
  public PlannerBlock()
  {
    super( Material.ROCK );
    setUnlocalizedName( "planner_block" );
    setCreativeTab( CreativeTabs.BUILDING_BLOCKS );
    setHardness( 2 );
    setResistance( 10 );
  }
}
