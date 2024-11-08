package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class MultiVariantGenerator implements BlockStateGenerator {
    private final Block block;
    private final List<Variant> baseVariants;
    private final Set<Property<?>> seenProperties = Sets.newHashSet();
    private final List<PropertyDispatch> declaredPropertySets = Lists.newArrayList();

    private MultiVariantGenerator(Block block, List<Variant> variants) {
        this.block = block;
        this.baseVariants = variants;
    }

    public MultiVariantGenerator with(PropertyDispatch map) {
        map.getDefinedProperties().forEach(property -> {
            if (this.block.getStateDefinition().getProperty(property.getName()) != property) {
                throw new IllegalStateException("Property " + property + " is not defined for block " + this.block);
            } else if (!this.seenProperties.add((Property<?>)property)) {
                throw new IllegalStateException("Values of property " + property + " already defined for block " + this.block);
            }
        });
        this.declaredPropertySets.add(map);
        return this;
    }

    @Override
    public JsonElement get() {
        Stream<Pair<Selector, List<Variant>>> stream = Stream.of(Pair.of(Selector.empty(), this.baseVariants));

        for (PropertyDispatch propertyDispatch : this.declaredPropertySets) {
            Map<Selector, List<Variant>> map = propertyDispatch.getEntries();
            stream = stream.flatMap(pair -> map.entrySet().stream().map(entry -> {
                    Selector selector = ((Selector)pair.getFirst()).extend(entry.getKey());
                    List<Variant> list = mergeVariants((List<Variant>)pair.getSecond(), entry.getValue());
                    return Pair.of(selector, list);
                }));
        }

        Map<String, JsonElement> map2 = new TreeMap<>();
        stream.forEach(pair -> map2.put(pair.getFirst().getKey(), Variant.convertList(pair.getSecond())));
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("variants", Util.make(new JsonObject(), json -> map2.forEach(json::add)));
        return jsonObject;
    }

    private static List<Variant> mergeVariants(List<Variant> left, List<Variant> right) {
        Builder<Variant> builder = ImmutableList.builder();
        left.forEach(leftVariant -> right.forEach(rightVariant -> builder.add(Variant.merge(leftVariant, rightVariant))));
        return builder.build();
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static MultiVariantGenerator multiVariant(Block block) {
        return new MultiVariantGenerator(block, ImmutableList.of(Variant.variant()));
    }

    public static MultiVariantGenerator multiVariant(Block block, Variant variant) {
        return new MultiVariantGenerator(block, ImmutableList.of(variant));
    }

    public static MultiVariantGenerator multiVariant(Block block, Variant... variants) {
        return new MultiVariantGenerator(block, ImmutableList.copyOf(variants));
    }
}
