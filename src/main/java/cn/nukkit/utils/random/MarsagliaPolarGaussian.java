package cn.nukkit.utils.random;

public class MarsagliaPolarGaussian {
    public final RandomSource randomSource;
    private double nextNextGaussian;
    private boolean haveNextNextGaussian;

    public MarsagliaPolarGaussian(RandomSource baseRandom) {
        this.randomSource = baseRandom;
    }

    public void reset() {
        this.haveNextNextGaussian = false;
    }

    public double nextGaussian() {
        if (this.haveNextNextGaussian) {
            this.haveNextNextGaussian = false;
            return this.nextNextGaussian;
        } else {
            double d;
            double e;
            double f;
            do {
                d = 2.0D * this.randomSource.nextDouble() - 1.0D;
                e = 2.0D * this.randomSource.nextDouble() - 1.0D;
                f = d * d + e * e;
            } while (f >= 1.0D || f == 0.0D);

            double g = Math.sqrt(-2.0D * Math.log(f) / f);
            this.nextNextGaussian = e * g;
            this.haveNextNextGaussian = true;
            return d * g;
        }
    }
}

