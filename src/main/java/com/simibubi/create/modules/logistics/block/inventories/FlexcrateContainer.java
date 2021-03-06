package com.simibubi.create.modules.logistics.block.inventories;

import com.simibubi.create.AllContainers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.SlotItemHandler;

public class FlexcrateContainer extends Container {

	public FlexcrateTileEntity te;
	public PlayerInventory playerInventory;
	public boolean doubleCrate;

	public FlexcrateContainer(int id, PlayerInventory inv, PacketBuffer extraData) {
		super(AllContainers.FLEXCRATE.type, id);
		ClientWorld world = Minecraft.getInstance().world;
		this.te = (FlexcrateTileEntity) world.getTileEntity(extraData.readBlockPos());
		this.te.handleUpdateTag(extraData.readCompoundTag());
		this.playerInventory = inv;
		init();
	}

	public FlexcrateContainer(int id, PlayerInventory inv, FlexcrateTileEntity te) {
		super(AllContainers.FLEXCRATE.type, id);
		this.te = te;
		this.playerInventory = inv;
		init();
	}

	private void init() {
		doubleCrate = te.isDoubleCrate();
		int x = doubleCrate ? 52 : 124;
		int maxCol = doubleCrate ? 8 : 4;
		for (int row = 0; row < 4; ++row) {
			for (int col = 0; col < maxCol; ++col) {
				this.addSlot(new SlotItemHandler(te.inventory, col + row * maxCol, x + col * 18, 25 + row * 18));
			}
		}

		// player Slots
		int xOffset = 58;
		int yOffset = 157;
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, xOffset + col * 18, yOffset + row * 18));
			}
		}

		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
			this.addSlot(new Slot(playerInventory, hotbarSlot, xOffset + hotbarSlot * 18, yOffset + 58));
		}

		detectAndSendChanges();
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		Slot clickedSlot = getSlot(index);
		if (!clickedSlot.getHasStack())
			return ItemStack.EMPTY;

		ItemStack stack = clickedSlot.getStack();
		int crateSize = doubleCrate ? 32 : 16;
		if (index < crateSize) {
			mergeItemStack(stack, crateSize, inventorySlots.size(), false);
			te.inventory.onContentsChanged(index);
		} else
			mergeItemStack(stack, 0, crateSize - 1, false);

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

}
