package ibis.steel;

import java.io.Serializable;

public class Estimate implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final Estimate ZERO = new Estimate(0, 0);
	private final double mean;
	private final double variance;

	public Estimate(final double mean, final double variance) {
		this.mean = mean;
		this.variance = variance;
	}

	public Estimate addIndependent(final Estimate b) {
		if (b == null) {
			return null;
		}
		return new Estimate(getMean() + b.getMean(), getVariance()
				+ b.getVariance());
	}

	public Estimate multiply(final double c) {
		return new Estimate(c * getMean(), c * c * getVariance());
	}

	public double getPessimisticEstimate() {
		return getMean() + Math.sqrt(getVariance());
	}

	public double getLikelyValue() {
		// TODO: for low sample count the variation should be larger.
		final double stdDev = Math.sqrt(getVariance());

		return getMean() + stdDev * Globals.rng.nextGaussian();
	}

	public double getMean() {
		return mean;
	}

	public double getVariance() {
		return variance;
	}
}
