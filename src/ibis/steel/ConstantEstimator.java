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
    public Estimate getEstimate() {
        return new ConstantEstimate(v);
    }

    @Override
    public String getStatisticsString() {
        return "constant value " + Utils.formatNumber(v);
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
