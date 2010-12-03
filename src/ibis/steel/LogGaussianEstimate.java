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
    final double logMean;
    final double logVariance;
    final int sampleCount;

    /**
     * Constructs a new log-gaussian estimate with the given mean and variance
     * for the log of the estimate, and with the given sample count.
     * 
     * @param logMean
     *            The mean of the log of the estimated value.
     * @param logVariance
     *            The variance of the log of the estimated value.
     * @param sampleCount
     *            The number of samples the estimate is based on.
     */
    public LogGaussianEstimate(final double logMean, final double logVariance,
            final int sampleCount) {
        this.logMean = logMean;
        this.logVariance = logVariance;
        this.sampleCount = sampleCount;
        if (logMean > Globals.MAX_LOG || Double.isNaN(logMean)
                || logVariance > Globals.MAX_LOG || Double.isNaN(logVariance)
                || logVariance < 0) {
            throw new IllegalArgumentException("Bad distribution: logMean="
                    + logMean + " logVariance=" + logVariance);
        }
    }

    @Override
    public Estimate addIndependent(final Estimate est) {
        if (est instanceof InfiniteEstimate) {
            return est;
        }
        if (est instanceof ConstantEstimate) {
            final ConstantEstimate ce = (ConstantEstimate) est;
            final double av = ce.v + Math.exp(logMean);
            return new LogGaussianEstimate(Math.log(av), logVariance,
                    sampleCount);
        }
        if (est instanceof LogGaussianEstimate) {
            final LogGaussianEstimate lest = (LogGaussianEstimate) est;
            final double av = Math.exp(logMean) + Math.exp(lest.logMean);
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
        if (c < 0) {
            throw new IllegalArgumentException("Negative multiplier: c=" + c);
        }
        if (c == 0) {
            return ConstantEstimate.ZERO;
        }
        final double lc = Math.log(c);
        return new LogGaussianEstimate(lc + logMean, logVariance, sampleCount);
    }

    @Override
    public String toString() {
        return String.format("e^(%.3g+/-%.3g)", logMean, getLogStdDev());
    }

    private double getLogStdDev() {
        return Math.sqrt(logVariance);
    }

    @Override
    public double getLikelyValue() {
        final double v = logMean + getLogStdDev() * Globals.rng.nextGaussian();
        return Math.exp(v);
    }

    @Override
    public double getHighEstimate() {
        final double stdDev = getLogStdDev();
        final double logMax = logMean + stdDev;
        return Math.exp(logMax);
    }
}
