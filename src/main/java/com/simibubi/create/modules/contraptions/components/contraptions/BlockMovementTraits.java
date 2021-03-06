package com.simibubi.create.modules.contraptions.components.contraptions;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.modules.contraptions.components.actors.HarvesterBlock;
import com.simibubi.create.modules.contraptions.components.actors.PortableStorageInterfaceBlock;
import com.simibubi.create.modules.contraptions.components.contraptions.chassis.AbstractChassisBlock;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.block.RedstoneWallTorchBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMovementTraits {

	public static boolean movementNecessary(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (isBrittle(state))
			return true;
		if (state.getMaterial().isReplaceable())
			return false;
		if (state.getCollisionShape(world, pos).isEmpty())
			return false;
		return true;
	}

	public static boolean movementAllowed(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() instanceof AbstractChassisBlock)
			return true;
		if (blockState.getBlockHardness(world, pos) == -1)
			return false;
		if (blockState.getBlock() == Blocks.OBSIDIAN)
			return false;
		if (AllBlocks.BELT.typeOf(blockState))
			return true;
		return blockState.getPushReaction() != PushReaction.BLOCK;
	}

	/**
	 * Brittle blocks will be collected first, as they may break when other blocks
	 * are removed before them
	 */
	public static boolean isBrittle(BlockState state) {
		Block block = state.getBlock();
		if (state.has(BlockStateProperties.HANGING))
			return true;
		if (block instanceof LadderBlock)
			return true;
		if (block instanceof TorchBlock)
			return true;
		if (block instanceof FlowerPotBlock)
			return true;
		if (block instanceof AbstractPressurePlateBlock)
			return true;
		if (block instanceof DoorBlock)
			return true;
		if (block instanceof HorizontalFaceBlock)
			return true;
		if (block instanceof AbstractRailBlock)
			return true;
		if (block instanceof RedstoneDiodeBlock)
			return true;
		if (block instanceof RedstoneWireBlock)
			return true;
		return false;
	}

	/**
	 * Attached blocks will move if blocks they are attached to are moved
	 */
	public static boolean isBlockAttachedTowards(BlockState state, Direction direction) {
		Block block = state.getBlock();
		if (block instanceof LadderBlock)
			return state.get(LadderBlock.FACING) == direction.getOpposite();
		if (block instanceof WallTorchBlock)
			return state.get(WallTorchBlock.HORIZONTAL_FACING) == direction.getOpposite();
		if (block instanceof AbstractPressurePlateBlock)
			return direction == Direction.DOWN;
		if (block instanceof DoorBlock)
			return direction == Direction.DOWN;
		if (block instanceof FlowerPotBlock)
			return direction == Direction.DOWN;
		if (block instanceof RedstoneDiodeBlock)
			return direction == Direction.DOWN;
		if (block instanceof RedstoneWireBlock)
			return direction == Direction.DOWN;
		if (block instanceof RedstoneWallTorchBlock)
			return state.get(RedstoneWallTorchBlock.FACING) == direction.getOpposite();
		if (block instanceof TorchBlock)
			return direction == Direction.DOWN;
		if (block instanceof HorizontalFaceBlock) {
			AttachFace attachFace = state.get(HorizontalFaceBlock.FACE);
			if (attachFace == AttachFace.CEILING)
				return direction == Direction.UP;
			if (attachFace == AttachFace.FLOOR)
				return direction == Direction.DOWN;
			if (attachFace == AttachFace.WALL)
				return direction.getOpposite() == state.get(HorizontalFaceBlock.HORIZONTAL_FACING);
		}
		if (state.has(BlockStateProperties.HANGING))
			return direction == (state.get(BlockStateProperties.HANGING) ? Direction.UP : Direction.DOWN);
		if (block instanceof AbstractRailBlock)
			return direction == Direction.DOWN;
		if (block instanceof HarvesterBlock)
			return direction == state.get(HarvesterBlock.HORIZONTAL_FACING).getOpposite();
		return false;
	}

	/**
	 * Non-Supportive blocks will not continue a chain of blocks picked up by e.g. a
	 * piston
	 */
	public static boolean notSupportive(BlockState state, Direction facing) {
		if (AllBlocks.DRILL.typeOf(state))
			return state.get(BlockStateProperties.FACING) == facing;
		if (AllBlocks.SAW.typeOf(state))
			return state.get(BlockStateProperties.FACING) == facing;
		if (AllBlocks.PORTABLE_STORAGE_INTERFACE.typeOf(state))
			return state.get(PortableStorageInterfaceBlock.FACING) == facing;
		if (AllBlocks.HARVESTER.typeOf(state))
			return state.get(BlockStateProperties.HORIZONTAL_FACING) == facing;
		return isBrittle(state);
	}

	public static boolean movementIgnored(BlockState state) {
		if (AllBlocks.MECHANICAL_PISTON.typeOf(state))
			return true;
		if (AllBlocks.STICKY_MECHANICAL_PISTON.typeOf(state))
			return true;
		if (AllBlocks.MECHANICAL_PISTON_HEAD.typeOf(state))
			return true;
		return false;
	}

}
