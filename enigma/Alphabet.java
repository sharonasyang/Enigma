package enigma;
import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Sharona Yang
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _alphabet = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            currChar = chars.charAt(i);
            if (currChar == '*' || currChar == '(' || currChar == ')') {
                throw error("Invalid characters in input");
            } else {
                for (int j = 0; j < i; j++) {
                    if (currChar == _alphabet[j]) {
                        throw error("Repeated character in input");
                    }
                }
                _alphabet[i] = chars.charAt(i);
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.length;
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < _alphabet.length; i++) {
            if (_alphabet[i] == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index >= _alphabet.length) {
            throw error("Index out of bounds");
        }
        return _alphabet[index];
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int index = 0;
        for (int i = 0; i < _alphabet.length; i++) {
            if (_alphabet[i] == ch) {
                index = i;
            }
        }
        return index;
    }

    /** Alphabet array. */
    private char[] _alphabet;

    /** The current character. */
    private char currChar;
}
