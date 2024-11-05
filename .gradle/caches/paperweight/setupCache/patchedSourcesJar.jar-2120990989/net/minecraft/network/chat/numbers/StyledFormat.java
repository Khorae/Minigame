package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class StyledFormat implements NumberFormat {
    public static final NumberFormatType<StyledFormat> TYPE = new NumberFormatType<StyledFormat>() {
        private static final MapCodec<StyledFormat> CODEC = Style.Serializer.MAP_CODEC.xmap(StyledFormat::new, format -> format.style);

        @Override
        public MapCodec<StyledFormat> mapCodec() {
            return CODEC;
        }

        @Override
        public void writeToStream(FriendlyByteBuf buf, StyledFormat format) {
            buf.writeWithCodec(NbtOps.INSTANCE, Style.Serializer.CODEC, format.style);
        }

        @Override
        public StyledFormat readFromStream(FriendlyByteBuf friendlyByteBuf) {
            Style style = friendlyByteBuf.readWithCodecTrusted(NbtOps.INSTANCE, Style.Serializer.CODEC);
            return new StyledFormat(style);
        }
    };
    public static final StyledFormat NO_STYLE = new StyledFormat(Style.EMPTY);
    public static final StyledFormat SIDEBAR_DEFAULT = new StyledFormat(Style.EMPTY.withColor(ChatFormatting.RED));
    public static final StyledFormat PLAYER_LIST_DEFAULT = new StyledFormat(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    public final Style style;

    public StyledFormat(Style style) {
        this.style = style;
    }

    @Override
    public MutableComponent format(int number) {
        return Component.literal(Integer.toString(number)).withStyle(this.style);
    }

    @Override
    public NumberFormatType<StyledFormat> type() {
        return TYPE;
    }
}