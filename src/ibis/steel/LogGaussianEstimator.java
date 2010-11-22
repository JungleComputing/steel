package ibis.steel;

import java.io.PrintStream;

/**
 * An estimator that assumes a log Gaussian distribution for the estimated
 * value. This distribution is particularly suitable for time estimates, since
 * it never results in negative values, and makes very small values very
 * unlikely.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public class LogGaussianEstimator implements Estimator {
	GaussianEstimator logEstimator;

	@Override
	public void setInitialEstimate(final Estimator est) {
		final Estimator logEst = new Estimator(Math.log(est.getAverage()),
				Math.log(est.getVariance()));
		logEstimator.setInitialEstimate(logEst);
	}

	@Override
	public double getLikelyValue() {
		return Math.exp(logEstimator.getLikelyValue());
	}

	@Override
	public void printStatistics(final PrintStream s, final String lbl) {
		logEstimator.printStatistics(s, lbl + "LOG");
	}

	@Override
	public void addSample(final double v) {
		logEstimator.addSample(Math.log(v));
	}

	@Override
	public String getName() {
		return "log-gaussian";
	}

	@Override
	public double getHighEstimate() {
		return Math.exp(logEstimator.getHighEstimate());
	}

	@Override
	public int getSampleCount() {
		return logEstimator.getSampleCount();
	}

	@Override
	public Estimator getEstimate() {
		return Math.exp(logEstimator.getEstimate());
	}

	@Override
	public String getStatisticsString() {
		// TODO Auto-generated method stub
		return null;
	}

}
