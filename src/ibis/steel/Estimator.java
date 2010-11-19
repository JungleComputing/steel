package ibis.steel;

import java.io.PrintStream;

/**
 * The interface of an estimator.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public interface Estimator {

    /**
     * Force an initial estimate on the estimator.
     * 
     * @param timeEstimate
     *            The new initial time estimate.
     */
    void setInitialEstimate(Estimate timeEstimate);

    /**
     * Based on the current data, return a likely value for a next sample. The
     * returned value may be random, so don't assume that for subsequent calls
     * the same value is returned, even if no new samples have been added.
     * 
     * @return A likely value.
     */
    double getLikelyValue();

    /**
     * Given a print stream and a label, print statistics of this estimator to
     * the given stream.
     * 
     * @param s
     *            The PrintStream to print to.
     * @param lbl
     *            The label to add to the output.
     */
    void printStatistics(PrintStream s, String lbl);

    /**
     * Adds a new sample to the estimate.
     * 
     * @param v
     *            The sample to add.
     */
    void addSample(double v);

    /**
     * Returns the name of this estimator.
     * 
     * @return The name of this estimator.
     */
    String getName();

    /**
     * Returns an estimate that is likely to be too high.
     * 
     * @return The estimate.
     */
    double getHighEstimate();

    /**
     * Returns the sample count of this estimator.
     * 
     * @return The sample count.
     */
    int getSampleCount();

    /**
     * Returns an estimate representing the current estimated value, and the
     * variance on the estimate. Contrary to the estimator itself, the estimate
     * cannot be updated with new samples, but it is suitable for communication.
     * 
     * @return The estimate.
     */
    Estimate getEstimate();

    /**
     * Returns a string with some statistics of this estimator.
     * 
     * @return The statistics string.
     */
    String getStatisticsString();

}