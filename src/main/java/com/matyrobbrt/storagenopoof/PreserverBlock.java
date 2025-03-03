package com.matyrobbrt.storagenopoof;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PreserverBlock extends Block implements EntityBlock {
    public PreserverBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PreserverBlockEntity(pos, state);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof PreserverBlockEntity pbe) {
            var lst = new ArrayList<ItemStack>(pbe.handler.getSlots());
            for (int i = 0; i < pbe.handler.getSlots(); i++) {
                lst.add(pbe.handler.getStackInSlot(i).copy());
            }
            return lst;
        }
        return List.of();
    }
}
