package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityVariantPredicate<V> {
    private final Function<Entity, Optional<V>> getter;
    private final EntitySubPredicate.Type type;

    public static <V> EntityVariantPredicate<V> create(Registry<V> registry, Function<Entity, Optional<V>> variantGetter) {
        return new EntityVariantPredicate<>(registry.byNameCodec(), variantGetter);
    }

    public static <V> EntityVariantPredicate<V> create(Codec<V> codec, Function<Entity, Optional<V>> variantGetter) {
        return new EntityVariantPredicate<>(codec, variantGetter);
    }

    private EntityVariantPredicate(Codec<V> codec, Function<Entity, Optional<V>> variantGetter) {
        this.getter = variantGetter;
        MapCodec<EntityVariantPredicate.SubPredicate<V>> mapCodec = RecordCodecBuilder.mapCodec(
            instance -> instance.group(codec.fieldOf("variant").forGetter(EntityVariantPredicate.SubPredicate::variant)).apply(instance, this::createPredicate)
        );
        this.type = new EntitySubPredicate.Type(mapCodec);
    }

    public EntitySubPredicate.Type type() {
        return this.type;
    }

    public EntityVariantPredicate.SubPredicate<V> createPredicate(V variant) {
        return new EntityVariantPredicate.SubPredicate<>(this.type, this.getter, variant);
    }

    public static record SubPredicate<V>(@Override EntitySubPredicate.Type type, Function<Entity, Optional<V>> getter, V variant) implements EntitySubPredicate {
        @Override
        public boolean matches(Entity entity, ServerLevel world, @Nullable Vec3 pos) {
            return this.getter.apply(entity).filter(variant -> variant.equals(this.variant)).isPresent();
        }
    }
}
