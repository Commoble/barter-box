package com.github.commoble.barterbox.barter_box;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class BarterBoxOfferInventoryHandler extends ItemStackHandler
{
	public static final String OFFER = "offer";
	
	private final BarterBoxTileEntity te;
	private Item offeredItem = Items.AIR;

	public BarterBoxOfferInventoryHandler(BarterBoxTileEntity te)
	{
		super(27);
		this.te = te;
	}

	@Override
	protected void onContentsChanged(int slot)
	{
		super.onContentsChanged(slot);
		World world = this.te.getWorld();
		if (!world.isRemote)
		{
			this.te.markDirty();
			BlockState state = this.te.getBlockState();
			world.notifyBlockUpdate(this.te.getPos(), state, state, 3);
		}
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack)
	{
		return this.offeredItem == Items.AIR || stack.getItem() == this.offeredItem;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	{
		ItemStack result = super.insertItem(slot, stack, simulate);
		// if item was successfully inserted and we don't currently have an item filter
		if (!simulate && this.offeredItem == Items.AIR && result.getCount() < stack.getCount())
		{
			Item item = result.getItem();
			if (item != Items.AIR)
			{	// update the allowed item
				this.offeredItem = item;
			}
		}
		return result;
	}

	@Override
	public CompoundNBT serializeNBT()
	{
		CompoundNBT nbt = super.serializeNBT();
		nbt.put(OFFER, StringNBT.valueOf(this.offeredItem.getRegistryName().toString()));
		return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
    	String itemName = nbt.getString(OFFER);
    	if (itemName != null)
    	{
        	Item item =  ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString(OFFER)));
            if (item != null)
            {
            	this.offeredItem = item;
            }
    	}
    	super.deserializeNBT(nbt);
    }

}
