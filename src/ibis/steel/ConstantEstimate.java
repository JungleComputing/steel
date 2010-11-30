package ibis.steel;

/**
 * An estimate that is modeled by a constant value.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public class ConstantEstimate implements Estimate {
    private static final long serialVersionUID = 1L;

    /**
     * The value of the constant estimate.
     */
    final double v;

    /**
     * A constant zero estimate.
     */
    public static final ConstantEstimate ZERO = new ConstantEstimate(0.0);

    /**
     * Given a value <code>v</code>, construct a constant estimate with the
     * given value.
     * 
     * @param v
     *            The value of the constant estimate.
     */
    public ConstantEstimate(final double v) {
        this.v = v;
    }

    /**
     * Given an estimate <code>est</code> that has a distribution that is
     * independent of this estimate, return a new estimate that is the sum of
     * this estimate and <code>est</code>.
     */
    @Override
    public Estimate addIndependent(final Estimate est) {
        if (est == null) {
            return null;
        }
        if (est instanceof ConstantEstimate) {
            // Adding two constants creates another constant.
            final ConstantEstimate cest = (ConstantEstimate) est;
            return new ConstantEstimate(v + cest.v);
        }
        // Let the other one handle it.
        return est.addIndependent(this);
    }

    @Override
    public Estimate multiply(final double c) {
        return new ConstantEstimate(c * v);
    }

    @Override
    public double getLikelyValue() {
        return v;
    }

    @Override
    public double getHighEstimate() {
        return v;
    }

    @Override
    public String format() {
        return Utils.formatNumber(v);
    }
}
