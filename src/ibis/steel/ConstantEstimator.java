package ibis.steel;

/**
 * An estimator that always estimates the same value. Mainly useful for
 * constants such as ZERO.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public class ConstantEstimator implements Estimator {
    private static final long serialVersionUID = 1L;
    /**
     * The constant estimate zero.
     */
    public static final ConstantEstimator ZERO = new ConstantEstimator(0.0);
    private final double v;

    ConstantEstimator(final double v) {
        this.v = v;
    }

    @Override
    public double getLikelyValue() {
        return v;
    }

    @Override
    public void addSample(final double s) {
        System.err.println("Adding a sample to a constant estimate is useless");
    }

    @Override
    public String getName() {
        return "constant";
    }

    @Override
    public double getHighEstimate() {
        return v;
    }

    @Override
    public int getSampleCount() {
        return 1;
    }

    @Override
    public Estimator getEstimate() {
        return new ConstantEstimator(v);
    }

    @Override
    public String getStatisticsString() {
        // TODO Auto-generated method stub
        return "constant value " + v;
    }

    @Override
    public Estimator addIndependent(final Estimator est) {
        if (est == null) {
            return null;
        }
        if (est instanceof ConstantEstimator) {
            // Adding two constants creates another constant.
            final ConstantEstimator cest = (ConstantEstimator) est;
            return new ConstantEstimator(v + cest.v);
        } else {
            // Let the other one handle it.
            return est.addIndependent(this);
        }
    }

    @Override
    public Estimator multiply(final double c) {
        // TODO Auto-generated method stub
        return new ConstantEstimator(c * v);
    }

    @Override
    public String format() {
        return String.format("%.3g", v);
    }

    @Override
    public double getAverage() {
        return v;
    }

}
