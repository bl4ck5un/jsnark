package examples.generators.babyjubjub;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.babyjubjub.PedersenCommitmentGadget;

import java.math.BigInteger;
import java.util.ArrayList;

public class PedersenCommitmentCircuitGenerator extends CircuitGenerator {
    private Wire message;
    private Wire randomness;

    public PedersenCommitmentCircuitGenerator(String circuitName) {
        super(circuitName);
    }

    @Override
    protected void buildCircuit() {
        message = createInputWire("message");
        randomness = createInputWire("opening");

        makeOutputArray(new PedersenCommitmentGadget(message, randomness).getOutputWires(), "output of ped");
    }

    @Override
    public void generateSampleInput(CircuitEvaluator circuitEvaluator) {
        // test vector taken from
        // https://github.com/barryWhiteHat/baby_jubjub_ecc/blob/master/tests/test_pedersen.py
        circuitEvaluator.setWireValue(message, new BigInteger("123123123123312321321"));
        circuitEvaluator.setWireValue(randomness, new BigInteger("123123123123123123123123"));
    }

    public static void main(String[] args) throws Exception {
        PedersenCommitmentCircuitGenerator generator = new PedersenCommitmentCircuitGenerator("babyjubjub_mul");

        generator.generateCircuit();
        // generator.printCircuit();
        generator.evalCircuit();
        generator.prepFiles();
        generator.runLibsnark();

        // print output
        CircuitEvaluator evaluator = generator.getCircuitEvaluator();
        ArrayList<Wire> outputWires = generator.getOutWires();

        System.out.println("********************************************************************************");
        for (Wire out : outputWires) {
            System.out.println(evaluator.getWireValue(out));
        }
        System.out.println("Expected: **********************************************************************");
        System.out.println(new BigInteger("8010604480252997578874361183087746053332521656016812693508547791817401879458"));
        System.out.println(new BigInteger("15523586168823793714775329447481371860621135473088351041443641753333446779329"));
    }
}
