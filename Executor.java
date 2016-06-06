import java.util.HashMap;
import java.util.Map;

/**
 * Class to implement execute the list of expressions in a Script
 * 
 * @author G94
 */
public class Executor {
    public static final int MAX_VARIABLE_COUNT = 52;

    /*
     * Execute the given script line by line. Uses a HashMap to store the
     * variable values for easy retrieval.
     */
    public static void execute(Script s) {
	Map<Character, BigNumber> varMap = new HashMap<>(MAX_VARIABLE_COUNT);

	for (int line_no = 1; line_no <= s.size;)
	    line_no = execute(s.lines[line_no], line_no, varMap);
    }

    /*
     * Execute a given expression and store the assignments in the varMap. Also
     * the next line to execute.
     */
    private static int execute(String expression, int line_no, Map<Character, BigNumber> varMap) {
	char[] tokens = expression.toCharArray();
	int length = tokens.length;
	char varName = tokens[0];
	BigNumber var1 = varMap.get(varName);
	if (!expression.contains("=")) {
	    /* Not an assignment */
	    switch (length) {
	    case 1: /* var */
		System.out.println(var1);
		break;

	    case 2: /* var) */
		var1.printList();
		break;

	    default: /* var?notzero:zero */
		String[] line_nos = new String(tokens, 2, length - 2).split(":");
		if (var1.words != 0) /* nonzero */
		    return Integer.parseInt(line_nos[0]);
		if (line_nos.length == 2) /* zero */
		    return Integer.parseInt(line_nos[1]);
		break;

	    }
	} else {
	    /* Assignment */
	    BigNumber result = null;
	    if (expression.matches("[a-zA-Z]=[0-9]+")) {
		/* var=NumberInDecimal */
		result = new BigNumber(new String(tokens, 2, length - 2));

	    } else {
		/* Arithmetic operation */
		BigNumber var2 = varMap.get(tokens[2]);
		BigNumber var3 = null;
		if (length == 5) /* Binary operator */
		    var3 = varMap.get(tokens[4]);
		char operand = tokens[3];
		switch (operand) {
		case '+': /* var=var+var */
		    result = BigNumber.add(var2, var3);
		    break;

		case '-': /* var=var-var */
		    result = BigNumber.subtract(var2, var3);
		    break;

		case '*': /* var=var*var */
		    result = BigNumber.product(var2, var3);
		    break;

		case '^': /* var=var^var */
		    result = BigNumber.power(var2, var3);
		    break;

		case '/': /* var=var/var */
		    result = BigNumber.divide(var2, var3);
		    break;

		case '%': /* var=var%var */
		    result = BigNumber.modulo(var2, var3);
		    break;

		case '!': /* var=var! */
		    result = BigNumber.factorial(var2);
		    break;

		case '~': /* var=var~ */
		    result = BigNumber.squareRoot(var2);
		    break;

		}
	    }
	    varMap.put(varName, result);
	}
	return line_no + 1;
    }
}