package examples.gadgets.babyjubjub;

import circuit.config.Config;
import circuit.operations.Gadget;
import circuit.structure.Wire;
import examples.gadgets.math.FieldDivisionGadget;

import java.math.BigInteger;

/**
 * this implements the addition operation in BabyJubjub
 * with regard to a fixed based
 * <p>
 * Input P
 * Output P + P_fixed
 * <p>
 * where P_fixed is a constant point
 */
public class AddConstantGadget extends Gadget {
    /**
     * curve constants
     * a = 168700
     * d = 168696
     */
    private final Wire a;
    private final Wire d;

    // P_fixed
    private final Wire fixed_x, fixed_y;

    // input
    private final Wire x, y;

    /**
     * output of circuit (x, y)
     */
    private Wire[] output;

    public AddConstantGadget(BigInteger fixed_x, BigInteger fixed_y, Wire x, Wire y, String... desc) {
        super(desc);

        // make sure jsnark uses the same field prime as Babyjubjub
        assert Config.FIELD_PRIME.equals(BabyJubjubCurve.FIELD_PRIME);

        // set the constants
        this.a = generator.createConstantWire(BabyJubjubCurve.PARAM_A);
        this.d = generator.createConstantWire(BabyJubjubCurve.PARAM_D);

        this.fixed_x = generator.createConstantWire(fixed_x);
        this.fixed_y = generator.createConstantWire(fixed_y);

        // connect the input
        this.x = x;
        this.y = y;

        // build the circuit
        buildCircuit();
    }

    /**
     * Implements the addition
     * Specification here: https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
     * <p>
     * x3 = (x1*fixed_y + y1*fixed_x)/(1 + d*x1*fixed_x*y1*fixed_y)
     * y3 = (y1*fixed_y - a*x1*fixed_x)/(1 - d*x1*fixed_x*y1*fixed_y)
     */
    private void buildCircuit() {
        output = new Wire[2];

        Wire x1x2 = this.x.mul(this.fixed_x);
        Wire y1y2 = this.y.mul(this.fixed_y);

        Wire d_prod = this.d.mul(x1x2).mul(y1y2);

        Wire x1y2 = this.x.mul(this.fixed_y);
        Wire y1x2 = this.fixed_x.mul(this.y);

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
