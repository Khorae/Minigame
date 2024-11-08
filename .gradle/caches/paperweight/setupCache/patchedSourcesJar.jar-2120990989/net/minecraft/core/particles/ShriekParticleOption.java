package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class ShriekParticleOption implements ParticleOptions {
    public static final Codec<ShriekParticleOption> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(Codec.INT.fieldOf("delay").forGetter(particleEffect -> particleEffect.delay)).apply(instance, ShriekParticleOption::new)
    );
    public static final ParticleOptions.Deserializer<ShriekParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ShriekParticleOption>() {
        @Override
        public ShriekParticleOption fromCommand(ParticleType<ShriekParticleOption> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            int i = stringReader.readInt();
            return new ShriekParticleOption(i);
        }

        @Override
        public ShriekParticleOption fromNetwork(ParticleType<ShriekParticleOption> particleType, FriendlyByteBuf friendlyByteBuf) {
            return new ShriekParticleOption(friendlyByteBuf.readVarInt());
        }
    };
    private final int delay;

    public ShriekParticleOption(int delay) {
        this.delay = delay;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(this.delay);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.delay);
    }

    @Override
    public ParticleType<ShriekParticleOption> getType() {
        return ParticleTypes.SHRIEK;
    }

    public int getDelay() {
        return this.delay;
    }
}
