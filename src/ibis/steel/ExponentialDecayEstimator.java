package ibis.steel;

/**
 * 
 * An estimator that uses an exponentially decaying average. That is, older
 * samples have exponentially decreasing influence on the estimate. The decay
 * factor is parameterized. The estimator assumes a Gaussian distribution of the
 * sampled signal.
 * 
 * @author Kees van Reeuwijk
 */
public class ExponentialDecayEstimator implements Estimator {
    private static final long serialVersionUID = 1L;
    private double average = 0.0;
    private double variance = 0.0;
    private final double alpha;
    private int sampleCount = 0;

    private ExponentialDecayEstimator(final double average,
            final double variance, final double alpha, final int sampleCount) {
        this.average = average;
        this.variance = variance;
        this.alpha = alpha;
        this.sampleCount = sampleCount;
    }

    public ExponentialDecayEstimator(final double average,
            final double variance, final double alpha) {
        this(average, variance, alpha, 1);
    }

    public ExponentialDecayEstimator(final double average, final double variance) {
        this(average, variance, 0.2, 1);
    }

    public ExponentialDecayEstimator(final Estimate est, final double alpha) {
        this.alpha = alpha;
        if (est instanceof ConstantEstimate) {
            final ConstantEstimate cest = (ConstantEstimate) est;
            average = Math.log(cest.v);
            variance = Math.log(10);
            this.sampleCount = 1;
        } else if (est instanceof GaussianEstimate) {
            final GaussianEstimate gest = (GaussianEstimate) est;
            average = gest.average;
            variance = gest.variance;
            this.sampleCount = gest.sampleCount;
        } else if (est instanceof LogGaussianEstimate) {
            final LogGaussianEstimate gest = (LogGaussianEstimate) est;
            average = Math.exp(gest.logAverage);
            variance = Math.exp(gest.logVariance);
            this.sampleCount = gest.sampleCount;
        } else {
            throw new IllegalArgumentException(
                    "ExponentialDecayEstimator: cannot initialize with a "
                            + est.getClass().getName() + " estimate");
        }
    }

    @Override
    public void addSample(final double x) {
        final double diff = x - average;
        final double incr = alpha * diff;
        average += incr;
        variance = (1 - alpha) * variance + diff * incr;
        sampleCount++;
    }

    @Override
    public double getHighEstimate() {
        return average + Math.sqrt(variance);
    }

    // FIXME: this is just an intuitive approximation of a likely values comp.
    private double getLikelyError() {
        return Math.sqrt(variance) / (1 - alpha);
    }

    private static double getLikelyValue(final double average,
            final double stdDev) {
        return average + stdDev * Globals.rng.nextGaussian();
    }

    @Override
    public double getLikelyValue() {
        final double err = getLikelyError();
        return getLikelyValue(average, err);
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
        return "average=" + Utils.formatNumber(average) + " stdDev="
                + Utils.formatNumber(stdDev) + " likely error="
                + Utils.formatNumber(err) + " samples=" + sampleCount;
    }

    @Override
    public Estimate getEstimate() {
        return new GaussianEstimate(average, variance, sampleCount);
    }

    @Override
    public String getStatisticsString() {
        return toString();
    }

    @Override
    public String format() {
        return Utils.formatNumber(average) + "\u00B1"
                + Utils.formatNumber(Math.sqrt(variance));
    }

}
