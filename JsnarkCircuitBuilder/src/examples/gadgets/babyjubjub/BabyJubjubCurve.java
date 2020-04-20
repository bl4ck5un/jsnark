package examples.gadgets.babyjubjub;

import java.math.BigInteger;

public class BabyJubjubCurve {
    public static final BigInteger FIELD_PRIME = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617", 10);
    public static final BigInteger PARAM_A = BigInteger.valueOf(168700);
    public static final BigInteger PARAM_D = BigInteger.valueOf(168696);

    /**
     * @param x
     * @param y
     * @return 2*(x, y)
     */
    public static BigInteger[] doubleConstantPoint(BigInteger x, BigInteger y) {
        /*
         * Implements the addition
         * Specification here: https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
         *
         * x3 = (x*y + y*x)/(1 + d*x*x*y*y)
         * y3 = (y*y - a*x*x)/(1 - d*x*x*y*y)
         *
         */
        BigInteger xy = x.multiply(y).mod(BabyJubjubCurve.FIELD_PRIME);
        BigInteger xx = x.multiply(x).mod(BabyJubjubCurve.FIELD_PRIME);
        BigInteger yy = y.multiply(y).mod(BabyJubjubCurve.FIELD_PRIME);

        BigInteger dxxyy = xx.multiply(yy).multiply(BabyJubjubCurve.PARAM_D).mod(BabyJubjubCurve.FIELD_PRIME);

        BigInteger x3_denum = BigInteger.ONE.add(dxxyy).modInverse(BabyJubjubCurve.FIELD_PRIME);
        BigInteger x3 = BigInteger.valueOf(2).multiply(xy).multiply(x3_denum).mod(BabyJubjubCurve.FIELD_PRIME);

        BigInteger y3_denum = BigInteger.ONE.subtract(dxxyy).modInverse(BabyJubjubCurve.FIELD_PRIME);
        BigInteger y3 = yy.subtract(PARAM_A.multiply(xx)).multiply(y3_denum).mod(BabyJubjubCurve.FIELD_PRIME);

        return new BigInteger[]{x3, y3};
    }
}
