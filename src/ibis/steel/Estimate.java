package ibis.steel;

import java.io.Serializable;

/**
 * The interface of an estimate.
 * 
 * @author Kees van Reeuwijk
 * 
 */
public interface Estimate extends Serializable {
    Estimate addIndependent(Estimate est);

    Estimate multiply(double v);

    String format();

    double getLikelyValue();

    double getAverage();

    double getHighEstimate();
}
