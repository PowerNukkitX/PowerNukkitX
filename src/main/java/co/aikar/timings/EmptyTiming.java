package co.aikar.timings;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import static co.aikar.timings.TimingIdentifier.DEFAULT_GROUP;

/**
 * 当Timings被完全关闭时使用的占位对象
 */
@PowerNukkitXOnly
@Since("1.19.50-r3")
public class EmptyTiming extends Timing {
    private static final TimingIdentifier IDENTIFIER = new TimingIdentifier(DEFAULT_GROUP.name, "Empty", null);
    EmptyTiming() {super(IDENTIFIER);}
    @Override
    void tick(boolean violated) {}
    @Override
    public Timing startTiming() {return this;}
    @Override
    public void stopTiming() {}
    @Override
    public void abort() {}
    @Override
    void addDiff(long diff) {}
    @Override
    void reset(boolean full) {}
    @Override
    public void close() {}
    @Override
    boolean isSpecial() {return false;}
}
