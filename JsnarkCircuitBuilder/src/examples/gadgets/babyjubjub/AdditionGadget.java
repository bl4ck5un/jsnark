package examples.gadgets.babyjubjub;

import circuit.config.Config;
import circuit.operations.Gadget;
import circuit.structure.Wire;
import examples.gadgets.math.FieldDivisionGadget;
import util.BigIntStorage;

import java.math.BigInteger;

/**
 * this implements the addition operation in BabyJubjub
 * https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
 *
 */
public class AdditionGadget extends Gadget {
    private static final BigInteger CURVE_FIELD_PRIME = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617", 16);
    private static final Integer CURVE_PARAM_A = 168700;
    private static final Integer CURVE_PARAM_D = 168696;

    /**
     * curve constants
     * a = 168700
     * d = 168696
     */
    private final Wire a;
    private final Wire d;

    /**
     * first point (x1, y1)
     * second point (x2, y2)
     */
    private final Wire x1, y1, x2, y2;

    /**
     * output of circuit (x, y)
     */
    private Wire[] output;

    public AdditionGadget(Wire x1, Wire y1, Wire x2, Wire y2, String... desc) {
        super(desc);

        // make sure jsnark uses the same field prime as Babyjubjub
        assert Config.FIELD_PRIME.equals(CURVE_FIELD_PRIME);

        // set the constants
        this.a = generator.createConstantWire(CURVE_PARAM_A);
        this.d = generator.createConstantWire(CURVE_PARAM_D);

        // connect the input
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        // build the circuit
        buildCircuit();
    }

    /**
     * Implements the addition
     * Specification here: https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
     *
     * x3 = (x1*y2 + y1*x2)/(1 + d*x1*x2*y1*y2)
     * y3 = (y1*y2 - a*x1*x2)/(1 - d*x1*x2*y1*y2)
     *
     */
    private void buildCircuit() {
        output = new Wire[2];

        Wire x1x2 = x1.mul(x2);
        Wire y1y2 = y1.mul(y2);

        Wire d_prod = this.d.mul(x1x2).mul(y1y2);

        Wire x1y2 = x1.mul(y2);
        Wire y1x2 = x2.mul(y1);

        // x3 = (x1*y2 + y1*x2)/(1 + d*x1*x2*y1*y2)
        Wire x3_num = x1y2.add(y1x2, "x3 num");
        Wire x3_dnum = generator.getOneWire().add(d_prod, "x3 denum");
        output[0] = new FieldDivisionGadget(x3_num, x3_dnum, "x3 div").getOutputWires()[0];

        // y3 = (y1*y2 - a*x1*x2)/(1 - d*x1*x2*y1*y2)
        Wire y3_num = y1y2.sub(this.a.mul(x1x2), "y3 num");
        Wire y3_denum = generator.getOneWire().sub(d_prod, "y3 denum");
        output[1] = new FieldDivisionGadget(y3_num, y3_denum, "y3 div").getOutputWires()[0];
    }

    @Override
    public Wire[] getOutputWires() {
        return output;
    }
}
