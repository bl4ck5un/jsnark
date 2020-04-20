package examples.generators.babyjubjub;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.babyjubjub.PedersenCommitmentGadget;

import java.math.BigInteger;
import java.util.ArrayList;

public class PedersenCommitmentCircuitGenerator extends CircuitGenerator {
    private final BigInteger[] G, H;
    private Wire message;
    private Wire randomness;

    public PedersenCommitmentCircuitGenerator(BigInteger[] G, BigInteger[] H, String circuitName) {
        super(circuitName);
        this.G = G;
        this.H = H;
    }

    public static void main(String[] args) throws Exception {
        // base
        BigInteger[] G = new BigInteger[]{
                new BigInteger("17777552123799933955779906779655732241715742912184938656739573121738514868268"),
                new BigInteger("2626589144620713026669568689430873010625803728049924121243784502389097019475")};

        // another generator
        // taken from https://github.com/barryWhiteHat/baby_jubjub_ecc/blob/master/tests/test_pedersen.py
        BigInteger[] H = new BigInteger[]{
                new BigInteger("16540640123574156134436876038791482806971768689494387082833631921987005038935"),
                new BigInteger("20819045374670962167435360035096875258406992893633759881276124905556507972311")};

        CircuitGenerator generator = new PedersenCommitmentCircuitGenerator(G, H, "babyjubjub_pedersen");

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
        circuitEvaluator.setWireValue(message, new BigInteger("123123123123312321321"));
        circuitEvaluator.setWireValue(randomness, new BigInteger("123123123123123123123123"));
    }

    @Override
    protected void buildCircuit() {
        message = createInputWire("message");
        randomness = createInputWire("opening");

        makeOutputArray(new PedersenCommitmentGadget(G, H, message, randomness).getOutputWires(), "output of ped");
    }
}
