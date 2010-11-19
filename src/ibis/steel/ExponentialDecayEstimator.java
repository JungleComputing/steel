package ibis.steel;

import java.io.PrintStream;

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
    private double average = 0.0;
    private double variance = 0.0;
    private final double alpha;
    private int sampleCount = 0;

    /**
     * Constructs a new exponential decay estimator with the given decay factor.
     * 
     * @param alpha
     *            The decay factor to use.
     */
    public ExponentialDecayEstimator(final double alpha) {
        this.alpha = alpha;
    }

    /**
     * Constructs a new exponential decay estimator with the decay factor 0.25.
     */
    public ExponentialDecayEstimator() {
        this(0.25);
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
        if (sampleCount < 2) {
            return Double.POSITIVE_INFINITY;
        }
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
    public void printStatistics(final PrintStream s, final String lbl) {
        final double stdDev = Math.sqrt(variance);
        final double err = getLikelyError();
        if (lbl != null) {
            s.print(lbl);
            s.print(": ");
        }
        s.format("samples=%d average=%.3g stdDev=%.3g likely error=%.3g\n",
                sampleCount, average, stdDev, err);
    }

    @Override
    public void setInitialEstimate(final Estimate v) {
        average = v.getAverage();
        variance = v.getVariance();
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
        return String.format(
                "samples=%d average=%.3g stdDev=%.3g likely error=%.3g",
                sampleCount, average, stdDev, err);
    }

    @Override
    public Estimate getEstimate() {
        return new Estimate(average, variance);
    }

    @Override
    public String getStatisticsString() {
        return toString();
    }
}
