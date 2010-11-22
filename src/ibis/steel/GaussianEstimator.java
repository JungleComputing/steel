package ibis.steel;

import java.io.PrintStream;

/**
 * An estimator that assumes a Gaussian distribution for the estimated value.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public class GaussianEstimator implements Estimator {
	private double average = 0.0;
	private double S = 0.0;
	private int sampleCount = 0;

	public GaussianEstimator(final double average, final double variance) {
		this(average, variance, 1);
	}

	private GaussianEstimator(final double average, final double variance,
			final int sampleCount) {
		this.average = average;
		this.S = variance * sampleCount;
		this.sampleCount = sampleCount;
	}

	@Override
	public void addSample(final double value) {
		sampleCount++;
		final double oldAverage = average;
		average += (value - average) / sampleCount;
		S += (value - oldAverage) * (value - average);
	}

	private double getStdDev() {
		if (sampleCount < 2) {
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
	public void printStatistics(final PrintStream s, final String lbl) {
		final double stdDev = getStdDev();
		final double err = getLikelyError();
		if (lbl != null) {
			s.print(lbl);
			s.print(": ");
		}
		s.println("samples=" + sampleCount + " average=" + average + " stdDev="
				+ stdDev + " likely error=" + err);
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
}
