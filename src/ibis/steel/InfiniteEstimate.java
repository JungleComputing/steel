package ibis.steel;

/**
 * An estimate that represents positive infinity.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public class InfiniteEstimate implements Estimate {
    private static final long serialVersionUID = 1L;

    /**
     * A constant INFINITE estimate.
     */
    public static final InfiniteEstimate INFINITE = new InfiniteEstimate();

    /**
     * Constructs an infinite estimate.
     */
    private InfiniteEstimate() {
        // Nothing
    }

    /**
     * Given an estimate <code>est</code> that has a distribution that is
     * independent of this estimate, return a new estimate that is the sum of
     * this estimate and <code>est</code>.
     */
    @Override
    public Estimate addIndependent(final Estimate est) {
        // Adding anything to an infinite estimate still yields infinite
        return this;
    }

    @Override
    public Estimate multiply(final double c) {
        // Multiplying an infinite estimate with anything still yields infinite.
        // Yeah, we're assuming c is not zero.
        return this;
    }

    @Override
    public double getLikelyValue() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getHighEstimate() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public String toString() {
        return "infinite";
    }
}
