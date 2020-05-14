package examples.gadgets.babyjubjub;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PedersenCommitmentGadgetTests {
    private void test(BigInteger messageTestVector, BigInteger randomnessTestVector, BigInteger[] expectedComm, Boolean printInput) {
        CircuitGenerator generator = new CircuitGenerator("PedersenComm test") {
            private final int message_len = 253;
            private Wire[] message;
            private Wire[] randomness;

            @Override
            protected void buildCircuit() {
                message = createInputWireArray(message_len, "message");
                randomness = createInputWireArray(message_len, "randomness");
                makeOutputArray(new PedersenCommitmentGadget(message, randomness).getOutputWires(), "output of ped");
            }

            @Override
            public void generateSampleInput(CircuitEvaluator evaluator) {
                for (int i = 0; i < message_len; i++) {
                    evaluator.setWireValue(this.message[i], messageTestVector.testBit(i) ? 1 : 0);
                }

                for (int i = 0; i < message_len; i++) {
                    evaluator.setWireValue(this.randomness[i], randomnessTestVector.testBit(i) ? 1 : 0);
                }

                if (printInput) {
                    System.out.print("message: ");
                    for (Wire w : this.message) {
                        System.out.print(evaluator.getWireValue(w).toString());
                    }
                    System.out.println();

                    System.out.print("randomness: ");
                    for (Wire w : this.randomness) {
                        System.out.print(evaluator.getWireValue(w).toString());
                    }
                    System.out.println();
                }
            }
        };

        generator.generateCircuit();
        generator.evalCircuit();

        // print output
        CircuitEvaluator evaluator = generator.getCircuitEvaluator();
        ArrayList<Wire> outputWires = generator.getOutWires();

        Assert.assertEquals(evaluator.getWireValue(outputWires.get(0)), expectedComm[0]);
        Assert.assertEquals(evaluator.getWireValue(outputWires.get(1)), expectedComm[1]);
    }

    @Test
    public void testCase1() {
        // test vector taken from
        // https://github.com/barryWhiteHat/baby_jubjub_ecc/blob/master/tests/test_pedersen.py
        BigInteger messageTestVector = new BigInteger("123123123123312321321");
        BigInteger randomnessTestVector = new BigInteger("123123123123123123123123");
        BigInteger[] expectedComm = new BigInteger[]{
                new BigInteger("8010604480252997578874361183087746053332521656016812693508547791817401879458"),
                new BigInteger("15523586168823793714775329447481371860621135473088351041443641753333446779329")
        };

        this.test(messageTestVector, randomnessTestVector, expectedComm, false);
    }

    @Test
    public void stringInput() {
        String name = "SOLOMON KELL";
        BigInteger messageTestVector = new BigInteger(name.getBytes(StandardCharsets.US_ASCII));

        // zero randomenss
        BigInteger randomnessTestVector = new BigInteger("0");

        BigInteger[] expectedComm = new BigInteger[]{
                new BigInteger("947110531308419965235128766421137132610119646293628534350447913270460466436"),
                new BigInteger("12928725955550205948380890723711797325943086160976526689661767521899017506386")
        };

        this.test(messageTestVector, randomnessTestVector, expectedComm, true);
    }
}
