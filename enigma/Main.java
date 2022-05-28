package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Sharona Yang
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine mach = readConfig();
        String nextLine = _input.nextLine();
        if (!nextLine.contains("*")) {
            throw error("wrong configuration");
        }
        if (nextLine.startsWith("*")) {
            setUp(mach, nextLine);
        } else {
            nextLine = nextLine.replaceAll("\\s", "");
            printMessageLine(mach.convert(nextLine));
        }
        while (_input.hasNextLine()) {
            nextLine = _input.nextLine();
            if (nextLine.startsWith("*")) {
                setUp(mach, nextLine);
            } else {
                nextLine = nextLine.replaceAll("\\s", "");
                printMessageLine(mach.convert(nextLine));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphabet = _config.nextLine();
            _alphabetArr = new String[alphabet.length()];
            for (int i = 0; i < _alphabetArr.length; i++) {
                _alphabetArr[i] = Character.toString(alphabet.charAt(i));
            }

            if (alphabet.contains(" ")) {
                throw error("Missing alphabet");
            }
            _alphabet = new Alphabet(alphabet);
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            _pawls = pawls;
            ArrayList<Rotor> listRotors = new ArrayList<>();
            while (_config.hasNext()) {
                listRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, listRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotorName = _config.next();
            _rotorNames = _rotorNames + rotorName + " ";
            String currNotch = _config.next();
            char rotorType = currNotch.charAt(0);
            _allRotorTypes = _allRotorTypes + rotorType + " ";
            if (rotorType == 'R') {
                _rotorTypes += rotorName;
            }
            String rotorNotch = "";
            for (int i = 1; i < currNotch.length(); i++) {
                rotorNotch += currNotch.charAt(i);
            }
            String temp = "";
            String nextEle = _config.next();
            while (_config.hasNext("\\([^*()]*\\)")) {
                temp = temp.concat(nextEle + " ");
                nextEle = _config.next();
            }

            if (!_config.hasNext("\\([^*()]*\\)") && _config.hasNextLine()) {
                temp = temp.concat(nextEle + " ");
            }

            if (_config.hasNext("\\([^*()]*")) {
                throw error("incorrect configuration");
            }

            Permutation p = new Permutation(temp, _alphabet);
            if (rotorType == 'M') {
                return new MovingRotor(rotorName, p, rotorNotch);
            } else if (rotorType == 'N') {
                return new FixedRotor(rotorName, p);
            } else {
                return new Reflector(rotorName, p);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] tempSet = settings.split(" ");
        if (!_rotorTypes.contains(tempSet[1])) {
            throw error("Reflector in wrong place");
        }

        String[] settRotors = new String[M.numRotors()];
        for (int i = 0; i < settRotors.length; i++) {

            settRotors[i] = tempSet[i + 1];
        }
        for (int i = 0; i < settRotors.length; i++) {
            if (!_rotorNames.contains(settRotors[i])) {
                throw error("Bad rotor name");
            }
        }

        if (tempSet.length >= M.numRotors() + 2) {
            _ringSettings[0] = tempSet[tempSet.length - 2];
            _ringSettings[1] = tempSet[tempSet.length - 1];
        }

        if (tempSet.length == M.numRotors() + 2) {
            _ringSettings[0] = tempSet[tempSet.length - 1];
            String optSet = "";
            for (int i = 0; i < M.numRotors() - 1; i++) {
                optSet += "A";
            }
            _ringSettings[1] = optSet;
        }

        _rotorArr = _rotorNames.split(" ");
        _rotorTypesArr = _allRotorTypes.split(" ");

        for (int i = 0; i < settRotors.length; i++) {
            for (int j = 0; j < _rotorArr.length; j++) {
                if (settRotors[i].equals(_rotorArr[j])) {
                    if (_rotorTypesArr[j].equals("M")) {
                        _numPawls++;
                    }
                }
            }
        }

        if (_numPawls > _pawls) {
            throw error("Wrong number of arguments");
        }
        _numPawls = 0;

        M.insertRotors(settRotors);
        M.setRotors(tempSet[M.numRotors() + 1]);
        String cycles = "";
        for (int i = M.numRotors() + 2; i < tempSet.length; i++) {
            cycles = cycles.concat(tempSet[i]);
            cycles = cycles.concat(" ");
        }
        M.setPlugboard(new Permutation(cycles, _alphabet));
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            if (i % 5 == 0 && i != 0) {
                _output.print(" ");
            }
            _output.print(msg.charAt(i));
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** Rotor types stored in a string. */
    private String _rotorTypes = "";

    /** Rotor names stored in a string. */
    private String _rotorNames = "";

    /** True if the array contains the rotor. */
    private boolean _containsRotor = false;

    /** The total number of rotors. */
    private String _totalRotors = "";

    /** The actual number of pawls. */
    private int _numPawls = 0;

    /** The expected number of pawls. */
    private int _pawls;

    /** The different types of rotors. */
    private String _allRotorTypes = "";

    /** Rotors stored in an array. */
    private String[] _rotorArr;

    /** Rotor types stored in an array. */
    private String[] _rotorTypesArr;

    /** Settings of the rings. */
    private String[] _ringSettings = new String[2];

    private String[] _alphabetArr;
}
