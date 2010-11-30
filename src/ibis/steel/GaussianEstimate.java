package ibis.steel;

public class GaussianEstimate implements Estimate {
    private static final long serialVersionUID = 1L;
    protected final double average;
    protected final double variance;
    final int sampleCount;

    public GaussianEstimate(final double average, final double variance,
            final int sampleCount) {
        this.average = average;
        this.variance = variance;
        this.sampleCount = sampleCount;
    }

    @Override
    public Estimate addIndependent(final Estimate est) {
        if (est == null) {
            return null;
        }
        if (est instanceof GaussianEstimate) {
            final GaussianEstimate gest = (GaussianEstimate) est;
            return new GaussianEstimate(average + gest.average, variance
                    + gest.variance, Math.min(sampleCount, gest.sampleCount));
        }
        throw new IllegalArgumentException("GaussianEstimator: cannot add a "
                + est.getClass().getName() + " estimator");
    }

    @Override
    public Estimate multiply(final double c) {
        return new GaussianEstimate(c * average, c * c * variance, sampleCount);
    }

    @Override
    public String format() {
        return Utils.formatNumber(average) + "\u00B1"
                + Utils.formatNumber(Math.sqrt(variance));
    }

    // FIXME: this is just an intuitive approximation of a likely values comp.
    double getLikelyError() {
        final double stdDev = Math.sqrt(variance);
        return stdDev + 0.1 * average / Math.sqrt(sampleCount);
    }

    @Override
    public double getLikelyValue() {
        final double err = getLikelyError();
        return average + err * Globals.rng.nextGaussian();
    }

    @Override
    public double getHighEstimate() {
        if (sampleCount < 1) {
            return Double.POSITIVE_INFINITY;
        }
        return average + Math.sqrt(variance);
    }
}
