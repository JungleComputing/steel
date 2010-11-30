package ibis.steel;

public class LogGaussianEstimate implements Estimate {
    private static final long serialVersionUID = 1L;
    final double logAverage;
    final double logVariance;
    final int sampleCount;

    LogGaussianEstimate(final double average, final double variance,
            final int sampleCount) {
        this.logAverage = average;
        this.logVariance = variance;
        this.sampleCount = sampleCount;
    }

    @Override
    public Estimate addIndependent(final Estimate est) {
        if (est == null) {
            return null;
        }
        if (est instanceof LogGaussianEstimate) {
            final LogGaussianEstimate lest = (LogGaussianEstimate) est;
            final double av = Math.exp(logAverage) + Math.exp(lest.logAverage);
            final double var = Math.exp(logVariance)
                    + Math.exp(lest.logVariance);
            return new LogGaussianEstimate(Math.log(av), Math.log(var),
                    Math.min(sampleCount, lest.sampleCount));
        }
        throw new IllegalArgumentException("GaussianEstimator: cannot add a "
                + est.getClass().getName() + " estimator");
    }

    @Override
    public Estimate multiply(final double c) {
        final double lc = Math.log(c);
        return new LogGaussianEstimate(lc + logAverage, 2 * lc + logVariance,
                sampleCount);
    }

    @Override
    public String format() {
        return Utils.formatNumber(Math.exp(logAverage)) + "~"
                + Utils.formatNumber(Math.exp(0.5 * logVariance));
    }

    private double getAverage() {
        return Math.exp(logAverage);
    }

    private double getLogStdDev() {
        return Math.sqrt(logVariance);
    }

    @Override
    public double getLikelyValue() {
        final double v = logAverage + getLogStdDev()
                * Globals.rng.nextGaussian();
        return Math.exp(v);
    }

    @Override
    public double getHighEstimate() {
        final double stdDev = getLogStdDev();
        final double logMax = logAverage + stdDev;
        return Math.exp(logMax);
    }
}
