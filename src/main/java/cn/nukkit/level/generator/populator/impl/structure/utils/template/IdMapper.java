package cn.nukkit.level.generator.populator.impl.structure.utils.template;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class IdMapper<T> implements Iterable<T> {

    private final HashMap<T, Integer> tToId;
    private final List<T> idToT;
    private int nextId;

    public IdMapper() {
        this(1 << 4);
    }

    public IdMapper(int expectedSize) {
        this.idToT = Lists.newArrayListWithExpectedSize(expectedSize);
        this.tToId = Maps.newHashMapWithExpectedSize(expectedSize);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filter(this.idToT.iterator(), Objects::nonNull);
    }

    public void addMapping(T t, int id) {
        this.tToId.put(t, id);
        while (this.idToT.size() <= id) {
            this.idToT.add(null);
        }
        this.idToT.set(id, t);
        if (this.nextId <= id) {
            this.nextId = id + 1;
        }
    }

    public void add(T t) {
        this.addMapping(t, this.nextId);
    }

    public int getId(T t) {
        Integer id = this.tToId.get(t);
        return id == null ? -1 : id;
    }

    public final T byId(int id) {
        if (id >= 0 && id < this.idToT.size()) {
            return this.idToT.get(id);
        }
        return null;
    }

    public int size() {
        return this.tToId.size();
    }
}
