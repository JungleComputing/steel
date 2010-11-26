package ibis.steel;

class Utils {
    static String formatNumber(double v) {
        final boolean neg = v < 0;
        if (neg) {
            v = -v;
        }
        if (v < 1000 && v >= 1) {
            final String fmt = neg ? "-%.3f" : "%.3f";
            return String.format(fmt, v);
        }
        int power = (int) Math.log10(v);
        if (v < 1) {
            power -= 1;
        }
        int power3 = power - (power % 3);
        if (power3 > power) {
            power3 -= 3;
        }
        final double m = v / Math.pow(10.0, power3);
        final String fmt = neg ? "-%.2fE%d" : "%.2fE%d";
        final String res = String.format(fmt, m, power3);
        return res;
    }

    private static void testFormat(final double v) {
        System.out.println("v=" + v + " -> " + formatNumber(v));
    }

    public static void main(final String[] args) {
        testFormat(1.23e4);
        testFormat(1.23e3);
        testFormat(1.23e2);
        testFormat(1.23e1);
        testFormat(1.23e-0);
        testFormat(1.23e-1);
        testFormat(1.23e-2);
        testFormat(1.23e-3);
        testFormat(1.23e-4);
        testFormat(1.23e-5);
        testFormat(1.23e-6);
        testFormat(1.23e-7);
        testFormat(1.23e-8);
        testFormat(1.23e-9);
        testFormat(-1.23e-7);
        testFormat(1.23);
        testFormat(-1.23);
        testFormat(12.3);
        testFormat(12.3e7);
        testFormat(12.3e8);
        testFormat(12.3e9);
    }
}
