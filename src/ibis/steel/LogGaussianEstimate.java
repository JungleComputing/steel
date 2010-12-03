package ibis.steel;

/**
 * An estimate using a log-gaussian model. That is, the log of the value is
 * modeled as having a Gaussian distribution.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public class LogGaussianEstimate implements Estimate {
    private static final long serialVersionUID = 1L;
    final double logAverage;
    final double logVariance;
    final int sampleCount;

    /**
     * Constructs a new log-gaussian estimate with the given average and
     * variance for the log of the estimate, and with the given sample count.
     * 
     * @param logAverage
     *            The average of the log of the estimated value.
     * @param logVariance
     *            The variance of the log of the estimated value.
     * @param sampleCount
     *            The number of samples the estimate is based on.
     */
    public LogGaussianEstimate(final double logAverage,
            final double logVariance, final int sampleCount) {
        this.logAverage = logAverage;
        this.logVariance = logVariance;
        this.sampleCount = sampleCount;
        if (Double.isInfinite(logAverage) || Double.isNaN(logAverage)
                || Double.isInfinite(logVariance) || Double.isNaN(logVariance)
                || logVariance < 0 || Double.isInfinite(Math.exp(logAverage))
                || Double.isInfinite(Math.exp(logVariance))) {
            throw new IllegalArgumentException("Bad distribution: logAverage="
                    + logAverage + " logVariance=" + logVariance);
        }
    }

    @Override
    public Estimate addIndependent(final Estimate est) {
        if (est instanceof InfiniteEstimate) {
            return est;
        }
        if (est instanceof ConstantEstimate) {
            final ConstantEstimate ce = (ConstantEstimate) est;
            final double av = ce.v + Math.exp(logAverage);
            return new LogGaussianEstimate(Math.log(av), logVariance,
                    sampleCount);
        }
        if (est instanceof LogGaussianEstimate) {
            final LogGaussianEstimate lest = (LogGaussianEstimate) est;
            final double av = Math.exp(logAverage) + Math.exp(lest.logAverage);
            final double var = Math.exp(logVariance)
                    + Math.exp(lest.logVariance);
            return new LogGaussianEstimate(Math.log(av), Math.log(var),
                    Math.min(sampleCount, lest.sampleCount));
        }
        throw new IllegalArgumentException("LogGaussianEstimate: cannot add a "
                + est.getClass().getName() + " estimate");
    }

    @Override
    public Estimate multiply(final double c) {
        final double lc = Math.log(c);
        return new LogGaussianEstimate(lc + logAverage, 2 * lc + logVariance,
                sampleCount);
    }

    @Override
    public String toString() {
        return String.format("e^(%.3g+/-%.3g)", logAverage, getLogStdDev());
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
