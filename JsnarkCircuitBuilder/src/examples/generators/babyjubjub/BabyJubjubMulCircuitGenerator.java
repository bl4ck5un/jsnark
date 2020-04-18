package examples.generators.babyjubjub;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.babyjubjub.ScalarMulGadget;

import java.math.BigInteger;
import java.util.ArrayList;

public class BabyJubjubMulCircuitGenerator extends CircuitGenerator {
    private Wire x;
    private Wire y;
    private Wire s;

    public BabyJubjubMulCircuitGenerator(String circuitName) {
        super(circuitName);
    }

    @Override
    protected void buildCircuit() {
        x = createInputWire("x");
        y = createInputWire("y");
        s = createInputWire("s");

        ScalarMulGadget jubjubGadget = new ScalarMulGadget(x, y, s, "babyjubjub-mul");
        Wire[] result = jubjubGadget.getOutputWires();
        makeOutputArray(result, "output of babyjubjub mul");
    }

    @Override
    public void generateSampleInput(CircuitEvaluator circuitEvaluator) {
        // test vector taken from (Test 6)
        // https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
        circuitEvaluator.setWireValue(x, new BigInteger("5299619240641551281634865583518297030282874472190772894086521144482721001553"));
        circuitEvaluator.setWireValue(y, new BigInteger("16950150798460657717958625567821834550301663161624707787222815936182638968203"));
        circuitEvaluator.setWireValue(s, new BigInteger("2736030358979909402780800718157159386076813972158567259200215660948447373041"));
    }

    public static void main(String[] args) throws Exception {
        BabyJubjubMulCircuitGenerator generator = new BabyJubjubMulCircuitGenerator("babyjubjub_mul");

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
        System.out.println(new BigInteger("0"));
        System.out.println(new BigInteger("1"));
    }
}
