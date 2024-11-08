package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

public class IdMapper<T> implements IdMap<T> {
    private int nextId;
    private final Reference2IntMap<T> tToId;
    private final List<T> idToT;

    public IdMapper() {
        this(512);
    }

    public IdMapper(int initialSize) {
        this.idToT = Lists.newArrayListWithExpectedSize(initialSize);
        this.tToId = new Reference2IntOpenHashMap<>(initialSize);
        this.tToId.defaultReturnValue(-1);
    }

    public void addMapping(T value, int id) {
        this.tToId.put(value, id);

        while (this.idToT.size() <= id) {
            this.idToT.add(null);
        }

        this.idToT.set(id, value);
        if (this.nextId <= id) {
            this.nextId = id + 1;
        }
    }

    public void add(T value) {
        this.addMapping(value, this.nextId);
    }

    @Override
    public int getId(T value) {
        return this.tToId.getInt(value);
    }

    @Nullable
    @Override
    public final T byId(int index) {
        return index >= 0 && index < this.idToT.size() ? this.idToT.get(index) : null;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filter(this.idToT.iterator(), Objects::nonNull);
    }

    public boolean contains(int index) {
        return this.byId(index) != null;
    }

    @Override
    public int size() {
        return this.tToId.size();
    }
}
