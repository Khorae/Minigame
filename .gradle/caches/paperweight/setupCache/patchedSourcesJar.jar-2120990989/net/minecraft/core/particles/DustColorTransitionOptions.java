package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustColorTransitionOptions extends DustParticleOptionsBase {
    public static final Vector3f SCULK_PARTICLE_COLOR = Vec3.fromRGB24(3790560).toVector3f();
    public static final DustColorTransitionOptions SCULK_TO_REDSTONE = new DustColorTransitionOptions(
        SCULK_PARTICLE_COLOR, DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1.0F
    );
    public static final Codec<DustColorTransitionOptions> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
                    ExtraCodecs.VECTOR3F.fieldOf("fromColor").forGetter(effect -> effect.color),
                    ExtraCodecs.VECTOR3F.fieldOf("toColor").forGetter(effect -> effect.toColor),
                    Codec.FLOAT.fieldOf("scale").forGetter(effect -> effect.scale)
                )
                .apply(instance, DustColorTransitionOptions::new)
    );
    public static final ParticleOptions.Deserializer<DustColorTransitionOptions> DESERIALIZER = new ParticleOptions.Deserializer<DustColorTransitionOptions>() {
        @Override
        public DustColorTransitionOptions fromCommand(ParticleType<DustColorTransitionOptions> particleType, StringReader stringReader) throws CommandSyntaxException {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(stringReader);
            stringReader.expect(' ');
            float f = stringReader.readFloat();
            Vector3f vector3f2 = DustParticleOptionsBase.readVector3f(stringReader);
            return new DustColorTransitionOptions(vector3f, vector3f2, f);
        }

        @Override
        public DustColorTransitionOptions fromNetwork(ParticleType<DustColorTransitionOptions> particleType, FriendlyByteBuf friendlyByteBuf) {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(friendlyByteBuf);
            float f = friendlyByteBuf.readFloat();
            Vector3f vector3f2 = DustParticleOptionsBase.readVector3f(friendlyByteBuf);
            return new DustColorTransitionOptions(vector3f, vector3f2, f);
        }
    };
    private final Vector3f toColor;

    public DustColorTransitionOptions(Vector3f fromColor, Vector3f toColor, float scale) {
        super(fromColor, scale);
        this.toColor = toColor;
    }

    public Vector3f getFromColor() {
        return this.color;
    }

    public Vector3f getToColor() {
        return this.toColor;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        super.writeToNetwork(buf);
        buf.writeFloat(this.toColor.x());
        buf.writeFloat(this.toColor.y());
        buf.writeFloat(this.toColor.z());
    }

    @Override
    public String writeToString() {
        return String.format(
            Locale.ROOT,
            "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f",
            BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
            this.color.x(),
            this.color.y(),
            this.color.z(),
            this.scale,
            this.toColor.x(),
            this.toColor.y(),
            this.toColor.z()
        );
    }

    @Override
    public ParticleType<DustColorTransitionOptions> getType() {
        return ParticleTypes.DUST_COLOR_TRANSITION;
    }
}
