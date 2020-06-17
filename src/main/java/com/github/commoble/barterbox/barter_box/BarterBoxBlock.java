package com.github.commoble.barterbox.barter_box;

import java.util.UUID;

import com.github.commoble.barterbox.TileEntityTypes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BarterBoxBlock extends Block
{
	public BarterBoxBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return TileEntityTypes.BARTER_BOX.create();
	}

	/**
	 * Called after the block has been placed via a BlockItem and the tile entity has been set
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof BarterBoxTileEntity && placer instanceof PlayerEntity)
		{
			BarterBoxTileEntity box = (BarterBoxTileEntity)te;
			PlayerEntity player = (PlayerEntity) placer;
			box.ownerID = player.getGameProfile().getId();
		}
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof BarterBoxTileEntity)
		{
			BarterBoxTileEntity box = (BarterBoxTileEntity)te;
			UUID playerID = player.getGameProfile().getId();
			if (box.ownerID.equals(playerID))
			{
				if (player instanceof ServerPlayerEntity)
				{
					ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
					ITextComponent name = new TranslationTextComponent("container.barterbox.barter_box_seller");
					IContainerProvider provider = BarterBoxSellerContainer.getServerContainerProvider(box);
					INamedContainerProvider namedProvider = new SimpleNamedContainerProvider(provider, name);
					NetworkHooks.openGui(serverPlayer, namedProvider);
				}
				
				return ActionResultType.SUCCESS;
			}
		}
		return super.onBlockActivated(state, world, pos, player, handIn, hit);
	}

}
