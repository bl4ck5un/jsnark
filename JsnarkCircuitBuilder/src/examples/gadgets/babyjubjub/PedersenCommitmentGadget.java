package examples.gadgets.babyjubjub;

import circuit.operations.Gadget;
import circuit.structure.Wire;

import java.math.BigInteger;

public class PedersenCommitmentGadget extends Gadget {
    // base
    private static final BigInteger[] G = new BigInteger[]{
            new BigInteger("17777552123799933955779906779655732241715742912184938656739573121738514868268"),
            new BigInteger("2626589144620713026669568689430873010625803728049924121243784502389097019475")};

    // another generator
    // taken from https://github.com/barryWhiteHat/baby_jubjub_ecc/blob/master/tests/test_pedersen.py
    private static final BigInteger[] H = new BigInteger[]{
            new BigInteger("16540640123574156134436876038791482806971768689494387082833631921987005038935"),
            new BigInteger("20819045374670962167435360035096875258406992893633759881276124905556507972311")};

    private final Wire[] comm;

    public PedersenCommitmentGadget(Wire message, Wire randomness, String... desc) {
        super(desc);

        // comm = h
        // comm = m*g + h*r
        Wire[] M = new ScalarMulGadget(
                this.generator.createConstantWire(G[0]),
                this.generator.createConstantWire(G[1]),
                message).getOutputWires();

        Wire[] R = new ScalarMulGadget(
                this.generator.createConstantWire(H[0]),
                this.generator.createConstantWire(H[1]),
                randomness).getOutputWires();

        this.comm = new AdditionGadget(M[0], M[1], R[0], R[1], "add").getOutputWires();
    }

    @Override
    public Wire[] getOutputWires() {
        return this.comm;
    }
}
