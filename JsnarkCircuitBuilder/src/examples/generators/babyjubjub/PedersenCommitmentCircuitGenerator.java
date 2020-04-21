package examples.generators.babyjubjub;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.babyjubjub.PedersenCommitmentGadget;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;

import java.math.BigInteger;
import java.util.ArrayList;

public class PedersenCommitmentCircuitGenerator extends CircuitGenerator {
    private Wire[] message;
    private Wire[] randomness;

    private final int INPUT_LENGTH = 253;

    public PedersenCommitmentCircuitGenerator(String circuitName) {
        super(circuitName);
    }

    public static void main(String[] args) throws Exception {
        CircuitGenerator generator = new PedersenCommitmentCircuitGenerator("babyjubjub_pedersen");

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

    @Override
    public void generateSampleInput(CircuitEvaluator circuitEvaluator) {
        // test vector taken from
        // https://github.com/barryWhiteHat/baby_jubjub_ecc/blob/master/tests/test_pedersen.py
        BigInteger messageTestVector = new BigInteger("123123123123312321321");
        System.out.println("Mesasge binary: " + messageTestVector.toString(2));
        for (int i = 0; i < INPUT_LENGTH; i++) {
            // testBit(0) is the least significant bit
            circuitEvaluator.setWireValue(this.message[i], messageTestVector.testBit(i) ? 1 : 0);
        }
        BigInteger randomnessTestVector = new BigInteger("123123123123123123123123");
//        BigInteger randomnessTestVector = new BigInteger("1");
        for (int i = 0; i < INPUT_LENGTH; i++) {
            circuitEvaluator.setWireValue(this.randomness[i], randomnessTestVector.testBit( i) ? 1 : 0);
        }
    }

    @Override
    protected void buildCircuit() {
        message = createInputWireArray(INPUT_LENGTH, "message");
        randomness = createInputWireArray(INPUT_LENGTH, "randomness");

        makeOutputArray(new PedersenCommitmentGadget(message, randomness).getOutputWires(), "output of ped");
    }
}
