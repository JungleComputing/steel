package ibis.steel;

import java.io.Serializable;

public class Estimate implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final Estimate ZERO = new Estimate(0, 0);
	final double mean;
	final double variance;

	public Estimate(final double mean, final double variance) {
		this.mean = mean;
		this.variance = variance;
	}

	public Estimate addIndependent(final Estimate b) {
		if (b == null) {
			return null;
		}
		return new Estimate(mean + b.mean, variance + b.variance);
	}

	public Estimate multiply(final double c) {
		return new Estimate(c * mean, c * c * variance);
	}

	double getPessimisticEstimate() {
		return mean + Math.sqrt(variance);
	}

	double getLikelyValue() {
		// TODO: for low sample count the variation should be larger.
		final double stdDev = Math.sqrt(variance);

		return mean + stdDev * Globals.rng.nextGaussian();
	}
}
