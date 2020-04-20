package examples.gadgets.babyjubjub;

import circuit.operations.Gadget;
import circuit.structure.Wire;

import java.math.BigInteger;

public class PedersenCommitmentGadget extends Gadget {
    private final Wire[] comm;

    /**
     * Computes a Pedersen commitment: m*G + r*H
     *
     * @param G          generator
     * @param H          a base
     * @param message    m
     * @param randomness r
     * @param desc       description
     */
    public PedersenCommitmentGadget(BigInteger[] G, BigInteger[] H, Wire message, Wire randomness, String... desc) {
        super(desc);

        // comm = h
        // comm = m*g + h*r
        Wire[] M = new FixedBaseMulGadget(G[0], G[1], message).getOutputWires();
        Wire[] R = new FixedBaseMulGadget(H[0], H[1], randomness).getOutputWires();
        this.comm = new AddGadget(M[0], M[1], R[0], R[1], "add").getOutputWires();
    }

    @Override
    public Wire[] getOutputWires() {
        return this.comm;
    }
}
