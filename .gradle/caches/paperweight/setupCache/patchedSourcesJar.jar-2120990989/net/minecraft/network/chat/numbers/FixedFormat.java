package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;

public class FixedFormat implements NumberFormat {
    public static final NumberFormatType<FixedFormat> TYPE = new NumberFormatType<FixedFormat>() {
        private static final MapCodec<FixedFormat> CODEC = ComponentSerialization.CODEC.fieldOf("value").xmap(FixedFormat::new, format -> format.value);

        @Override
        public MapCodec<FixedFormat> mapCodec() {
            return CODEC;
        }

        @Override
        public void writeToStream(FriendlyByteBuf buf, FixedFormat format) {
            buf.writeComponent(format.value);
        }

        @Override
        public FixedFormat readFromStream(FriendlyByteBuf friendlyByteBuf) {
            Component component = friendlyByteBuf.readComponentTrusted();
            return new FixedFormat(component);
        }
    };
    public final Component value;

    public FixedFormat(Component text) {
        this.value = text;
    }

    @Override
    public MutableComponent format(int number) {
        return this.value.copy();
    }

    @Override
    public NumberFormatType<FixedFormat> type() {
        return TYPE;
    }
}
