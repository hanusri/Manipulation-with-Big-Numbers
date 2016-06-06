import java.io.InputStream;
import java.util.Scanner;

/**
 * Class to parse the input for Level 2
 * 
 * @author G94
 */
public class Parser {
    /*
     * Parse the input from the given stream into a String[] for future
     * execution. Every line is stored in the array in the position specified by
     * the line_no component of a single line, with direct one-to-one from
     * line_no to array index.
     */
    public static Script parse(InputStream src) {
	Scanner in = new Scanner(src);
	/* Ignore the first position for easy line number addressing */
	String[] lines = new String[Script.MAX_LINE_COUNT + 1];
	int size = 0;
	while (in.hasNext()) {
	    int line_no = in.nextInt();
	    lines[line_no] = in.next();
	    size++;
	}
	in.close();
	return new Script(lines, size);
    }
}