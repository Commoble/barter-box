package com.github.commoble.barterbox.barter_box;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;

public class BarterBoxEarnedItemHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundNBT>
{
	public static final String WANTED_ITEM = "want";
	public static final String EARNED_ITEMS = "earned";
	
	public Item wantedItem = Items.AIR;
	public long earnedItems = 0;
	
	public final BarterBoxTileEntity te;
	
	public BarterBoxEarnedItemHandler(BarterBoxTileEntity box)
	{
		this.te = box;
	}
	
	@Override
	public CompoundNBT serializeNBT()
	{
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString(WANTED_ITEM, this.wantedItem.getRegistryName().toString());
		nbt.putLong(EARNED_ITEMS, this.earnedItems);
		
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt)
	{
		String itemName = nbt.getString(WANTED_ITEM);
		if (itemName != null)
		{
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
			if (item != null)
			{
				this.wantedItem = item;
				if (this.wantedItem == Items.AIR)
				{
					this.earnedItems = 0;
				}
				else
				{
					this.earnedItems = nbt.getLong(EARNED_ITEMS);
				}
			}
		}
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
		this.wantedItem = stack.getItem();
		this.earnedItems = stack.getCount();
		this.te.markDirty();
	}

	@Override
	public int getSlots()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return this.extractItem(slot, (int)this.earnedItems, true);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (this.isItemValid(slot, stack))
		{
			if (!simulate)
			{
				this.earnedItems += stack.getCount();
				this.te.markDirty();
			}
			
			return ItemStack.EMPTY;
		}
		else
		{
			return stack;
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		// if we don't want anything, don't extract anything
		if (this.wantedItem == Items.AIR)
		{
			return ItemStack.EMPTY;
		}
		else
		{
			@SuppressWarnings("deprecation")
			int returnableAmount = Math.max(this.wantedItem.getMaxStackSize(), Math.max((int)this.earnedItems, amount));
			ItemStack result = new ItemStack(this.wantedItem, returnableAmount);
			if (!simulate)
			{
				this.earnedItems -= returnableAmount;
				this.te.markDirty();
			}
			return result;
		}
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return this.wantedItem != Items.AIR && stack.getItem() == this.wantedItem;
	}
	
}
