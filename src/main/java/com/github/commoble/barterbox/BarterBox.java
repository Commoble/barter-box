package com.github.commoble.barterbox;

import com.github.commoble.barterbox.barter_box.BarterBoxBlock;
import com.github.commoble.barterbox.barter_box.BarterBoxSellerContainer;
import com.github.commoble.barterbox.barter_box.BarterBoxTileEntity;
import com.github.commoble.barterbox.client.ClientEvents;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod(BarterBox.MODID)
public class BarterBox
{
	public static final String MODID = "barterbox";
	
	public BarterBox()
	{
		// get event busses
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		
		DeferredRegister<Block> blocks = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
		DeferredRegister<Item> items = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
		DeferredRegister<ContainerType<?>> containers = new DeferredRegister<>(ForgeRegistries.CONTAINERS, MODID);
		DeferredRegister<TileEntityType<?>> tileEntityTypes = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MODID);
		
		// register objects
		registerBlocks(blocks);
		registerItems(items);
		registerContainers(containers);
		registerTileEntityTypes(tileEntityTypes);
		
		// subscribe deferred registers
		DeferredRegister<?>[] registers = {blocks, items, containers, tileEntityTypes};
		for (DeferredRegister<?> register : registers)
		{
			register.register(modBus);
		}
		
		// add common event listeners
		
		// add client event listeners if we are on the client
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			ClientEvents.addClientListeners(modBus, forgeBus);
		}
	}
	
	public static void registerBlocks(DeferredRegister<Block> blocks)
	{
		blocks.register(Names.BARTER_BOX, () -> new BarterBoxBlock(Block.Properties.from(Blocks.CRAFTING_TABLE)));
	}
	
	public static void registerItems(DeferredRegister<Item> items)
	{
		items.register(Names.BARTER_BOX, () -> new BlockItem(BlockHolders.BARTER_BOX, new Item.Properties().group(ItemGroup.DECORATIONS)));
	}
	
	public static void registerContainers(DeferredRegister<ContainerType<?>> containers)
	{
		containers.register(Names.BARTER_BOX_SELLER, () -> new ContainerType<>(BarterBoxSellerContainer::getClientContainer));
	}
	
	public static void registerTileEntityTypes(DeferredRegister<TileEntityType<?>> tileEntityTypes)
	{
		tileEntityTypes.register(Names.BARTER_BOX,
			() -> TileEntityType.Builder.create(BarterBoxTileEntity::new, BlockHolders.BARTER_BOX).build(null));
	}
	
	public static ResourceLocation getResourceLocation(String name)
	{
		return new ResourceLocation(MODID, name);
	}
	
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> getRegistryObject(String name, IForgeRegistry<T> registry)
	{
		return RegistryObject.of(getResourceLocation(name), registry);
	}
}
