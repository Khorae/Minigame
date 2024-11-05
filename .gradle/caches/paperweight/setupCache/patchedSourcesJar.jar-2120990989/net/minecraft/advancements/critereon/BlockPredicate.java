package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public record BlockPredicate(
    Optional<TagKey<Block>> tag, Optional<HolderSet<Block>> blocks, Optional<StatePropertiesPredicate> properties, Optional<NbtPredicate> nbt
) {
    private static final Codec<HolderSet<Block>> BLOCKS_CODEC = BuiltInRegistries.BLOCK
        .holderByNameCodec()
        .listOf()
        .xmap(HolderSet::direct, blocks -> blocks.stream().toList());
    public static final Codec<BlockPredicate> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
                    ExtraCodecs.strictOptionalField(TagKey.codec(Registries.BLOCK), "tag").forGetter(BlockPredicate::tag),
                    ExtraCodecs.strictOptionalField(BLOCKS_CODEC, "blocks").forGetter(BlockPredicate::blocks),
                    ExtraCodecs.strictOptionalField(StatePropertiesPredicate.CODEC, "state").forGetter(BlockPredicate::properties),
                    ExtraCodecs.strictOptionalField(NbtPredicate.CODEC, "nbt").forGetter(BlockPredicate::nbt)
                )
                .apply(instance, BlockPredicate::new)
    );

    public boolean matches(ServerLevel world, BlockPos pos) {
        if (!world.isLoaded(pos)) {
            return false;
        } else {
            BlockState blockState = world.getBlockState(pos);
            if (this.tag.isPresent() && !blockState.is(this.tag.get())) {
                return false;
            } else if (this.blocks.isPresent() && !blockState.is(this.blocks.get())) {
                return false;
            } else if (this.properties.isPresent() && !this.properties.get().matches(blockState)) {
                return false;
            } else {
                if (this.nbt.isPresent()) {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity == null || !this.nbt.get().matches(blockEntity.saveWithFullMetadata())) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static class Builder {
        private Optional<HolderSet<Block>> blocks = Optional.empty();
        private Optional<TagKey<Block>> tag = Optional.empty();
        private Optional<StatePropertiesPredicate> properties = Optional.empty();
        private Optional<NbtPredicate> nbt = Optional.empty();

        private Builder() {
        }

        public static BlockPredicate.Builder block() {
            return new BlockPredicate.Builder();
        }

        public BlockPredicate.Builder of(Block... blocks) {
            this.blocks = Optional.of(HolderSet.direct(Block::builtInRegistryHolder, blocks));
            return this;
        }

        public BlockPredicate.Builder of(Collection<Block> blocks) {
            this.blocks = Optional.of(HolderSet.direct(Block::builtInRegistryHolder, blocks));
            return this;
        }

        public BlockPredicate.Builder of(TagKey<Block> tag) {
            this.tag = Optional.of(tag);
            return this;
        }

        public BlockPredicate.Builder hasNbt(CompoundTag nbt) {
            this.nbt = Optional.of(new NbtPredicate(nbt));
            return this;
        }

        public BlockPredicate.Builder setProperties(StatePropertiesPredicate.Builder state) {
            this.properties = state.build();
            return this;
        }

        public BlockPredicate build() {
            return new BlockPredicate(this.tag, this.blocks, this.properties, this.nbt);
        }
    }
}
