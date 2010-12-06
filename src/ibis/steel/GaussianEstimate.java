package ibis.steel;

/**
 * 
 * An estimate that models the value as having a Gaussian distribution.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public class GaussianEstimate implements Estimate {
    private static final long serialVersionUID = 1L;
    protected final double mean;
    protected final double variance;
    final int sampleCount;

    /**
     * Constructs a new estimate with the given mean and variance for the value,
     * and the given sample count.
     * 
     * @param mean
     *            The mean of the estimated value.
     * @param variance
     *            The variance of the estimated value.
     * @param sampleCount
     *            The number of samples the estimate is based on.
     */
    public GaussianEstimate(final double mean, final double variance,
            final int sampleCount) {
        this.mean = mean;
        this.variance = variance;
        this.sampleCount = sampleCount;
    }

    @Override
    public Estimate addIndependent(final Estimate est) {
        if (est instanceof InfiniteEstimate) {
            return est;
        }
        if (est instanceof ConstantEstimate) {
            final ConstantEstimate ce = (ConstantEstimate) est;
            return new GaussianEstimate(ce.v + mean, variance, sampleCount);
        }
        if (est instanceof GaussianEstimate) {
            final GaussianEstimate gest = (GaussianEstimate) est;
            return new GaussianEstimate(mean + gest.mean, variance
                    + gest.variance, Math.min(sampleCount, gest.sampleCount));
        }
        throw new IllegalArgumentException("GaussianEstimate: cannot add a "
                + est.getClass().getName() + " estimate");
    }

    @Override
    public Estimate multiply(final double c) {
        return new GaussianEstimate(c * mean, c * c * variance, sampleCount);
    }

    @Override
    public String toString() {
        return Utils.formatNumber(mean) + "+-"
                + Utils.formatNumber(Math.sqrt(variance));
    }

    // FIXME: this is just an intuitive approximation of a likely values comp.
    double getLikelyError() {
        final double stdDev = Math.sqrt(variance);
        return stdDev + 0.1 * mean / Math.sqrt(sampleCount);
    }

    @Override
    public double getLikelyValue() {
        final double err = getLikelyError();
        return mean + err * Globals.rng.nextGaussian();
    }

    @Override
    public double getHighEstimate() {
        return mean + Math.sqrt(variance);
    }
}
