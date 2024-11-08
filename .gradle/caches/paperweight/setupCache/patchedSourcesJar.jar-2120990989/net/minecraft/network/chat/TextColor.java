package net.minecraft.network.chat;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;

public final class TextColor {

    private static final String CUSTOM_COLOR_PREFIX = "#";
    public static final Codec<TextColor> CODEC = Codec.STRING.comapFlatMap(TextColor::parseColor, TextColor::serialize);
    private static final Map<ChatFormatting, TextColor> LEGACY_FORMAT_TO_COLOR = (Map) Stream.of(ChatFormatting.values()).filter(ChatFormatting::isColor).collect(ImmutableMap.toImmutableMap(Function.identity(), (enumchatformat) -> {
        return new TextColor(enumchatformat.getColor(), enumchatformat.getName(), enumchatformat); // CraftBukkit
    }));
    private static final Map<String, TextColor> NAMED_COLORS = (Map) TextColor.LEGACY_FORMAT_TO_COLOR.values().stream().collect(ImmutableMap.toImmutableMap((chathexcolor) -> {
        return chathexcolor.name;
    }, Function.identity()));
    private final int value;
    @Nullable
    public final String name;
    // CraftBukkit start
    @Nullable
    public final ChatFormatting format;

    private TextColor(int i, String s, ChatFormatting format) {
        this.value = i & 16777215;
        this.name = s;
        this.format = format;
    }

    private TextColor(int rgb) {
        this.value = rgb & 16777215;
        this.name = null;
        this.format = null;
    }
    // CraftBukkit end

    public int getValue() {
        return this.value;
    }

    public String serialize() {
        return this.name != null ? this.name : this.formatValue();
    }

    private String formatValue() {
        return String.format(Locale.ROOT, "#%06X", this.value);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            TextColor chathexcolor = (TextColor) object;

            return this.value == chathexcolor.value;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.value, this.name});
    }

    public String toString() {
        return this.serialize();
    }

    @Nullable
    public static TextColor fromLegacyFormat(ChatFormatting formatting) {
        return (TextColor) TextColor.LEGACY_FORMAT_TO_COLOR.get(formatting);
    }

    public static TextColor fromRgb(int rgb) {
        return new TextColor(rgb);
    }

    public static DataResult<TextColor> parseColor(String name) {
        if (name.startsWith("#")) {
            try {
                int i = Integer.parseInt(name.substring(1), 16);

                return i >= 0 && i <= 16777215 ? DataResult.success(TextColor.fromRgb(i), Lifecycle.stable()) : DataResult.error(() -> {
                    return "Color value out of range: " + name;
                });
            } catch (NumberFormatException numberformatexception) {
                return DataResult.error(() -> {
                    return "Invalid color value: " + name;
                });
            }
        } else {
            TextColor chathexcolor = (TextColor) TextColor.NAMED_COLORS.get(name);

            return chathexcolor == null ? DataResult.error(() -> {
                return "Invalid color name: " + name;
            }) : DataResult.success(chathexcolor, Lifecycle.stable());
        }
    }
}
