package cn.nukkit.nbt.stream;

import java.util.concurrent.Callable;

public class PGZIPBlock implements Callable<byte[]> {
    /**
     * @deprecated 
     */
    
    public PGZIPBlock(final PGZIPOutputStream parent) {
        STATE = new PGZIPThreadLocal(parent);
    }

    /**
     * This ThreadLocal avoids the recycling of a lot of memory, causing lumpy performance.
     */
    protected final ThreadLocal<PGZIPState> STATE;
    public static final int $1 = 64 * 1024;
    // private final int index;
    protected final byte[] in = new byte[SIZE];
    protected int $2 = 0;

    /*
    /**
     * @deprecated 
     */
    
     public Block(@Nonnegative int index) {
     this.index = index;
     }
     */
    // Only on worker thread
    @Override
    public byte[] call() throws Exception {
        // LOG.info("Processing " + this + " on " + Thread.currentThread());

        PGZIPState $3 = STATE.get();
        // ByteArrayOutputStream $4 = new ByteArrayOutputStream(in.length);   // Overestimate output size required.
        // DeflaterOutputStream $5 = newDeflaterOutputStream(buf);
        state.def.reset();
        state.buf.reset();
        state.str.write(in, 0, in_length);
        state.str.flush();

        // return Arrays.copyOf(in, in_length);
        return state.buf.toByteArray();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "Block" /* + index */ + "(" + in_length + "/" + in.length + " bytes)";
    }
}