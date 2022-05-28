package enigma;


/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Sharona Yang
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        int index = 0;
        _alphabet = alphabet;
        _cycles = new String[_alphabet.size()];
        char curr;
        if (cycles == "") {
            for (int i = 0; i < _cycles.length; i++) {
                _cycles[i] = Character.toString
                        (_alphabet.toChar(i));
            }
        } else {
            for (int i = 0; i < cycles.length(); i++) {
                curr = cycles.charAt(i);
                if (curr == ' ' || curr == '(') {
                    continue;
                } else if (curr == ')') {
                    index++;
                    continue;
                } else {
                    if (_cycles[index] == null) {
                        _cycles[index] = Character.toString(curr);
                    } else {
                        _cycles[index] += Character.toString(curr);
                    }
                }
            }
            boolean[] contains = new boolean[_alphabet.size()];
            boolean btemp = false;
            char achar;
            for (int i = 0; i < _alphabet.size(); i++) {
                achar = _alphabet.toChar(i);
                for (int j = 0; j < index; j++) {
                    for (int k = 0; k < _cycles[j].length(); k++) {
                        if (_cycles[j].charAt(k) == achar) {
                            btemp = true;
                        }
                    }
                }
                contains[i] = btemp;
            }

            for (int i = 0; i < _cycles.length; i++) {
                if (!contains[i]) {
                    _cycles[index] = Character.
                            toString(_alphabet.toChar(i));
                    index++;
                }
            }

        }

    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char target = _alphabet.toChar(wrap(p));
        char temp;
        int num;
        for (int i = 0; i < _cycles.length; i++) {
            if (_cycles[i] == null) {
                break;
            } else {
                for (int j = 0; j < _cycles[i].length(); j++) {
                    if (target == _cycles[i].charAt(j)) {
                        num = (j + 1) % _cycles[i].length();
                        temp = _cycles[i].charAt(num);
                        return _alphabet.toInt(temp);
                    }
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char target = _alphabet.toChar(wrap(c));
        char temp;
        int num;
        for (int i = 0; i < _cycles.length; i++) {
            if (_cycles[i] == null) {
                break;
            } else {
                for (int j = 0; j < _cycles[i].length(); j++) {
                    if (target == _cycles[i].charAt(j)) {
                        if (j <= 0) {
                            j += _cycles[i].length();
                            num = (j - 1) % _cycles[i].length();
                            temp = _cycles[i].charAt(num);
                            return _alphabet.toInt(temp);
                        } else {
                            num = (j - 1) % _cycles[i].length();
                            temp = _cycles[i].charAt(num);
                            return _alphabet.toInt(temp);
                        }
                    }
                }
            }

        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int len = 0;
        for (int i = 0; i < _cycles.length; i++) {
            len = len + _cycles[i].length();
        }
        if (len > _alphabet.size()) {
            return false;
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of the permutation. */
    private String[] _cycles;
}
