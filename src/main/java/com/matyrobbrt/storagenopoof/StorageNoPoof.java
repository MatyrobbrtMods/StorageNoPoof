package com.matyrobbrt.storagenopoof;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Mod(value = StorageNoPoof.MOD_ID)
public class StorageNoPoof {
    public static final String MOD_ID = "storagenopoof";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);

    public static final DeferredBlock<Block> PRESERVER = BLOCKS.register("preserver", () -> new PreserverBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE).strength(0.2F)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PreserverBlockEntity>> PRESERVER_BE = BLOCK_ENTITIES.register(
            "preserver", () -> BlockEntityType.Builder.of(PreserverBlockEntity::new, PRESERVER.get()).build(null)
    );

    public StorageNoPoof(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);

        bus.addListener((final RegisterCapabilitiesEvent event) -> {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PRESERVER_BE.get(), (object, context) -> object.handler);
        });

        bus.addListener((final GatherDataEvent event) -> {
            event.getGenerator().addProvider(event.includeClient(), new BlockStateProvider(event.getGenerator().getPackOutput(), MOD_ID, event.getExistingFileHelper()) {
                @Override
                protected void registerStatesAndModels() {
                    simpleBlock(PRESERVER.value());
                }
            });
            event.getGenerator().addProvider(event.includeClient(), new LanguageProvider(event.getGenerator().getPackOutput(), MOD_ID, "en_us") {
                @Override
                protected void addTranslations() {
                    addBlock(PRESERVER, "Preserver");
                }
            });
        });
    }

    @Nullable
    public static BlockEntity attemptToPreserveInventories(BlockPos pos, BlockState state, CompoundTag tag, HolderLookup.Provider registries, Level level) {
        if (state.isAir()) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
            if (id != null) {
                var type = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(id);
                if (type == null) {
                    var preservation = StorageNoPoof.attemptPreservation(id, tag, registries);
                    if (preservation != null) {
                        var newState = StorageNoPoof.PRESERVER.get().defaultBlockState();
                        level.setBlock(pos, newState, Block.UPDATE_ALL);

                        var be = new PreserverBlockEntity(pos, newState);

                        be.loadItems(preservation);
                        be.originalTag = tag;

                        return be;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static List<ItemStack> attemptPreservation(ResourceLocation beId, CompoundTag rootTag, HolderLookup.Provider registries) {
        if (rootTag.contains("Items", Tag.TAG_LIST)) {
            var size = rootTag.getList("Items", 10).size();
            var inv = NonNullList.withSize(size, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(rootTag, inv, registries);
            return inv;
        }
        return null;
    }
}
