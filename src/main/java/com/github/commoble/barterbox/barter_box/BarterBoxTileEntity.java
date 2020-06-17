package com.github.commoble.barterbox.barter_box;

import java.util.UUID;

import javax.annotation.Nullable;

import com.github.commoble.barterbox.TileEntityTypes;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class BarterBoxTileEntity extends TileEntity
{
	// NBT tag names
	public static final String OWNER = "owner";
	public static final String SELLER_ITEMS = "seller_items";
	public static final String BUYER_ITEMS = "buyer_items";
	
	@Nullable
	public UUID ownerID = null;
	
	public BarterBoxOfferInventoryHandler offeredItems = new BarterBoxOfferInventoryHandler(this);
	public BarterBoxEarnedItemHandler earnedItems = new BarterBoxEarnedItemHandler(this);

	public BarterBoxTileEntity()
	{
		super(TileEntityTypes.BARTER_BOX);
	}

	// called to load the TE onto the server from the hard drive
	@Override
	public void read(CompoundNBT compound)
	{
		super.read(compound);
		this.ownerID = NBTUtil.readUniqueId(compound.getCompound(OWNER));
		this.offeredItems.deserializeNBT(compound.getCompound(SELLER_ITEMS));
		this.earnedItems.deserializeNBT(compound.getCompound(BUYER_ITEMS));
	}
	
	// called to save the TE to the hard drive from the server
	@Override
	public CompoundNBT write(CompoundNBT compound)
	{
		CompoundNBT nbt = super.write(compound);
		if (this.ownerID != null)
		{
			nbt.put(OWNER, NBTUtil.writeUniqueId(this.ownerID));
		}
		nbt.put(SELLER_ITEMS, this.offeredItems.serializeNBT());
		nbt.put(BUYER_ITEMS, this.earnedItems.serializeNBT());
		return nbt;
	}
	
	// called to generate NBT for a syncing packet when a client loads a chunk that
	// this TE is in
	@Override
	public CompoundNBT getUpdateTag()
	{
		// we want to tell the client about as much data as it needs to know
		// since it doesn't know any data at this point, we can usually just defer to
		// write() above
		// there's an equivalent method for reading the update tag but it just defaults
		// to read() anyway
		return this.write(new CompoundNBT());
	}

	// called to send a packet to the client when a block update occurs on the server
	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);

		// the number here is generally ignored for non-vanilla TileEntities, 0 is
		// safest
		return new SUpdateTileEntityPacket(this.getPos(), 0, nbt);
	}

	// this method gets called on the client when it receives the packet that was
	// sent in the previous method
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet)
	{
		this.read(packet.getNbtCompound());
	}
}
