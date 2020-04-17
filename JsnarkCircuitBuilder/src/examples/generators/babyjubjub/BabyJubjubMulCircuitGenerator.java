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
        // test vector taken from
        // https://github.com/barryWhiteHat/baby_jubjub_ecc/blob/master/tests/test_mul.py
        circuitEvaluator.setWireValue(x, new BigInteger("17777552123799933955779906779655732241715742912184938656739573121738514868268"));
        circuitEvaluator.setWireValue(y, new BigInteger("2626589144620713026669568689430873010625803728049924121243784502389097019475"));
        circuitEvaluator.setWireValue(s, new BigInteger("14474011154664524427946373126085988481658748083205070504932198000989141204993"));
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
        System.out.println(new BigInteger("10164343493729775566033630529262184691025040849344456090975503683071363199598"));
        System.out.println(new BigInteger("1442322238505690604101225876236968359816131961581303072165222721153965839907"));
    }
}
