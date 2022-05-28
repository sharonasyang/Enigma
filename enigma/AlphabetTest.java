package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

public class AlphabetTest {

    @Test
    public void testConstructor() {
        Alphabet input1 = new Alphabet("ABCabc012");
    }

    @Test
    public void testSize() {
        Alphabet test = new Alphabet("ABCD");
        assertEquals(4, test.size());
    }

    @Test
    public void testContains() {
        Alphabet test = new Alphabet("ABCabc012");
        assertTrue(test.contains('A'));
        assertTrue(test.contains('a'));
        assertTrue(test.contains('0'));
        assertFalse(test.contains('Z'));
        assertFalse(test.contains('9'));
    }

    @Test
    public void testCharIndAt() {
        Alphabet test = new Alphabet("ABCabc012");
        assertEquals(0, test.toInt('A'));
        assertEquals('b', test.toChar(4));
        assertEquals('2', test.toChar(8));
        assertEquals(5, test.toInt('c'));
    }

}
