package ibis.steel;

/**
 * An estimator that assumes a log Gaussian distribution for the estimated
 * value. This distribution is particularly suitable for time estimates, since
 * it never results in negative values, and makes very small values very
 * unlikely.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public class LogGaussianEstimator implements Estimator {
    private static final long serialVersionUID = 1L;
    private double logMean = 0.0;
    private double logS = 0.0;
    private int sampleCount = 0;

    private LogGaussianEstimator(final double logMean,
            final double logVariance, final int sampleCount) {
        if (Double.isInfinite(logMean) || Double.isNaN(logMean)
                || Double.isInfinite(logVariance) || Double.isNaN(logVariance)
                || logVariance < 0) {
            throw new IllegalArgumentException("Bad distribution: logMean="
                    + logMean + " logVariance=" + logVariance);
        }
        this.logMean = logMean;
        logS = sampleCount * logVariance;
        this.sampleCount = sampleCount;
    }

    /**
     * Constructs a new log-gaussian estimator with the given initial mean and
     * standard deviation.
     * 
     * @param logMean
     *            The initial mean of the log.
     * @param logVariance
     *            The initial variance of the log.
     */
    public LogGaussianEstimator(final double logMean, final double logVariance) {
        this(logMean, logVariance, 1);
    }

    private double getLogStdDev() {
        return Math.sqrt(logS / sampleCount);
    }

    @Override
    public double getLikelyValue() {
        final double v = logMean + getLogStdDev() * Globals.rng.nextGaussian();
        return Math.exp(v);
    }

    @Override
    public void addSample(final double v) {
        if (v <= 0 || Double.isInfinite(v)) {
            throw new IllegalArgumentException("Bad sample: v=" + v);
        }
        final double value = Math.log(v);
        sampleCount++;
        final double oldMean = logMean;
        logMean += (value - logMean) / sampleCount;
        logS += (value - oldMean) * (value - logMean);
    }

    @Override
    public String getName() {
        return "log-gaussian";
    }

    @Override
    public double getHighEstimate() {
        final double stdDev = getLogStdDev();
        final double logMax = logMean + stdDev;
        return Math.exp(logMax);
    }

    @Override
    public int getSampleCount() {
        return sampleCount;
    }

    @Override
    public String getStatisticsString() {
        final double stdDev = getLogStdDev();
        final double rangeMin = Math.exp(logMean - stdDev);
        final double rangeMax = Math.exp(logMean + stdDev);
        return "mean=" + Utils.formatNumber(Math.exp(logMean)) + " range="
                + Utils.formatNumber(rangeMin) + "..."
                + Utils.formatNumber(rangeMax) + " samples=" + sampleCount;
    }

    @Override
    public Estimate getEstimate() {
        return new LogGaussianEstimate(logMean, logS / sampleCount, sampleCount);
    }

    @Override
    public String format() {
        return Utils.formatNumber(Math.exp(logMean)) + "~"
                + Utils.formatNumber(Math.exp(0.5 * logS / sampleCount));
    }

}
