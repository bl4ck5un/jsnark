package examples.generators.babyjubjub;

import circuit.eval.CircuitEvaluator;
import circuit.operations.Gadget;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.babyjubjub.FixedBaseMulGadget;

import java.math.BigInteger;
import java.util.ArrayList;

public class BabyJubjubFixedBaseMulCircuitGenerator extends CircuitGenerator {
    // fixed base
    private final BigInteger x, y;
    private Wire s;

    public BabyJubjubFixedBaseMulCircuitGenerator(BigInteger x, BigInteger y, String circuitName) {
        super(circuitName);

        this.x = x;
        this.y = y;
    }

    public static void main(String[] args) throws Exception {
        // test vector taken from (Test 6)
        // https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
        BigInteger fixed_x = new BigInteger("5299619240641551281634865583518297030282874472190772894086521144482721001553");
        BigInteger fixed_y = new BigInteger("16950150798460657717958625567821834550301663161624707787222815936182638968203");
        BabyJubjubFixedBaseMulCircuitGenerator generator = new BabyJubjubFixedBaseMulCircuitGenerator(fixed_x, fixed_y, "babyjubjub_fixed_mul");

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

    @Override
    protected void buildCircuit() {
        s = createInputWire("s");

        Gadget jubjubGadget = new FixedBaseMulGadget(x, y, s, "babyjubjub-mul");
        Wire[] result = jubjubGadget.getOutputWires();
        makeOutputArray(result, "output of babyjubjub mul");
    }

    @Override
    public void generateSampleInput(CircuitEvaluator circuitEvaluator) {
        // test vector taken from (Test 6)
        // https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
        circuitEvaluator.setWireValue(s, new BigInteger("2736030358979909402780800718157159386076813972158567259200215660948447373041"));
    }
}
