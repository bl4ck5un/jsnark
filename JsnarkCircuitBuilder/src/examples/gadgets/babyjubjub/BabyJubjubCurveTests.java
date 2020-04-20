package examples.gadgets.babyjubjub;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class BabyJubjubCurveTests {
    @Test
    public void doubling() {
        // test vectors taken from
        // https://github.com/ethereum/EIPs/blob/41569d75e42da2046cb18fdca79609e18968af47/eip-draft_babyjubjub.md
        // x1 = 17777552123799933955779906779655732241715742912184938656739573121738514868268,
        // y1 = 2626589144620713026669568689430873010625803728049924121243784502389097019475
        BigInteger[] doubled = BabyJubjubCurve.doubleConstantPoint(
                new BigInteger("17777552123799933955779906779655732241715742912184938656739573121738514868268"),
                new BigInteger("2626589144620713026669568689430873010625803728049924121243784502389097019475"));

        Assert.assertEquals(doubled[0], new BigInteger("6890855772600357754907169075114257697580319025794532037257385534741338397365"));
        Assert.assertEquals(doubled[1], new BigInteger("4338620300185947561074059802482547481416142213883829469920100239455078257889"));
    }

    @Test
    public void doublingIdentity() {
        BigInteger[] doubled = BabyJubjubCurve.doubleConstantPoint(BigInteger.ZERO, BigInteger.ONE);
        Assert.assertEquals(doubled[0], BigInteger.ZERO);
        Assert.assertEquals(doubled[1], BigInteger.ONE);
    }
}
