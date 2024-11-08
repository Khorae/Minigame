package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public record FluidPredicate(Optional<TagKey<Fluid>> tag, Optional<Holder<Fluid>> fluid, Optional<StatePropertiesPredicate> properties) {
    public static final Codec<FluidPredicate> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
                    ExtraCodecs.strictOptionalField(TagKey.codec(Registries.FLUID), "tag").forGetter(FluidPredicate::tag),
                    ExtraCodecs.strictOptionalField(BuiltInRegistries.FLUID.holderByNameCodec(), "fluid").forGetter(FluidPredicate::fluid),
                    ExtraCodecs.strictOptionalField(StatePropertiesPredicate.CODEC, "state").forGetter(FluidPredicate::properties)
                )
                .apply(instance, FluidPredicate::new)
    );

    public boolean matches(ServerLevel world, BlockPos pos) {
        if (!world.isLoaded(pos)) {
            return false;
        } else {
            FluidState fluidState = world.getFluidState(pos);
            return (!this.tag.isPresent() || fluidState.is(this.tag.get()))
                && (!this.fluid.isPresent() || fluidState.is(this.fluid.get().value()))
                && (!this.properties.isPresent() || this.properties.get().matches(fluidState));
        }
    }

    public static class Builder {
        private Optional<Holder<Fluid>> fluid = Optional.empty();
        private Optional<TagKey<Fluid>> fluids = Optional.empty();
        private Optional<StatePropertiesPredicate> properties = Optional.empty();

        private Builder() {
        }

        public static FluidPredicate.Builder fluid() {
            return new FluidPredicate.Builder();
        }

        public FluidPredicate.Builder of(Fluid fluid) {
            this.fluid = Optional.of(fluid.builtInRegistryHolder());
            return this;
        }

        public FluidPredicate.Builder of(TagKey<Fluid> tag) {
            this.fluids = Optional.of(tag);
            return this;
        }

        public FluidPredicate.Builder setProperties(StatePropertiesPredicate state) {
            this.properties = Optional.of(state);
            return this;
        }

        public FluidPredicate build() {
            return new FluidPredicate(this.fluids, this.fluid, this.properties);
        }
    }
}
