package net.minecraft.locale;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringDecomposer;
import org.slf4j.Logger;

public abstract class Language {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    public static final String DEFAULT = "en_us";
    private static volatile Language instance = loadDefault();

    private static Language loadDefault() {
        Builder<String, String> builder = ImmutableMap.builder();
        BiConsumer<String, String> biConsumer = builder::put;
        parseTranslations(biConsumer, "/assets/minecraft/lang/en_us.json");
        final Map<String, String> map = builder.build();
        return new Language() {
            @Override
            public String getOrDefault(String key, String fallback) {
                return map.getOrDefault(key, fallback);
            }

            @Override
            public boolean has(String key) {
                return map.containsKey(key);
            }

            @Override
            public boolean isDefaultRightToLeft() {
                return false;
            }

            @Override
            public FormattedCharSequence getVisualOrder(FormattedText text) {
                return visitor -> text.visit(
                            (style, string) -> StringDecomposer.iterateFormatted(string, style, visitor) ? Optional.empty() : FormattedText.STOP_ITERATION,
                            Style.EMPTY
                        )
                        .isPresent();
            }
        };
    }

    private static void parseTranslations(BiConsumer<String, String> entryConsumer, String path) {
        try (InputStream inputStream = Language.class.getResourceAsStream(path)) {
            loadFromJson(inputStream, entryConsumer);
        } catch (JsonParseException | IOException var7) {
            LOGGER.error("Couldn't read strings from {}", path, var7);
        }
    }

    public static void loadFromJson(InputStream inputStream, BiConsumer<String, String> entryConsumer) {
        JsonObject jsonObject = GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);

        for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String string = UNSUPPORTED_FORMAT_PATTERN.matcher(GsonHelper.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
            entryConsumer.accept(entry.getKey(), string);
        }
    }

    public static Language getInstance() {
        return instance;
    }

    public static void inject(Language language) {
        instance = language;
    }

    public String getOrDefault(String key) {
        return this.getOrDefault(key, key);
    }

    public abstract String getOrDefault(String key, String fallback);

    public abstract boolean has(String key);

    public abstract boolean isDefaultRightToLeft();

    public abstract FormattedCharSequence getVisualOrder(FormattedText text);

    public List<FormattedCharSequence> getVisualOrder(List<FormattedText> texts) {
        return texts.stream().map(this::getVisualOrder).collect(ImmutableList.toImmutableList());
    }
}
