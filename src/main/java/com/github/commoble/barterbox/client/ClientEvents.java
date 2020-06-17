package com.github.commoble.barterbox.client;

import com.github.commoble.barterbox.ContainerTypes;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents
{
	public static void addClientListeners(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addListener(ClientEvents::onClientSetup);
	}
	
	public static void onClientSetup(FMLClientSetupEvent event)
	{
		ScreenManager.registerFactory(ContainerTypes.BARTER_BOX_SELLER, BarterBoxSellerScreen::new);
	}
}
