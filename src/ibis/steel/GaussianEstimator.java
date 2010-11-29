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
    private double average = 0.0;
    private double S = 0.0;
    private int sampleCount = 0;

    private GaussianEstimator(final double average, final double variance,
            final int sampleCount) {
        this.average = average;
        S = variance * sampleCount;
        this.sampleCount = sampleCount;
    }

    public GaussianEstimator(final double average, final double variance) {
        this(average, variance, 1);
    }

    public GaussianEstimator() {
        this(0, 0, 0);
    }

    @Override
    public void addSample(final double value) {
        sampleCount++;
        final double oldAverage = average;
        average += (value - average) / sampleCount;
        S += (value - oldAverage) * (value - average);
    }

    private double getStdDev() {
        if (sampleCount < 1) {
            return Double.POSITIVE_INFINITY;
        }
        return Math.sqrt(S / sampleCount);
    }

    // FIXME: this is just an intuitive approximation of a likely values comp.
    double getLikelyError() {
        final double stdDev = getStdDev();
        return stdDev + 0.1 * average / Math.sqrt(sampleCount);
    }

    @Override
    public double getHighEstimate() {
        if (sampleCount < 1) {
            return Double.POSITIVE_INFINITY;
        }
        return average + Math.sqrt(S / sampleCount);
    }

    @Override
    public double getLikelyValue() {
        final double err = getLikelyError();
        return average + err * Globals.rng.nextGaussian();
    }

    @Override
    public String getStatisticsString() {
        final double stdDev = getStdDev();
        final double err = getLikelyError();
        return "samples=" + sampleCount + " average="
                + Utils.formatNumber(average) + " stdDev="
                + Utils.formatNumber(stdDev) + " likely error="
                + Utils.formatNumber(err);
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
        return new GaussianEstimate(average, S / sampleCount, sampleCount);
    }

    @Override
    public String format() {
        return Utils.formatNumber(average) + "\u00B1"
                + Utils.formatNumber(getStdDev());
    }

    @Override
    public double getAverage() {
        return average;
    }
}
