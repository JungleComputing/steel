package ibis.steel;

/**
 * An estimator that assumes a Gaussian distribution for the estimated value.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public class GaussianEstimator implements Estimator {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private double mean = 0.0;
    private double S = 0.0;
    private int sampleCount = 0;

    private GaussianEstimator(final double mean, final double variance,
            final int sampleCount) {
        this.mean = mean;
        S = variance * sampleCount;
        this.sampleCount = sampleCount;
    }

    /**
     * Constructs a new estimator with the given initial mean and variance.
     * 
     * @param mean
     *            The initial mean of the estimator.
     * @param variance
     *            The initial variance of the estimator.
     */
    public GaussianEstimator(final double mean, final double variance) {
        this(mean, variance, 1);
    }

    @Override
    public void addSample(final double value) {
        sampleCount++;
        final double oldMean = mean;
        mean += (value - mean) / sampleCount;
        S += (value - oldMean) * (value - mean);
    }

    private double getStdDev() {
        return Math.sqrt(S / sampleCount);
    }

    // FIXME: this is just an intuitive approximation of a likely values comp.
    double getLikelyError() {
        final double stdDev = getStdDev();
        return stdDev + 0.1 * mean / Math.sqrt(sampleCount);
    }

    @Override
    public double getHighEstimate() {
        return mean + Math.sqrt(S / sampleCount);
    }

    @Override
    public double getLikelyValue() {
        final double err = getLikelyError();
        return mean + err * Globals.rng.nextGaussian();
    }

    @Override
    public String getStatisticsString() {
        final double stdDev = getStdDev();
        final double err = getLikelyError();
        return "mean=" + Utils.formatNumber(mean) + " stdDev="
                + Utils.formatNumber(stdDev) + " likely error="
                + Utils.formatNumber(err) + " samples=" + sampleCount;
    }

    @Override
    public String getName() {
        return "gaussian";
    }

    @Override
    public int getSampleCount() {
        return sampleCount;
    }

    @Override
    public Estimate getEstimate() {
        return new GaussianEstimate(mean, S / sampleCount, sampleCount);
    }

    @Override
    public String format() {
        return Utils.formatNumber(mean) + "\u00B1"
                + Utils.formatNumber(getStdDev());
    }

}
