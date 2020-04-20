package examples.generators.babyjubjub;

import circuit.eval.CircuitEvaluator;
import circuit.operations.Gadget;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.babyjubjub.AddConstantGadget;

import java.math.BigInteger;
import java.util.ArrayList;

public class BabyJubjubAddConstantCircuitGenerator extends CircuitGenerator {
    private final BigInteger fixed_x, fixed_y;
    private Wire x, y;

    public BabyJubjubAddConstantCircuitGenerator(BigInteger constant_x, BigInteger constant_y, String circuitName) {
        super(circuitName);
        this.fixed_x = constant_x;
        this.fixed_y = constant_y;
    }

    public static void main(String[] args) throws Exception {
        /*
         * test cases take from
         * https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
         */
        BigInteger fixed_x = new BigInteger("16540640123574156134436876038791482806971768689494387082833631921987005038935");
        BigInteger fixed_y = new BigInteger("20819045374670962167435360035096875258406992893633759881276124905556507972311");

        BabyJubjubAddConstantCircuitGenerator generator = new BabyJubjubAddConstantCircuitGenerator(fixed_x, fixed_y, "babyjubjub_fixed_add");

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

    @Override
    protected void buildCircuit() {
        this.x = createInputWire("x");
        this.y = createInputWire("y");

        Gadget addGadget = new AddConstantGadget(fixed_x, fixed_y, x, y, "add");
        Wire[] result = addGadget.getOutputWires();

        makeOutputArray(result, "output of BabyJubJub fixed addition");
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

        circuitEvaluator.setWireValue(x, new BigInteger("17777552123799933955779906779655732241715742912184938656739573121738514868268"));
        circuitEvaluator.setWireValue(y, new BigInteger("2626589144620713026669568689430873010625803728049924121243784502389097019475"));
    }
}
