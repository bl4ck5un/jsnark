package examples.gadgets.babyjubjub;

import circuit.operations.Gadget;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.math.FieldDivisionGadget;

import java.math.BigInteger;

public class PedersenCommitmentGadget extends Gadget {
    public static final BigInteger FIELD_PRIME = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617", 10);
    public static final BigInteger PARAM_A = BigInteger.valueOf(168700);
    public static final BigInteger PARAM_D = BigInteger.valueOf(168696);
    private final AffinePoint comm;
    // base
    private final AffinePoint basePoint = new AffinePoint(generator,
            new BigInteger("17777552123799933955779906779655732241715742912184938656739573121738514868268"),
            new BigInteger("2626589144620713026669568689430873010625803728049924121243784502389097019475"));
    // another generator
    // taken from https://github.com/barryWhiteHat/baby_jubjub_ecc/blob/master/tests/test_pedersen.py
    private final AffinePoint hPoint = new AffinePoint(generator,
            new BigInteger("16540640123574156134436876038791482806971768689494387082833631921987005038935"),
            new BigInteger("20819045374670962167435360035096875258406992893633759881276124905556507972311"));

    /**
     * Computes a Pedersen commitment: m*G + r*H
     *
     * @param message    m
     * @param randomness r
     * @param desc       description
     */
    public PedersenCommitmentGadget(Wire[] message, Wire[] randomness, String... desc) {
        super(desc);

        assert message.length == randomness.length;

        // pre-compute
        AffinePoint[] baseTable = preprocess(basePoint, message.length);
        AffinePoint[] hTable = preprocess(hPoint, randomness.length);

        // comm = m*g + h*r
        AffinePoint M = this.mul(message, baseTable);
        AffinePoint R = this.mul(randomness, hTable);

        this.comm = this.addAffinePoints(M, R);
//        this.comm = M;
    }

    /**
     * Implements the addition
     * Specification here: https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
     *
     * @param p1 Point 1, in the form of (x1, y1)
     * @param p2 Point 2, in the form of (x2, y2)
     * @return p in the form of (x3, y3) such that
     * x3 = (x1*y2 + y1*x2)/(1 + d*x1*x2*y1*y2)
     * y3 = (y1*y2 - a*x1*x2)/(1 - d*x1*x2*y1*y2)
     */
    public AffinePoint addAffinePoints(AffinePoint p1, AffinePoint p2) {
        Wire x1x2 = p1.x.mul(p2.x);
        Wire y1y2 = p1.y.mul(p2.y);

        Wire d_prod = x1x2.mul(y1y2).mul(PARAM_D);

        Wire x1y2 = p1.x.mul(p2.y);
        Wire y1x2 = p2.x.mul(p1.y);

        // x3 = (x1*y2 + y1*x2)/(1 + d*x1*x2*y1*y2)
        Wire x3_num = x1y2.add(y1x2, "x3 num");
        Wire x3_dnum = generator.getOneWire().add(d_prod, "x3 denum");
        Wire x3 = new FieldDivisionGadget(x3_num, x3_dnum, "x3 div").getOutputWires()[0];

        // y3 = (y1*y2 - a*x1*x2)/(1 - d*x1*x2*y1*y2)
        Wire y3_num = y1y2.sub(x1x2.mul(PARAM_A), "y3 num");
        Wire y3_denum = generator.getOneWire().sub(d_prod, "y3 denum");
        Wire y3 = new FieldDivisionGadget(y3_num, y3_denum, "y3 div").getOutputWires()[0];

        return new AffinePoint(x3, y3);
    }

    public AffinePoint doubleAffinePoint(AffinePoint p) {
        /*
         * Implements the addition
         * Specification here: https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
         *
         * x = 2xy/(1 + d*x*x*y*y)
         * y = (y*y - a*x*x)/(1 - d*x*x*y*y)
         *
         */
        Wire xy = p.x.mul(p.y);
        Wire xx = p.x.mul(p.x);
        Wire yy = p.y.mul(p.y);
        Wire dxxyy = xx.mul(yy).mul(PARAM_D);

        // x = 2xy / (1 + d*x^2*y^2)
        Wire x = new FieldDivisionGadget(
                xy.mul(2),
                generator.getOneWire().add(dxxyy)).getOutputWires()[0];

        // y = (y^2 - a*x^2)/(1 - dx^2 y^2)
        Wire y = new FieldDivisionGadget(
                yy.sub(xx.mul(PARAM_A)),
                generator.getOneWire().sub(dxxyy)).getOutputWires()[0];

        return new AffinePoint(x, y);
    }

    private AffinePoint[] preprocess(AffinePoint p, int length) {
        AffinePoint[] precomputedTable = new AffinePoint[length];
        precomputedTable[0] = p;
        for (int j = 1; j < length; j++) {
            precomputedTable[j] = doubleAffinePoint(precomputedTable[j - 1]);
        }
        return precomputedTable;
    }

    /**
     * Windowed method for multiplication
     *
     * @param scalar           bit array. scalar[0] is the least significant bit
     * @param precomputedTable holding 2^i*P for i \in [0, |scalar|]
     * @return scalar * P where P is defined by precomputedTable
     */
    private AffinePoint mul(Wire[] scalar, AffinePoint[] precomputedTable) {
        AffinePoint result = new AffinePoint(generator.getZeroWire(), generator.getOneWire());
        for (int j = 0; j < scalar.length; j++) {
            AffinePoint tmp = addAffinePoints(result, precomputedTable[j]);
            Wire isOne = scalar[j];
            // if isOne == 0, result.x = result.x
            // else: result.x = tmp.x
            result.x = result.x.add(isOne.mul(tmp.x.sub(result.x)));
            result.y = result.y.add(isOne.mul(tmp.y.sub(result.y)));
        }
        return result;
    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{this.comm.x, this.comm.y};
    }

    public static class AffinePoint {
        public Wire x;
        public Wire y;

        AffinePoint(Wire x, Wire y) {
            this.x = x;
            this.y = y;
        }

        AffinePoint(CircuitGenerator generator, BigInteger x, BigInteger y) {
            this.x = generator.createConstantWire(x);
            this.y = generator.createConstantWire(y);
        }

        AffinePoint(AffinePoint p) {
            this.x = p.x;
            this.y = p.y;
        }
    }
}
