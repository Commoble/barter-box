package com.github.commoble.barterbox;

import com.github.commoble.barterbox.barter_box.BarterBoxTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BarterBox.MODID)
public class TileEntityTypes
{
	@ObjectHolder(Names.BARTER_BOX)
	public static final TileEntityType<BarterBoxTileEntity> BARTER_BOX = null;
}
