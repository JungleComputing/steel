package ibis.steel;

import java.io.Serializable;

/**
 * @author Kees van Reeuwijk
 * 
 */
public class Estimate implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * A constant representing zero average and variance.
     */
    public static final Estimate ZERO = new Estimate(0, 0);
    private final double average;
    private final double variance;

    /**
     * Constructs a new estimate with the given average and variance.
     * 
     * @param mean
     *            The average of the new estimate.
     * @param variance
     *            The variance of the new estimate.
     */
    public Estimate(final double mean, final double variance) {
        this.average = mean;
        this.variance = variance;
    }

    /**
     * Given another estimated value, that should be statistically independent
     * of this one, returns a new estimate that represents the sum of this
     * estimate and the other estimate.
     * 
     * @param other
     *            The other estimated value.
     * @return The sum of the two estimates.
     */
    public Estimate addIndependent(final Estimate other) {
        if (other == null) {
            return null;
        }
        return new Estimate(getAverage() + other.getAverage(), getVariance()
                + other.getVariance());
    }

    /**
     * Given a constant value, returns a new estimate that is multiplied by this
     * constant.
     * 
     * @param c
     *            The constant to multiply with.
     * @return The new, multiplied, constant.
     */
    public Estimate multiply(final double c) {
        return new Estimate(c * getAverage(), c * c * getVariance());
    }

    /**
     * Returns an estimate value that is likely to be too high.
     * 
     * @return The estimate value.
     */
    public double getHighEstimate() {
        return average + Math.sqrt(getVariance());
    }

    /**
     * Returns a random likely value for this estimate.
     * 
     * @return The likely value.
     */
    public double getLikelyValue() {
        // TODO: for low sample count the variation should be larger.
        final double stdDev = Math.sqrt(getVariance());

        return average + stdDev * Globals.rng.nextGaussian();
    }

    /**
     * Returns the average of this estimate.
     * 
     * @return The average of this estimate.
     */
    public double getAverage() {
        return average;
    }

    /**
     * Returns the variance of this estimate.
     * 
     * @return The variance of this estimate.
     */
    public double getVariance() {
        return variance;
    }
}
