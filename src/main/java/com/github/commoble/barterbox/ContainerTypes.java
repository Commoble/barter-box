package com.github.commoble.barterbox;

import com.github.commoble.barterbox.barter_box.BarterBoxSellerContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BarterBox.MODID)
public class ContainerTypes
{
	@ObjectHolder(Names.BARTER_BOX_SELLER)
	public static final ContainerType<BarterBoxSellerContainer> BARTER_BOX_SELLER = null;
}
