package cn.nukkit.utils.random;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;

import java.util.concurrent.atomic.AtomicLong;

public class RandomSupport {
    public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
    public static final long SILVER_RATIO_64 = 7640891576956012809L;
    private static final HashFunction MD5_128 = Hashing.md5();
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

    public static long mixStafford13(long seed) {
        seed = (seed ^ seed >>> 30) * -4658895280553007687L;
        seed = (seed ^ seed >>> 27) * -7723592293110705685L;
        return seed ^ seed >>> 31;
    }

    public static RandomSupport.Seed128bit upgradeSeedTo128bitUnmixed(long seed) {
        long l = seed ^ 7640891576956012809L;
        long m = l + -7046029254386353131L;
        return new RandomSupport.Seed128bit(l, m);
    }

    public static RandomSupport.Seed128bit upgradeSeedTo128bit(long seed) {
        return upgradeSeedTo128bitUnmixed(seed).mixed();
    }

    public static RandomSupport.Seed128bit seedFromHashOf(String seed) {
        byte[] bs = MD5_128.hashString(seed, Charsets.UTF_8).asBytes();
        long l = Longs.fromBytes(bs[0], bs[1], bs[2], bs[3], bs[4], bs[5], bs[6], bs[7]);
        long m = Longs.fromBytes(bs[8], bs[9], bs[10], bs[11], bs[12], bs[13], bs[14], bs[15]);
        return new RandomSupport.Seed128bit(l, m);
    }

    public static long generateUniqueSeed() {
        return SEED_UNIQUIFIER.updateAndGet((seedUniquifier) -> {
            return seedUniquifier * 1181783497276652981L;
        }) ^ System.nanoTime();
    }

    public record Seed128bit(long seedLo, long seedHi) {
        public RandomSupport.Seed128bit xor(long seedLo, long seedHi) {
            return new RandomSupport.Seed128bit(this.seedLo ^ seedLo, this.seedHi ^ seedHi);
        }

        public RandomSupport.Seed128bit xor(RandomSupport.Seed128bit seed) {
            return this.xor(seed.seedLo, seed.seedHi);
        }

        public RandomSupport.Seed128bit mixed() {
            return new RandomSupport.Seed128bit(RandomSupport.mixStafford13(this.seedLo), RandomSupport.mixStafford13(this.seedHi));
        }
    }
}
