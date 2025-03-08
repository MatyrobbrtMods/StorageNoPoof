package com.matyrobbrt.storagenopoof.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.matyrobbrt.storagenopoof.StorageNoPoof;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @WrapOperation(method = "lambda$postLoadChunk$10", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadStatic(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private static BlockEntity attemptToPreserveInventories(BlockPos pos, BlockState state, CompoundTag tag, HolderLookup.Provider registries, Operation<BlockEntity> operation, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) LevelChunk chunk) {
        var pres = StorageNoPoof.attemptToPreserveInventories(pos, state, tag, registries, level, chunk);
        return pres == null ? operation.call(pos, state, tag, registries) : pres;
    }
}
