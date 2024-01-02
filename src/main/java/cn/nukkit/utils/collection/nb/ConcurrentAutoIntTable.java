package cn.nukkit.utils.collection.nb;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;


/**
 * An auto-resizing table of {@code longs}, supporting low-contention CAS
 * operations.  Updates are done with CAS's to no particular table element.
 * The intent is to support highly scalable counters, r/w locks, and other
 * structures where the updates are associative, loss-free (no-brainer), and
 * otherwise happen at such a high volume that the cache contention for
 * CAS'ing a single word is unacceptable.
 *
 * @author Cliff Click, SuperIceCN
 * @since 1.5
 */


public class ConcurrentAutoIntTable implements Serializable {

    // --- public interface ---

    /**
     * Add the given value to current counter value.  Concurrent updates will
     * not be lost, but addAndGet or getAndAdd are not implemented because the
     * total counter value (i.e., {@link #get}) is not atomically updated.
     * Updates are striped across an array of counters to avoid cache contention
     * and has been tested with performance scaling linearly up to 768 CPUs.
     */
    public void add(int x) {
        add_if(x);
    }

    /**
     * {@link #add} with -1
     */
    public void decrement() {
        add_if(-1);
    }

    /**
     * {@link #add} with +1
     */
    public void increment() {
        add_if(1);
    }

    /**
     * Atomically set the sum of the striped counters to specified value.
     * Rather more expensive than a simple store, in order to remain atomic.
     */
    public void set(int x) {
        CAT newcat = new CAT(null, 4, x);
        // Spin until CAS works
        while (!CAS_cat(_cat, newcat)) {/*empty*/}
    }

    /**
     * Current value of the counter.  Since other threads are updating furiously
     * the value is only approximate, but it includes all counts made by the
     * current thread.  Requires a pass over the internally striped counters.
     */
    public long get() {
        return _cat.sum();
    }

    /**
     * Same as {@link #get}, included for completeness.
     */
    public int intValue() {
        return (int) _cat.sum();
    }

    /**
     * Same as {@link #get}, included for completeness.
     */
    public long longValue() {
        return _cat.sum();
    }

    /**
     * A cheaper {@link #get}.  Updated only once/millisecond, but as fast as a
     * simple load instruction when not updating.
     */
    public long estimate_get() {
        return _cat.estimate_sum();
    }

    /**
     * Return the counter's {@code long} value converted to a string.
     */
    public String toString() {
        return _cat.toString();
    }

    /**
     * Return the internal counter striping factor.  Useful for diagnosing
     * performance problems.
     */
    public int internal_size() {
        return _cat._t.length;
    }

    // Only add 'x' to some slot in table, hinted at by 'hash'.  The sum can
    // overflow.  Value is CAS'd so no counts are lost.  The CAS is retried until
    // it succeeds.  Returned value is the old value.
    private long add_if(int x) {
        return _cat.add_if(x, hash(), this);
    }

    // The underlying array of concurrently updated long counters
    private volatile CAT _cat = new CAT(null, 16/*Start Small, Think Big!*/, 0);
    private static AtomicReferenceFieldUpdater<ConcurrentAutoIntTable, CAT> _catUpdater =
            AtomicReferenceFieldUpdater.newUpdater(ConcurrentAutoIntTable.class, CAT.class, "_cat");

    private boolean CAS_cat(CAT oldcat, CAT newcat) {
        return _catUpdater.compareAndSet(this, oldcat, newcat);
    }

    // Hash spreader
    private static int hash() {
        //int h = (int)Thread.currentThread().getId();
        int h = System.identityHashCode(Thread.currentThread());
        return h << 3;                // Pad out cache lines.  The goal is to avoid cache-line contention
    }

    // --- CAT -----------------------------------------------------------------
    private static class CAT implements Serializable {
        private static final VarHandle _IHandle = MethodHandles.arrayElementVarHandle(int[].class);

        private static boolean CAS(int[] A, int idx, int old, int nnn) {
            return _IHandle.compareAndSet(A, idx, old, nnn);
        }

        //volatile long _resizers;    // count of threads attempting a resize
        //static private final AtomicLongFieldUpdater<CAT> _resizerUpdater =
        //  AtomicLongFieldUpdater.newUpdater(CAT.class, "_resizers");

        private final CAT _next;
        private volatile long _fuzzy_sum_cache;
        private volatile long _fuzzy_time;
        private static final int MAX_SPIN = 1;
        private final int[] _t;     // Power-of-2 array of ints

        CAT(CAT next, int sz, int init) {
            _next = next;
            _t = new int[sz];
            _t[0] = init;
        }

        // Only add 'x' to some slot in table, hinted at by 'hash'.  The sum can
        // overflow.  Value is CAS'd so no counts are lost.  The CAS is attempted
        // ONCE.
        public int add_if(int x, int hash, ConcurrentAutoIntTable master) {
            final int[] t = _t;
            final int idx = hash & (t.length - 1);
            // Peel loop; try once fast
            int old = t[idx];
            final boolean ok = CAS(t, idx, old, old + x);
            if (ok) return old;      // Got it
            // Try harder
            int cnt = 0;
            while (true) {
                old = t[idx];
                if (CAS(t, idx, old, old + x)) break; // Got it!
                cnt++;
            }
            if (cnt < MAX_SPIN) return old; // Allowable spin loop count
            if (t.length >= 1024 * 1024) return old; // too big already

            // Too much contention; double array size in an effort to reduce contention
            //long r = _resizers;
            //final int newbytes = (t.length<<1)<<3/*word to bytes*/;
            //while( !_resizerUpdater.compareAndSet(this,r,r+newbytes) )
            //  r = _resizers;
            //r += newbytes;
            if (master._cat != this) return old; // Already doubled, don't bother
            //if( (r>>17) != 0 ) {      // Already too much allocation attempts?
            //  // We could use a wait with timeout, so we'll wakeup as soon as the new
            //  // table is ready, or after the timeout in any case.  Annoyingly, this
            //  // breaks the non-blocking property - so for now we just briefly sleep.
            //  //synchronized( this ) { wait(8*megs); }         // Timeout - we always wakeup
            //  try { Thread.sleep(r>>17); } catch( InterruptedException e ) { }
            //  if( master._cat != this ) return old;
            //}

            CAT newcat = new CAT(this, t.length * 2, 0);
            // Take 1 stab at updating the CAT with the new larger size.  If this
            // fails, we assume some other thread already expanded the CAT - so we
            // do not need to retry until it succeeds.
            while (master._cat == this && !master.CAS_cat(this, newcat)) {/*empty*/}
            return old;
        }

        // Return the current sum of all things in the table.  Writers can be
        // updating the table furiously, so the sum is only locally accurate.
        public long sum() {
            long sum = _next == null ? 0 : _next.sum(); // Recursively get cached sum
            final int[] t = _t;
            for (int cnt : t) sum += cnt;
            return sum;
        }

        // Fast fuzzy version.  Used a cached value until it gets old, then re-up
        // the cache.
        public long estimate_sum() {
            // For short tables, just do the work
            if (_t.length <= 64) return sum();
            // For bigger tables, periodically freshen a cached value
            long millis = System.currentTimeMillis();
            if (_fuzzy_time != millis) { // Time marches on?
                _fuzzy_sum_cache = sum(); // Get sum the hard way
                _fuzzy_time = millis;   // Indicate freshness of cached value
            }
            return _fuzzy_sum_cache;  // Return cached sum
        }

        public String toString() {
            return Long.toString(sum());
        }
    }
}
