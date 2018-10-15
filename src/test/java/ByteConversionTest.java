import matwably.util.ByteConversion;
import org.junit.Test;
import static org.junit.Assert.*;
public class ByteConversionTest {

        @Test
        public void testingConstructors() {
            double a = 123123.121;
            ByteConversion s;
            s = new ByteConversion(a);
            assertTrue("Double should correctly be decoded to hex string",
                    s.toString().equals("\\2d\\b2\\9d\\ef\\31\\0f\\fe\\40"));
            float b = 513.512f;
            s = new ByteConversion(b);
            assertTrue("Float should correctly be decoded to hex string",
                    s.toString().equals("\\c5\\60\\00\\44"));
            char c = '\u2E85';
            s = new ByteConversion(c);
            assertTrue("Char should correctly be decoded to hex string",
                    s.toString().equals("\\85\\2e"));
            short d = 20222;
            s = new ByteConversion(d);
            assertTrue("Short should correctly be decoded to hex string",
                    s.toString().equals("\\fe\\4e"));
            int e = 1231221312;
            s = new ByteConversion(e);
            assertTrue("Int should correctly be decoded to hex string",
                    s.toString().equals("\\40\\f2\\62\\49"));
            a = -123123.121;
            s = new ByteConversion(a);
            assertTrue("Negative Double should correctly be decoded to hex string",
                    s.toString().equals("\\2d\\b2\\9d\\ef\\31\\0f\\fe\\c0"));
            b = -513.512f;
            s = new ByteConversion(b);
            assertTrue("Negative Float should correctly be decoded to hex string",
                    s.toString().equals("\\c5\\60\\00\\c4"));
            d = -20222;
            s = new ByteConversion(d);
            assertTrue("Negative Short should correctly be decoded to hex string",
                    s.toString().equals("\\02\\b1"));
            e = -1231221312;
            s = new ByteConversion(e);
            assertTrue("Negative Int should correctly be decoded to hex string",
                    s.toString().equals("\\c0\\0d\\9d\\b6"));

        }



}
