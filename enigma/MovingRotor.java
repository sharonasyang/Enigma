package enigma;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Sharona Yang
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            if (setting() == alphabet().toInt(_notches.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set((setting() + 1) % alphabet().size());
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    String notches() {
        return _notches;
    }

    /** The notches of the rotors. */
    private String _notches;
}
