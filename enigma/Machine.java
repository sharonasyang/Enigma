package enigma;

import java.util.Collection;

/** Class that represents a complete enigma machine.
 *  @author Sharona Yang
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[_numRotors];
        _hasTurned = new boolean[_numRotors];
        _willTurnArr = new boolean[_numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotors[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        String temp;
        Object[] possRotors = _allRotors.toArray();
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < possRotors.length; j++) {
                temp = ((Rotor) possRotors[j]).name();
                if (rotors[i].equals(temp)) {
                    _rotors[i] = (Rotor) possRotors[j];
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < _rotors.length; i++) {
            _rotors[i].set(setting.charAt(i - 1));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        for (int i = _numRotors - 1; i >= _numRotors - _pawls; i--) {
            if (_rotors[i].atNotch()) {
                _willTurnArr[i] = true;
            } else {
                _willTurnArr[i] = false;
            }
            _hasTurned[i] = false;
        }

        for (int i = _numRotors - 1; i > _numRotors - _pawls; i--) {
            if (_willTurnArr[i]) {
                if (_rotors[i - 1].rotates() && !_hasTurned[i - 1]) {
                    _rotors[i - 1].advance();
                    _hasTurned[i - 1] = true;
                    if ((i != (_numRotors - 1)) && !_hasTurned[i]) {
                        _rotors[i].advance();
                        _hasTurned[i] = true;
                    }
                }
            }
        }

        if (!_hasTurned[_numRotors - 1]) {
            _rotors[_numRotors - 1].advance();
            _hasTurned[_numRotors - 1] = true;
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int temp = c;
        for (int i = _numRotors - 1; i >= 0; i--) {
            temp = _rotors[i].convertForward(temp);
        }
        for (int i = 1; i < _numRotors; i++) {
            temp = _rotors[i].convertBackward(temp);
        }
        return temp;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String message = "";
        int temp = 0;
        for (int i = 0; i < msg.length(); i += 1) {
            temp = convert(_alphabet.toInt(msg.charAt(i)));
            message += _alphabet.toChar(temp);
        }
        return message;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** The number of rotors. */
    private int _numRotors;

    /** The number of pawls. */
    private int _pawls;

    /** The collection of all the rotors. */
    private Collection<Rotor> _allRotors;

    /** Array of the rotors. */
    private Rotor[] _rotors;

    /** The plugboard's settings. */
    private Permutation _plugboard;

    /** Checks if the rotor has already turned. */
    private boolean[] _hasTurned;

    /** Checks if the rotor will turn. */
    private boolean[] _willTurnArr;
}
