package cn.nukkit.nbt.stream;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * A multi-threaded version of {@link java.util.zip.GZIPOutputStream}.
 *
 * @author shevek
 */
public class PGZIPOutputStream extends FilterOutputStream {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(t -> new Thread(t, "PGZIPOutputStream#EXECUTOR"));

    public static ExecutorService getSharedThreadPool() {
        return EXECUTOR;
    }

    private final static int GZIP_MAGIC = 0x8b1f;
    private IntList blockSizes = new IntArrayList();
    private int level = Deflater.BEST_SPEED;
    private int strategy = Deflater.DEFAULT_STRATEGY;

    protected Deflater newDeflater() {
        Deflater def = new Deflater(level, true);
        def.setStrategy(strategy);
        return def;
    }

    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    protected static DeflaterOutputStream newDeflaterOutputStream(OutputStream out, Deflater deflater) {
        return new DeflaterOutputStream(out, deflater, 512, true);
    }

    private final ExecutorService executor;
    private final int nthreads;
    private final CRC32 crc = new CRC32();
    private final BlockingQueue<Future<byte[]>> emitQueue;
    private PGZIPBlock block;

    // Use long for bytesWritten to support >2GB
    private long bytesWritten = 0;

    public PGZIPOutputStream(OutputStream out, ExecutorService executor, int nthreads) throws IOException {
        super(out);
        this.executor = executor;
        this.nthreads = nthreads;
        this.emitQueue = new ArrayBlockingQueue<>(nthreads);
        this.block = new PGZIPBlock(this);
        writeHeader();
    }

    public PGZIPOutputStream(OutputStream out, int nthreads) throws IOException {
        this(out, getSharedThreadPool(), nthreads);
    }

    public PGZIPOutputStream(OutputStream out) throws IOException {
        this(out, Runtime.getRuntime().availableProcessors());
    }

    private void writeHeader() throws IOException {
        out.write(new byte[]{
                (byte) GZIP_MAGIC,
                (byte) (GZIP_MAGIC >> 8),
                Deflater.DEFLATED,
                0, 0, 0, 0, 0, 0,
                3
        });
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[]{(byte) (b & 0xFF)});
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        crc.update(b, off, len);
        bytesWritten += len;
        while (len > 0) {
            int capacity = block.in.length - block.in_length;
            if (len >= capacity) {
                System.arraycopy(b, off, block.in, block.in_length, capacity);
                block.in_length += capacity;
                off += capacity;
                len -= capacity;
                submit();
            } else {
                System.arraycopy(b, off, block.in, block.in_length, len);
                block.in_length += len;
                break;
            }
        }
    }

    private void submit() throws IOException {
        emitUntil(nthreads - 1);
        emitQueue.add(executor.submit(block));
        block = new PGZIPBlock(this);
    }

    private void tryEmit() throws IOException, InterruptedException, ExecutionException {
        while (true) {
            Future<byte[]> future = emitQueue.peek();
            if (future == null) return;
            if (!future.isDone()) return;
            emitQueue.remove();
            byte[] toWrite = future.get();
            blockSizes.add(toWrite.length);
            out.write(toWrite);
        }
    }

    private void emitUntil(int taskCountAllowed) throws IOException {
        try {
            while (emitQueue.size() > taskCountAllowed) {
                Future<byte[]> future = emitQueue.remove();
                byte[] toWrite = future.get();
                blockSizes.add(toWrite.length);
                out.write(toWrite);
            }
            tryEmit();
        } catch (ExecutionException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
    }

    @Override
    public void flush() throws IOException {
        if (block.in_length > 0) submit();
        emitUntil(0);
        super.flush();
    }

    public void finish() throws IOException {
        if (bytesWritten >= 0) {
            flush();
            newDeflaterOutputStream(out, newDeflater()).finish();
            ByteBuffer buf = ByteBuffer.allocate(8);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.putInt((int) (crc.getValue() & 0xFFFFFFFFL));
            buf.putInt((int) (bytesWritten & 0xFFFFFFFFL));
            out.write(buf.array());
        }
    }

    @Override
    public void close() throws IOException {
        if (bytesWritten >= 0) {
            finish();
            out.flush();
            out.close();
            bytesWritten = Long.MIN_VALUE;
        }
    }
}