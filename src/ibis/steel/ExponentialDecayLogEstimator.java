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

    private ExponentialDecayLogEstimator(final double logAverage,
            final double logVariance, final double alpha, final int sampleCount) {
        this.logAverage = logAverage;
        this.logVariance = logVariance;
        this.alpha = alpha;
        this.sampleCount = sampleCount;
        if (logAverage > Globals.MAX_LOG || Double.isNaN(logAverage)
                || logVariance > Globals.MAX_LOG || Double.isNaN(logVariance)
                || logVariance < 0) {
            throw new IllegalArgumentException("Bad distribution: logAverage="
                    + logAverage + " logVariance=" + logVariance);
        }
    }

    /**
     * Constructs a new exponential decay estimator with a log-gaussian
     * probability model. Use the given log
     * 
     * @param logAverage
     *            The initial average of the log.
     * @param logVariance
     *            The initial variance of the log.
     * @param alpha
     *            The decay factor of the estimator.
     */
    public ExponentialDecayLogEstimator(final double logAverage,
            final double logVariance, final double alpha) {
        this(logAverage, logVariance, alpha, 1);
    }

    /**
     * Constructs a new estimator with the given estimate as initial sample.
     * 
     * @param est
     *            The estimate to use as sample.
     * @param alpha
     *            The decay factor of the estimator.
     */
    public ExponentialDecayLogEstimator(final Estimate est, final double alpha) {
        this.alpha = alpha;
        if (est instanceof ConstantEstimate) {
            final ConstantEstimate cest = (ConstantEstimate) est;
            logAverage = Math.log(cest.v);
            logVariance = Math.log(10);
            sampleCount = 1;
        } else if (est instanceof GaussianEstimate) {
            final GaussianEstimate gest = (GaussianEstimate) est;
            logAverage = Math.log(gest.average);
            logVariance = Math.log(gest.variance);
            sampleCount = gest.sampleCount;
        } else if (est instanceof LogGaussianEstimate) {
            final LogGaussianEstimate gest = (LogGaussianEstimate) est;
            logAverage = gest.logAverage;
            logVariance = gest.logVariance;
            sampleCount = gest.sampleCount;
        } else {
            throw new IllegalArgumentException(
                    "ExponentialDecayLogEstimator: cannot initialize with a "
                            + est.getClass().getName() + " estimate");
        }
        if (logAverage > Globals.MAX_LOG || Double.isNaN(logAverage)
                || logVariance > Globals.MAX_LOG || Double.isNaN(logVariance)
                || logVariance < 0) {
            throw new IllegalArgumentException("Bad distribution: logAverage="
                    + logAverage + " logVariance=" + logVariance + " est="
                    + est);
        }
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
    public Estimate getEstimate() {
        return new LogGaussianEstimate(logAverage, logVariance, sampleCount);
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
    public String format() {
        return Utils.formatNumber(Math.exp(logAverage)) + "~"
                + Utils.formatNumber(Math.exp(0.5 * logVariance));
    }

}
