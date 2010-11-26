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
public class ExponentialDecayLogEstimator implements Estimator {
    private static final long serialVersionUID = 1L;
    private double logAverage;
    private double logVariance;
    private final double alpha;
    private int sampleCount = 0;

    private ExponentialDecayLogEstimator(final double average,
            final double variance, final double alpha, final int sampleCount) {
        logAverage = Math.log(average);
        logVariance = Math.log(variance);
        this.alpha = alpha;
        this.sampleCount = sampleCount;
    }

    public ExponentialDecayLogEstimator(final double average,
            final double variance, final double alpha) {
        this(average, variance, alpha, 1);
    }

    public ExponentialDecayLogEstimator(final double average,
            final double variance) {
        this(average, variance, 0.2, 1);
    }

    @Override
    public void addSample(final double xx) {
        final double x = Math.log(xx);
        final double diff = x - logAverage;
        final double incr = alpha * diff;
        logAverage += incr;
        logVariance = (1 - alpha) * logVariance + diff * incr;
        sampleCount++;
    }

    @Override
    public double getHighEstimate() {
        if (sampleCount < 2) {
            return Double.POSITIVE_INFINITY;
        }
        return Math.exp(logAverage) + Math.exp(0.5 * logVariance);
    }

    // FIXME: this is just an intuitive approximation of a likely values comp.
    private double getLikelyError() {
        return Math.sqrt(logVariance) / (1 - alpha);
    }

    private static double getLikelyValue(final double average,
            final double stdDev) {
        return average + stdDev * Globals.rng.nextGaussian();
    }

    @Override
    public double getLikelyValue() {
        final double err = getLikelyError();
        return Math.exp(getLikelyValue(logAverage, err));
    }

    @Override
    public String getName() {
        return "log-exponential-decay";
    }

    @Override
    public int getSampleCount() {
        return sampleCount;
    }

    @Override
    public String toString() {
        return format();
    }

    @Override
    public Estimator getEstimate() {
        return new ExponentialDecayLogEstimator(logAverage, logVariance, alpha,
                sampleCount);
    }

    @Override
    public String getStatisticsString() {
        final double stdDev = Math.sqrt(logVariance);
        final double rangeMin = Math.exp(logAverage - stdDev);
        final double rangeMax = Math.exp(logAverage + stdDev);
        return "average=" + Utils.formatNumber(Math.exp(logAverage))
                + " range=" + Utils.formatNumber(rangeMin) + "..."
                + Utils.formatNumber(rangeMax) + " samples=" + sampleCount;
    }

    @Override
    public Estimator addIndependent(final Estimator est) {
        if (est instanceof ConstantEstimator) {
            final ConstantEstimator cest = (ConstantEstimator) est;
            final double v = Math.exp(logAverage)
                    + Math.exp(cest.getLikelyValue());
            return new ExponentialDecayLogEstimator(Math.log(v), logVariance,
                    alpha, sampleCount);
        } else if (est instanceof ExponentialDecayLogEstimator) {
            final ExponentialDecayLogEstimator lest = (ExponentialDecayLogEstimator) est;
            final double av = Math.exp(logAverage) + Math.exp(lest.logAverage);
            final double var = Math.exp(logVariance)
                    + Math.exp(lest.logVariance);
            return new ExponentialDecayLogEstimator(Math.log(av),
                    Math.log(var), alpha, Math.min(sampleCount,
                            lest.sampleCount));
        }
        throw new IllegalArgumentException("GaussianEstimator: cannot add a "
                + est.getClass().getName() + " estimator");
    }

    @Override
    public Estimator multiply(final double c) {
        final double lc = Math.log(c);
        return new ExponentialDecayLogEstimator(lc + logAverage, 2 * lc
                + logVariance, alpha, sampleCount);
    }

    @Override
    public String format() {
        return Utils.formatNumber(Math.exp(logAverage)) + "~"
                + Utils.formatNumber(Math.exp(0.5 * logVariance));
    }

    @Override
    public double getAverage() {
        return Math.exp(logAverage);
    }
}
