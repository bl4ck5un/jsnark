package examples.gadgets.babyjubjub;

import circuit.operations.Gadget;
import circuit.structure.Wire;
import circuit.structure.WireArray;

import java.math.BigInteger;

/**
 * this implements a simple double-and-add scalar multiplication algorithm
 * with a fixed base
 * <p>
 * Input s (scalar)
 * output s*P (an EC point)
 */
public class FixedBaseMulGadget extends Gadget {
    // scalar (as a bit array)
    private final WireArray s;
    private final int bitwidth = 256;
    // P = (x, y)
    private BigInteger x, y;
    // output wires (x, y)
    private Wire[] output;

    public FixedBaseMulGadget(BigInteger x, BigInteger y, Wire s, String... desc) {
        super(desc);
        this.x = x;
        this.y = y;

        // store the scalar in binary form
        this.s = s.getBitWires(bitwidth, "split S as bits");

        buildCircuit();
    }

    private void buildCircuit() {
        output = new Wire[2];

        // initialized to the identity point (0, 1)
        Wire[] Q = new Wire[]{generator.getZeroWire(), generator.getOneWire()};

        // a simple double-and-add algorithm
        // https://en.wikipedia.org/wiki/Elliptic_curve_point_multiplication
        for (int i = 0; i < bitwidth; i++) {
            // add P(x,y) (the fixed base) and Q
            Wire[] add_result = new AddConstantGadget(x, y, Q[0], Q[1], "unconditional add").getOutputWires();

            // only update Q if s[i] == 1
            // namely Q = Q if s[i] == 0 else add_result
            // namely Q = s[i] * add_result + (~s[i])*Q
            Wire[] Q_left = new Wire[]{
                    s.get(i).mul(add_result[0], "Q left [0]"),
                    s.get(i).mul(add_result[1], "Q left [1]")
            };

            Wire neg_s = s.get(i).isEqualTo(0, "neg s");

            Wire[] Q_right = new Wire[]{
                    neg_s.mul(Q[0], "Q right [0]"),
                    neg_s.mul(Q[1], "Q right [1]"),
            };

            Q = new Wire[]{
                    Q_left[0].add(Q_right[0]),
                    Q_left[1].add(Q_right[1])
            };

            // always double N
            BigInteger[] doubled = BabyJubjubCurve.doubleConstantPoint(x, y);
            this.x = doubled[0];
            this.y = doubled[1];
        }

        output = Q;
    }


    @Override
    public Wire[] getOutputWires() {
        return output;
    }
}