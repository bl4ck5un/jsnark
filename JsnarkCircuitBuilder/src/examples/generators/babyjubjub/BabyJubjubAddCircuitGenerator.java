package examples.generators.babyjubjub;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.babyjubjub.AdditionGadget;

import java.math.BigInteger;
import java.util.ArrayList;

public class BabyJubjubAddCircuitGenerator extends CircuitGenerator {
    private Wire x1, x2, y1, y2;

    public BabyJubjubAddCircuitGenerator(String circuitName) {
        super(circuitName);
    }

    @Override
    protected void buildCircuit() {
        this.x1 = createInputWire("x1");
        this.y1 = createInputWire("x2");
        this.x2 = createInputWire("y1");
        this.y2 = createInputWire("y2");

        AdditionGadget addGadget = new AdditionGadget(x1, y1, x2, y2);
        Wire[] result = addGadget.getOutputWires();

        makeOutputArray(result, "output of BabyJubJub addition");
    }

    @Override
    public void generateSampleInput(CircuitEvaluator circuitEvaluator) {
        /*
         * test cases take from
         * https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
         *
         * x1 = 17777552123799933955779906779655732241715742912184938656739573121738514868268
         * y1 = 2626589144620713026669568689430873010625803728049924121243784502389097019475
         *
         * x2 = 16540640123574156134436876038791482806971768689494387082833631921987005038935
         * y2 = 20819045374670962167435360035096875258406992893633759881276124905556507972311
         */

        circuitEvaluator.setWireValue(x1, new BigInteger("17777552123799933955779906779655732241715742912184938656739573121738514868268"));
        circuitEvaluator.setWireValue(y1, new BigInteger("2626589144620713026669568689430873010625803728049924121243784502389097019475"));
        circuitEvaluator.setWireValue(x2, new BigInteger("16540640123574156134436876038791482806971768689494387082833631921987005038935"));
        circuitEvaluator.setWireValue(y2, new BigInteger("20819045374670962167435360035096875258406992893633759881276124905556507972311"));

//        (0, 1) is identity
//        circuitEvaluator.setWireValue(x2, 0);
//        circuitEvaluator.setWireValue(y2, 1);
    }

    public static void main(String[] args) throws Exception {
        BabyJubjubAddCircuitGenerator generator = new BabyJubjubAddCircuitGenerator("babyjubjub addition");

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
        System.out.println(new BigInteger("7916061937171219682591368294088513039687205273691143098332585753343424131937"));
        System.out.println(new BigInteger("14035240266687799601661095864649209771790948434046947201833777492504781204499"));
    }
}
