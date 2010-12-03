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
public class ExponentialDecayLogEstimator implements Estimator {
    private static final long serialVersionUID = 1L;
    private double logMean;
    private double logVariance;
    private final double alpha;
    private int sampleCount = 0;

    private ExponentialDecayLogEstimator(final double logMean,
            final double logVariance, final double alpha, final int sampleCount) {
        this.logMean = logMean;
        this.logVariance = logVariance;
        this.alpha = alpha;
        this.sampleCount = sampleCount;
        if (logMean > Globals.MAX_LOG || Double.isNaN(logMean)
                || logVariance > Globals.MAX_LOG || Double.isNaN(logVariance)
                || logVariance < 0) {
            throw new IllegalArgumentException("Bad distribution: logMean="
                    + logMean + " logVariance=" + logVariance);
        }
    }

    /**
     * Constructs a new exponential decay estimator with a log-gaussian
     * probability model. Use the given log
     * 
     * @param logMean
     *            The initial mean of the log.
     * @param logVariance
     *            The initial variance of the log.
     * @param alpha
     *            The decay factor of the estimator.
     */
    public ExponentialDecayLogEstimator(final double logMean,
            final double logVariance, final double alpha) {
        this(logMean, logVariance, alpha, 1);
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
            logMean = Math.log(cest.v);
            logVariance = Math.log(10);
            sampleCount = 1;
        } else if (est instanceof GaussianEstimate) {
            final GaussianEstimate gest = (GaussianEstimate) est;
            logMean = Math.log(gest.mean);
            logVariance = Math.log(gest.variance);
            sampleCount = gest.sampleCount;
        } else if (est instanceof LogGaussianEstimate) {
            final LogGaussianEstimate gest = (LogGaussianEstimate) est;
            logMean = gest.logMean;
            logVariance = gest.logVariance;
            sampleCount = gest.sampleCount;
        } else {
            throw new IllegalArgumentException(
                    "ExponentialDecayLogEstimator: cannot initialize with a "
                            + est.getClass().getName() + " estimate");
        }
        if (logMean > Globals.MAX_LOG || Double.isNaN(logMean)
                || logVariance > Globals.MAX_LOG || Double.isNaN(logVariance)
                || logVariance < 0) {
            throw new IllegalArgumentException("Bad distribution: logMean="
                    + logMean + " logVariance=" + logVariance + " est=" + est);
        }
    }

    @Override
    public void addSample(final double v) {
        if (v <= 0 || Double.isInfinite(v)) {
            throw new IllegalArgumentException("Bad sample: v=" + v);
        }
        final double x = Math.log(v);
        final double diff = x - logMean;
        final double incr = alpha * diff;
        logMean += incr;
        logVariance += incr * (x - logMean);
        sampleCount++;
        if (Double.isNaN(logMean) || Double.isNaN(logVariance)
                || logMean > Globals.MAX_LOG || logVariance > Globals.MAX_LOG) {
            throw new IllegalArgumentException("Bad sample: v=" + v
                    + " logMean=" + logMean + " logVariance=" + logVariance
                    + " incr=" + incr);
        }
    }

    @Override
    public double getHighEstimate() {
        return Math.exp(logMean) + Math.exp(0.5 * logVariance);
    }

    // FIXME: this is just an intuitive approximation of a likely values comp.
    private double getLikelyError() {
        return Math.sqrt(logVariance) / (1 - alpha);
    }

    private static double getLikelyValue(final double mean, final double stdDev) {
        return mean + stdDev * Globals.rng.nextGaussian();
    }

    @Override
    public double getLikelyValue() {
        final double err = getLikelyError();
        return Math.exp(getLikelyValue(logMean, err));
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
        return new LogGaussianEstimate(logMean, logVariance, sampleCount);
    }

    @Override
    public String getStatisticsString() {
        final double stdDev = Math.sqrt(logVariance);
        final double rangeMin = Math.exp(logMean - stdDev);
        final double rangeMax = Math.exp(logMean + stdDev);
        return "mean=" + Utils.formatNumber(Math.exp(logMean)) + " range="
                + Utils.formatNumber(rangeMin) + "..."
                + Utils.formatNumber(rangeMax) + " samples=" + sampleCount;
    }

    @Override
    public String format() {
        return Utils.formatNumber(Math.exp(logMean)) + "~"
                + Utils.formatNumber(Math.exp(0.5 * logVariance));
    }

}
