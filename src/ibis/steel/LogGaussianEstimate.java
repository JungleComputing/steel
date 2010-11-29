package ibis.steel;

public class LogGaussianEstimate implements Estimate {
    private final double logAverage;
    private final double logVariance;

    LogGaussianEstimate(double average, double variance) {
        super();
        this.logAverage = average;
        this.logVariance = variance;
    }

    @Override
    public Estimate addIndependent(final Estimate est) {
        if (est == null) {
            return null;
        }
        if (est instanceof ConstantEstimate) {
            final ConstantEstimator cest = (ConstantEstimator) est;
            final double v = Math.exp(logAverage)
                    + Math.exp(cest.getLikelyValue());
            return new LogGaussianEstimate(Math.log(v), logVariance);
        } else if (est instanceof LogGaussianEstimate) {
            final LogGaussianEstimate lest = (LogGaussianEstimate) est;
            final double av = Math.exp(logAverage) + Math.exp(lest.logAverage);
            final double var = Math.exp(logVariance)
                    + Math.exp(lest.logVariance);
            return new LogGaussianEstimate(Math.log(av), Math.log(var));
        }
        throw new IllegalArgumentException("GaussianEstimator: cannot add a "
                + est.getClass().getName() + " estimator");
    }

    @Override
    public Estimate multiply(final double c) {
        final double lc = Math.log(c);
        return new LogGaussianEstimate(lc + logAverage, 2 * lc + logVariance);
    }

    @Override
    public String format() {
        return Utils.formatNumber(Math.exp(logAverage)) + "~"
                + Utils.formatNumber(Math.exp(0.5 * logVariance));
    }

    @Override
    public double getAverage() {
        return Math.exp(logAverage);
    }
}
