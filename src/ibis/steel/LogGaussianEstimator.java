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
    private double logAverage = 0.0;
    private double logS = 0.0;
    private int sampleCount = 0;

    private LogGaussianEstimator(final double logAverage, final double logS,
            final int sampleCount) {
        this.logAverage = logAverage;
        this.logS = logS;
        this.sampleCount = sampleCount;
    }

    /**
     * Constructs a new log-gaussian estimator with the given initial average
     * and standard deviation.
     * 
     * @param average
     *            The initial average.
     * @param stdDev
     *            The initial standard deviation.
     */
    public LogGaussianEstimator(final double average, final double stdDev) {
        this(Math.log(average), 2 * Math.log(stdDev), 1);
    }

    private double getLogStdDev() {
        return Math.sqrt(logS / sampleCount);
    }

    @Override
    public double getLikelyValue() {
        final double v = logAverage + getLogStdDev()
                * Globals.rng.nextGaussian();
        return Math.exp(v);
    }

    @Override
    public void addSample(final double v) {
        final double value = Math.log(v);
        sampleCount++;
        final double oldAverage = logAverage;
        logAverage += (value - logAverage) / sampleCount;
        logS += (value - oldAverage) * (value - logAverage);
    }

    @Override
    public String getName() {
        return "log-gaussian";
    }

    @Override
    public double getHighEstimate() {
        final double stdDev = getLogStdDev();
        final double logMax = logAverage + stdDev;
        return Math.exp(logMax);
    }

    @Override
    public int getSampleCount() {
        return sampleCount;
    }

    @Override
    public String getStatisticsString() {
        final double stdDev = getLogStdDev();
        final double rangeMin = Math.exp(logAverage - stdDev);
        final double rangeMax = Math.exp(logAverage + stdDev);
        return "average=" + Utils.formatNumber(Math.exp(logAverage))
                + " range=" + Utils.formatNumber(rangeMin) + "..."
                + Utils.formatNumber(rangeMax) + " samples=" + sampleCount;
    }

    @Override
    public Estimator getEstimate() {
        return new LogGaussianEstimator(logAverage, logS, sampleCount);
    }

    @Override
    public Estimator addIndependent(final Estimator est) {
        if (est == null) {
            return null;
        }
        if (est instanceof ConstantEstimator) {
            final ConstantEstimator cest = (ConstantEstimator) est;
            final double v = Math.exp(logAverage)
                    + Math.exp(cest.getLikelyValue());
            return new LogGaussianEstimator(Math.log(v), logS, sampleCount);
        }
        if (est instanceof LogGaussianEstimator) {
            final LogGaussianEstimator lest = (LogGaussianEstimator) est;
            final double v = Math.exp(logAverage) + Math.exp(lest.logAverage);
            final double S = Math.exp(logS) + Math.exp(lest.logS);
            return new LogGaussianEstimator(Math.log(v), Math.log(S), Math.min(
                    sampleCount, lest.sampleCount));
        }
        throw new IllegalArgumentException(
                "LogGaussianEstimator: cannot add a "
                        + est.getClass().getName() + " estimator");
    }

    @Override
    public Estimator multiply(final double v) {
        final double lv = Math.log(v);
        return new LogGaussianEstimator(lv + logAverage, 2 * lv + logS,
                sampleCount);
    }

    @Override
    public String format() {
        return Utils.formatNumber(Math.exp(logAverage)) + "~"
                + Utils.formatNumber(Math.exp(0.5 * logS / sampleCount));
    }

    @Override
    public double getAverage() {
        return Math.exp(logAverage);
    }

}
