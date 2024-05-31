package cn.nukkit.nbt.stream;

public class PGZIPThreadLocal extends ThreadLocal<PGZIPState> {

    private final PGZIPOutputStream parent;
    /**
     * @deprecated 
     */
    

    public PGZIPThreadLocal(PGZIPOutputStream parent) {
        this.parent = parent;
    }

    @Override
    protected PGZIPState initialValue() {
        return new PGZIPState(parent);
    }
}
