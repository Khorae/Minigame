package net.minecraft.network.chat.numbers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class NumberFormatTypes {
    public static final MapCodec<NumberFormat> MAP_CODEC = BuiltInRegistries.NUMBER_FORMAT_TYPE
        .byNameCodec()
        .dispatchMap(NumberFormat::type, formatType -> (Codec<? extends NumberFormat>)formatType.mapCodec().codec());
    public static final Codec<NumberFormat> CODEC = MAP_CODEC.codec();

    public static NumberFormatType<?> bootstrap(Registry<NumberFormatType<?>> registry) {
        NumberFormatType<?> numberFormatType = Registry.register(registry, "blank", BlankFormat.TYPE);
        Registry.register(registry, "styled", StyledFormat.TYPE);
        Registry.register(registry, "fixed", FixedFormat.TYPE);
        return numberFormatType;
    }

    public static <T extends NumberFormat> void writeToStream(FriendlyByteBuf buf, T format) {
        NumberFormatType<T> numberFormatType = (NumberFormatType<T>)format.type();
        buf.writeId(BuiltInRegistries.NUMBER_FORMAT_TYPE, numberFormatType);
        numberFormatType.writeToStream(buf, format);
    }

    public static NumberFormat readFromStream(FriendlyByteBuf buf) {
        NumberFormatType<?> numberFormatType = buf.readById(BuiltInRegistries.NUMBER_FORMAT_TYPE);
        return numberFormatType.readFromStream(buf);
    }
}
