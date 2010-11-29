package ibis.steel;

public class ConstantEstimate implements Estimate {
    private static final long serialVersionUID = 1L;

    private final double v;

    public static final ConstantEstimate ZERO = new ConstantEstimate(0.0);

    ConstantEstimate(final double v) {
        this.v = v;
    }

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

    @Override
    public double getAverage() {
        return v;
    }
}
