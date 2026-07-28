package jmh;

import org.powernukkitx.nbt.tag.ByteTag;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.LongTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.nbt.tag.Tag;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class NbtTagBenchmark {

    private CompoundTag readCompound;
    private ListTag<IntTag> readList;
    private List<IntTag> intTags;

    @Setup
    public void setup() {
        readCompound = new CompoundTag()
                .putByte("b", 1)
                .putShort("s", 2)
                .putInt("i", 3)
                .putLong("l", 4L)
                .putFloat("f", 5f)
                .putDouble("d", 6D)
                .putString("str", "value")
                .putBoolean("bool", true);

        readList = new ListTag<>(Tag.TAG_Int);
        intTags = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            IntTag tag = new IntTag(i);
            readList.add(tag);
            intTags.add(tag);
        }
    }

    @Benchmark
    public void buildCompoundManyFields(Blackhole hole) {
        CompoundTag tag = new CompoundTag()
                .putByte("b", 1)
                .putShort("s", 2)
                .putInt("i", 3)
                .putLong("l", 4L)
                .putFloat("f", 5f)
                .putDouble("d", 6D)
                .putString("str", "value")
                .putBoolean("bool", true);
        hole.consume(tag);
    }

    @Benchmark
    public void buildCompoundInts(Blackhole hole) {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < 32; i++) {
            tag.putInt("k" + i, i);
        }
        hole.consume(tag);
    }

    @Benchmark
    public void readCompoundFields(Blackhole hole) {
        hole.consume(readCompound.getByte("b"));
        hole.consume(readCompound.getShort("s"));
        hole.consume(readCompound.getInt("i"));
        hole.consume(readCompound.getLong("l"));
        hole.consume(readCompound.getFloat("f"));
        hole.consume(readCompound.getDouble("d"));
        hole.consume(readCompound.getString("str"));
        hole.consume(readCompound.getBoolean("bool"));
    }

    @Benchmark
    public void readCompoundWithDefault(Blackhole hole) {
        hole.consume(readCompound.getInt("missing", 42));
        hole.consume(readCompound.getString("missing", "def"));
        hole.consume(readCompound.getInt("i", 0));
    }

    @Benchmark
    public void compoundContains(Blackhole hole) {
        hole.consume(readCompound.contains("i"));
        hole.consume(readCompound.containsInt("i"));
        hole.consume(readCompound.containsString("str"));
        hole.consume(readCompound.containsList("missing"));
    }

    @Benchmark
    public void copyCompound(Blackhole hole) {
        hole.consume(readCompound.copy());
    }

    @Benchmark
    public void putAllCompound(Blackhole hole) {
        CompoundTag tag = new CompoundTag();
        tag.putAll(readCompound);
        hole.consume(tag);
    }

    @Benchmark
    public void buildNestedCompound(Blackhole hole) {
        CompoundTag root = new CompoundTag();
        for (int i = 0; i < 8; i++) {
            CompoundTag child = new CompoundTag()
                    .putInt("id", i)
                    .putString("name", "node");
            root.putCompound("child" + i, child);
        }
        hole.consume(root);
    }

    @Benchmark
    public void buildListAdd(Blackhole hole) {
        ListTag<IntTag> list = new ListTag<>(Tag.TAG_Int);
        for (int i = 0; i < 64; i++) {
            list.add(new IntTag(i));
        }
        hole.consume(list);
    }

    @Benchmark
    public void buildListAddAll(Blackhole hole) {
        ListTag<IntTag> list = new ListTag<>();
        list.addAll(intTags);
        hole.consume(list);
    }

    @Benchmark
    public void buildListFromCollection(Blackhole hole) {
        hole.consume(new ListTag<>(List.of(new IntTag(5), new IntTag(6), new IntTag(7))));
    }

    @Benchmark
    public void iterateList(Blackhole hole) {
        int sum = 0;
        for (int i = 0; i < readList.size(); i++) {
            sum += readList.get(i).data;
        }
        hole.consume(sum);
    }

    @Benchmark
    public void copyList(Blackhole hole) {
        hole.consume(readList.copy());
    }

    @Benchmark
    public void listType(Blackhole hole) {
        hole.consume(readList.type);
        hole.consume(readList.size());
    }

    @Benchmark
    public void constructScalarTags(Blackhole hole) {
        hole.consume(new IntTag(1));
        hole.consume(new LongTag(2L));
        hole.consume(new FloatTag(3f));
        hole.consume(new DoubleTag(4D));
        hole.consume(new ByteTag((byte) 5));
        hole.consume(new StringTag("six"));
    }

    @Benchmark
    public void tagGetId(Blackhole hole) {
        hole.consume(readCompound.getId());
        hole.consume(readList.getId());
        hole.consume(new IntTag(0).getId());
        hole.consume(new StringTag("x").getId());
    }

    @Benchmark
    public void compoundListRoundTrip(Blackhole hole) {
        ListTag<StringTag> list = new ListTag<>(Tag.TAG_String);
        list.add(new StringTag("a"));
        list.add(new StringTag("b"));
        CompoundTag tag = new CompoundTag().putList("items", list);
        hole.consume(tag.getList("items"));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NbtTagBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
