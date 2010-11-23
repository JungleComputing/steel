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
        this.S = variance * sampleCount;
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

    private static double getLikelyValue(final double average,
            final double stdDev) {
        return average + stdDev * Globals.rng.nextGaussian();
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
        return getLikelyValue(average, err);
    }

    @Override
    public String getStatisticsString() {
        final double stdDev = getStdDev();
        final double err = getLikelyError();
        return "samples=" + sampleCount + " average=" + average + " stdDev="
                + stdDev + " likely error=" + err;
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
    public Estimator getEstimate() {
        return new GaussianEstimator(average, S / sampleCount);
    }

    @Override
    public Estimator addIndependent(Estimator est) {
        if (est instanceof ConstantEstimator) {
            final ConstantEstimator cest = (ConstantEstimator) est;
            return new GaussianEstimator(average + cest.getLikelyValue(), S,
                    sampleCount);
        } else if (est instanceof GaussianEstimator) {
            final GaussianEstimator gest = (GaussianEstimator) est;
            return new GaussianEstimator(average + gest.average, S + gest.S,
                    Math.min(sampleCount, gest.sampleCount));
        }
        throw new IllegalArgumentException("GaussianEstimator: cannot add a "
                + est.getClass().getName() + " estimator");
    }

    @Override
    public Estimator multiply(double c) {
        return new GaussianEstimator(c * average, c * c * S, sampleCount);
    }

    @Override
    public String format() {
        return String.format("%.3g�%.3g", average, getStdDev());
    }

    @Override
    public double getAverage() {
        return average;
    }
}
