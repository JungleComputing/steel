package ibis.steel;

import java.io.Serializable;

/**
 * The interface of an estimate.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public interface Estimate extends Serializable {
    /**
     * Given an estimate <code>est</code> that has a distribution that is
     * independent of this estimate, returns a new estimate that is the sum of
     * this estimate and <code>est</code>.
     * 
     * @param est
     *            The estimate that should be added to this one.
     * 
     * @return The new estimate.
     */
    Estimate addIndependent(Estimate est);

    /**
     * Given a constant value <code>v</code>, returns a new estimate that is the
     * product of <code>v</code> and this estimate.
     * 
     * @param v
     *            The constant to multiply with.
     * @return The new estimate, the product of this estimate and <code>v</code>
     *         . .
     */
    Estimate multiply(double v);

    /**
     * Returns a random but likely value for this estimate.
     * 
     * @return A likely value for this estimate.
     */
    double getLikelyValue();

    /**
     * Returns a constant value that is higher than most likely values of this
     * estimate.
     * 
     * @return The high estimate.
     */
    double getHighEstimate();
}
