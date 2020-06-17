package com.github.commoble.barterbox.barter_box;

import com.github.commoble.barterbox.BlockHolders;
import com.github.commoble.barterbox.ContainerTypes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BarterBoxSellerContainer extends Container
{	
	// how many slots we have on each side
	public static final int SELL_ROWS = 3;
	public static final int SELL_COLUMNS = 9;
	public static final int SELL_SLOTS = SELL_ROWS * SELL_COLUMNS;
	
	// x-and-y-positions to start placing the container slots at
	public static final int XSTART_SELLER = 8;
	public static final int YSTART_SELLER = 18;
	public static final int XSTART_BUYER = 123;
	public static final int YSTART_BUYER = 91;
	public static final int SLOT_SPACING = 18; // distance between two adjacent slot positions (x or y)
	
	// player inventory
	public static final int PLAYER_ROWS = 3;
	public static final int PLAYER_COLUMNS = 9;
	public static final int XSTART_PLAYER = XSTART_SELLER;
	public static final int YSTART_PLAYER = 140;
	public static final int YSTART_HOTBAR = 198;
	public static final int PLAYER_SLOTS = (PLAYER_ROWS + 1) * PLAYER_COLUMNS;
	
	// slot ids by sub-inventory
	// seller slots are in the range [FIRST_SELLER_SLOT, END_SELLER_SLOTS)
	// player slots are in the range [FIRST_PLAYER_SLOT, END_PLAYER_SLOTS)
	public static final int FIRST_SELLER_SLOT = 0;
	public static final int FIRST_BUYER_SLOT = FIRST_SELLER_SLOT + SELL_SLOTS;
	public static final int FIRST_PLAYER_SLOT = FIRST_BUYER_SLOT + 1;
	public static final int END_PLAYER_SLOTS = FIRST_PLAYER_SLOT + PLAYER_SLOTS;
	public static final int END_SELLER_SLOTS = FIRST_BUYER_SLOT;
	
	public final IWorldPosCallable usabilityTest;
	
	public static BarterBoxSellerContainer getClientContainer(int id, PlayerInventory playerInventory)
	{
		return new BarterBoxSellerContainer(id, playerInventory, new ItemStackHandler(SELL_SLOTS), new ItemStackHandler(SELL_SLOTS), IWorldPosCallable.DUMMY);
	}
	
	public static IContainerProvider getServerContainerProvider(BarterBoxTileEntity te)
	{
		return (id, playerInventory, player) -> new BarterBoxSellerContainer(id, playerInventory, te.offeredItems, te.earnedItems,
			IWorldPosCallable.of(te.getWorld(), te.getPos()));
	}

	protected BarterBoxSellerContainer(int id, PlayerInventory playerInventory, IItemHandler sellerItems, IItemHandler buyerItems, IWorldPosCallable usabilityTest)
	{
		super(ContainerTypes.BARTER_BOX_SELLER, id);
		
		this.usabilityTest = usabilityTest;
		
		// add slots for items to be sold
		for (int row=0; row<SELL_ROWS; row++)
		{
			for (int column=0; column<SELL_COLUMNS; column++)
			{
				int slotIndex = row*SELL_COLUMNS + column;
				int x = XSTART_SELLER + SLOT_SPACING*column;
				int y = YSTART_SELLER + SLOT_SPACING*row;
				this.addSlot(new SlotItemHandler(sellerItems, slotIndex, x, y));
			}
		}
		
		// add slots for items received from buyers
		this.addSlot(new SlotItemHandler(buyerItems, 0, XSTART_BUYER, YSTART_BUYER));

		// add slots for player hotbar
		for (int column = 0; column < PLAYER_COLUMNS; ++column)
		{
			int x = XSTART_PLAYER + column * SLOT_SPACING;
			this.addSlot(new Slot(playerInventory, column, x, YSTART_HOTBAR));
		}
		
		// add slots for player backpack
		for (int row = 0; row < PLAYER_ROWS; ++row)
		{
			int y = YSTART_PLAYER + row*SLOT_SPACING;
			for (int column = 0; column < PLAYER_COLUMNS; ++column)
			{
				int x = XSTART_PLAYER + column * SLOT_SPACING;
				int slotIndex = column + row * PLAYER_COLUMNS + PLAYER_COLUMNS;
				this.addSlot(new Slot(playerInventory, slotIndex, x, y));
			}
		}
	}

	/**
	 * Called on the server to verify that a player is allowed to open the container, given their current world position.
	 * This should only care about world position, we can verify whether the player is the seller or not when we
	 * open the container
	 */
	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return Container.isWithinUsableDistance(this.usabilityTest, playerIn, BlockHolders.BARTER_BOX);
	}

	/**
	 * Called when the player shift-clicks a slot. This is called repeatedly until
	 * it returns an empty itemstack.
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stackInSlot = slot.getStack();
			slotStackCopy = stackInSlot.copy();
			
			// if this is a seller slot or buyer slot
			if (index < FIRST_PLAYER_SLOT)
			{
				if (!this.mergeItemStack(stackInSlot, FIRST_PLAYER_SLOT, END_PLAYER_SLOTS, true))
				{
					return ItemStack.EMPTY;
				}
			}
			// otherwise, this is a player slot
			else if (!this.mergeItemStack(stackInSlot, FIRST_SELLER_SLOT, END_SELLER_SLOTS, false))
			{
				return ItemStack.EMPTY;
			}

			// make sure empty slots are empty
			if (stackInSlot.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (stackInSlot.getCount() == slotStackCopy.getCount())
			{
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, stackInSlot);
		}

		return slotStackCopy;

	}

}
