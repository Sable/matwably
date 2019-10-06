import matwably.util.ByteConversion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteConversionTest {

        @Test
        public void testingConstructors() {
            double a = 123123.121;
            ByteConversion s;
            s = new ByteConversion(a);

            assertEquals(
                    "\\2d\\b2\\9d\\ef\\31\\0f\\fe\\40",
                    s.toString(),
                    "Double should correctly be decoded to hex string");
            float b = 513.512f;
            s = new ByteConversion(b);
            assertEquals("\\c5\\60\\00\\44",
                    s.toString(),
                    "Float should correctly be decoded to hex string");
            char c = '\u2E85';
            s = new ByteConversion(c);
            assertEquals( "\\85\\2e", s.toString(),
                    "Char should correctly be decoded to hex string");
            short d = 20222;
            s = new ByteConversion(d);
            assertEquals( "\\fe\\4e", s.toString(),
                    "Short should correctly be decoded to hex string");
            int e = 1231221312;
            s = new ByteConversion(e);
            assertEquals( "\\40\\f2\\62\\49",
                    s.toString(),
                    "Int should correctly be decoded to hex string");
            a = -123123.121;
            s = new ByteConversion(a);
            assertEquals( s.toString(),"\\2d\\b2\\9d\\ef\\31\\0f\\fe\\c0",
                    "Negative Double should correctly be decoded to hex string");
            b = -513.512f;
            s = new ByteConversion(b);
            assertEquals("\\c5\\60\\00\\c4", s.toString(),
                    "Negative Float should correctly be decoded to hex string");
            d = -20222;
            s = new ByteConversion(d);
            assertEquals("\\02\\b1", s.toString(),
                    "Negative Short should correctly be decoded to hex string");
            e = -1231221312;
            s = new ByteConversion(e);
            assertEquals("\\c0\\0d\\9d\\b6", s.toString(),
                    "Negative Int should correctly be decoded to hex string");

        }



}
