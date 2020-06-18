package com.github.commoble.barterbox.client;

import com.github.commoble.barterbox.barter_box.BarterBoxSellerContainer;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

public class SetBarterItemScreen extends Screen
{
	public final BarterBoxSellerContainer container;
	
	public SetBarterItemScreen(BarterBoxSellerContainer container, ITextComponent titleIn)
	{
		super(titleIn);
		this.container = container;
	}

}
