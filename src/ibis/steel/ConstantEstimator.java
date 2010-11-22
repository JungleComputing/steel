package ibis.steel;

import java.io.PrintStream;

public class ConstantEstimator implements Estimator {
	private final double v;

	public ConstantEstimator(final double v) {
		this.v = v;
	}

	@Override
	public double getLikelyValue() {
		return v;
	}

	@Override
	public void printStatistics(final PrintStream s, final String lbl) {
		s.println(lbl + ": constant value " + v);
	}

	@Override
	public void addSample(final double v) {
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
	public Estimator addIndependent(
			final Estimator bestCompletionTimeAfterMasterQueue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Estimator multiply(final double i) {
		// TODO Auto-generated method stub
		return null;
	}

}
