package ibis.steel;

public class GaussianEstimate implements Estimate {
    private final double average;
    private final double variance;

    public GaussianEstimate(double average, double variance) {
        super();
        this.average = average;
        this.variance = variance;
    }

    @Override
    public Estimate addIndependent(final Estimate est) {
        if (est == null) {
            return null;
        }
        if (est instanceof ConstantEstimator) {
            final ConstantEstimator cest = (ConstantEstimator) est;
            return new GaussianEstimate(average + cest.getLikelyValue(),
                    variance);
        } else if (est instanceof GaussianEstimate) {
            final GaussianEstimate gest = (GaussianEstimate) est;
            return new GaussianEstimate(average + gest.average, variance
                    + gest.variance);
        }
        throw new IllegalArgumentException("GaussianEstimator: cannot add a "
                + est.getClass().getName() + " estimator");
    }

    @Override
    public Estimate multiply(final double c) {
        return new GaussianEstimate(c * average, c * c * variance);
    }

    @Override
    public String format() {
        return Utils.formatNumber(average) + "\u00B1"
                + Utils.formatNumber(Math.sqrt(variance));
    }

    @Override
    public double getAverage() {
        return average;
    }
}
