/**
 * Driver program for LP1 level 2
 * 
 * @author rbk
 */

public class LP1Driver {
    public static void main(String[] args) {
	if (args != null && args.length > 0)
	    BigNumber.DEFAULT_BASE = Long.parseLong(args[0]);

	Script script = Parser.parse(System.in);
	Executor.execute(script);
    }
}