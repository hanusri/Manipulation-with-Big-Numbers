/**
 * Class to store the sequence of expressions
 * 
 * @author G94
 */
public class Script {
    public final String[] lines; /* Array of expressions */
    public final int size; /* Number of lines in the script */

    public static final int MAX_LINE_COUNT = 1000;

    public Script(String[] lines, int size) {
	super();
	this.lines = lines;
	this.size = size;
    }
}