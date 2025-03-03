package com.matyrobbrt.storagenopoof;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class PreserverBlockEntity extends BlockEntity {
    public static final String TAG_ID = "PreservedItems", ORIGINAL_TAG_ID = "OriginalTag";

    public final ItemStackHandler handler = new ItemStackHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            PreserverBlockEntity.this.setChanged();

            for (int i = 0; i < getSlots(); i++) {
                if (!getStackInSlot(i).isEmpty()) {
                    return;
                }
            }

            if (level != null) {
                level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }
    };
    public CompoundTag originalTag = new CompoundTag();

    public PreserverBlockEntity(BlockPos pos, BlockState blockState) {
        super(StorageNoPoof.PRESERVER_BE.get(), pos, blockState);
    }

    public void loadItems(List<ItemStack> stack) {
        handler.setSize(stack.size());
        for (int i = 0; i < stack.size(); i++) {
            handler.setStackInSlot(i, stack.get(i).copy());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_ID)) {
            handler.deserializeNBT(registries, tag.getCompound(TAG_ID));
        }
        if (tag.contains(ORIGINAL_TAG_ID)) {
            originalTag = tag.getCompound(ORIGINAL_TAG_ID);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(TAG_ID, handler.serializeNBT(registries));
        tag.put(ORIGINAL_TAG_ID, originalTag);
    }
}
