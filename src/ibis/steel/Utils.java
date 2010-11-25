package ibis.steel;

class Utils {
    static String formatNumber(double v) {
        final boolean neg = v < 0;
        if (neg) {
            v = -v;
        }
        if (v < 1000 && v > 1e-3) {
            final String fmt = neg ? "-%.3f" : "%.3f";
            return String.format(fmt, v);
        }
        final int power = (int) Math.round(Math.log10(v));
        int power3 = power - (power % 3);
        if (power3 > power) {
            power3 -= 3;
        }
        final double m = v / Math.pow(10.0, power3);
        final String fmt = neg ? "-%.3fe%d" : "%.3fe%d";
        final String res = String.format(fmt, m, power3);
        return res;
    }
}
