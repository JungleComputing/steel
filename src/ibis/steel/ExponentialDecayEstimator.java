package ibis.steel;

/**
 * 
 * An estimator that uses an exponentially decaying mean. That is, older samples
 * have exponentially decreasing influence on the estimate. The decay factor is
 * parameterized. The estimator assumes a Gaussian distribution of the sampled
 * signal.
 * 
 * @author Kees van Reeuwijk
 */
public class ExponentialDecayEstimator implements Estimator {
    private static final long serialVersionUID = 1L;
    private double mean = 0.0;
    private double variance = 0.0;
    private final double alpha;
    private int sampleCount = 0;

    private ExponentialDecayEstimator(final double mean, final double variance,
            final double alpha, final int sampleCount) {
        this.mean = mean;
        this.variance = variance;
        this.alpha = alpha;
        this.sampleCount = sampleCount;
    }

    public ExponentialDecayEstimator(final double mean, final double variance,
            final double alpha) {
        this(mean, variance, alpha, 1);
    }

    public ExponentialDecayEstimator(final double mean, final double variance) {
        this(mean, variance, 0.2, 1);
    }

    public ExponentialDecayEstimator(final Estimate est, final double alpha) {
        this.alpha = alpha;
        if (est instanceof ConstantEstimate) {
            final ConstantEstimate cest = (ConstantEstimate) est;
            mean = Math.log(cest.v);
            variance = Math.log(10);
            sampleCount = 1;
        } else if (est instanceof GaussianEstimate) {
            final GaussianEstimate gest = (GaussianEstimate) est;
            mean = gest.mean;
            variance = gest.variance;
            sampleCount = gest.sampleCount;
        } else if (est instanceof LogGaussianEstimate) {
            final LogGaussianEstimate gest = (LogGaussianEstimate) est;
            mean = Math.exp(gest.logMean);
            variance = Math.exp(gest.logVariance);
            sampleCount = gest.sampleCount;
        } else {
            throw new IllegalArgumentException(
                    "ExponentialDecayEstimator: cannot initialize with a "
                            + est.getClass().getName() + " estimate");
        }
    }

    @Override
    public void addSample(final double x) {
        final double diff = x - mean;
        final double incr = alpha * diff;
        mean += incr;
        variance = (1 - alpha) * (variance + diff * incr);
        sampleCount++;
    }

    @Override
    public double getHighEstimate() {
        return mean + Math.sqrt(variance);
    }

    // FIXME: this is just an intuitive approximation of a likely values comp.
    private double getLikelyError() {
        return Math.sqrt(variance) / (1 - alpha);
    }

    private static double getLikelyValue(final double mean, final double stdDev) {
        return mean + stdDev * Globals.rng.nextGaussian();
    }

    @Override
    public double getLikelyValue() {
        final double err = getLikelyError();
        return getLikelyValue(mean, err);
    }

    @Override
    public String getName() {
        return "exponential-decay";
    }

    @Override
    public int getSampleCount() {
        return sampleCount;
    }

    @Override
    public String toString() {
        final double stdDev = Math.sqrt(variance);
        final double err = getLikelyError();
        return "mean=" + Utils.formatNumber(mean) + " stdDev="
                + Utils.formatNumber(stdDev) + " likely error="
                + Utils.formatNumber(err) + " samples=" + sampleCount;
    }

    @Override
    public Estimate getEstimate() {
        return new GaussianEstimate(mean, variance, sampleCount);
    }

    @Override
    public String getStatisticsString() {
        return toString();
    }

    @Override
    public String format() {
        return Utils.formatNumber(mean) + "\u00B1"
                + Utils.formatNumber(Math.sqrt(variance));
    }

}
