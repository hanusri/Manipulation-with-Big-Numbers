/**
 * Class to store and manipulate the sign of a BigNumber. Enum is used to
 * restrict the values to +1(+ve) and -1(-ve). Using +1 & -1 is easy when
 * calculating the sign of product, divide, etc.
 * 
 * @author G94
 */
public enum Sign {
    PLUS(1), MINUS(-1);

    public final int value;

    private Sign(int value) {
	this.value = value;
    }

    public static Sign from(long n) {
	if (n >= 0)
	    return PLUS;
	return MINUS;
    }

    public static Sign negate(Sign a) {
	if (a.value == 1)
	    /* -(1) = -1 */
	    return MINUS;
	/* -(-1) = 1 */
	return PLUS;
    }

    public static Sign product(Sign a, Sign b) {
	if (a.value * b.value == 1)
	    /*
	     * 1 * 1 = 1
	     * 
	     * -1 * -1 = 1
	     */
	    return Sign.PLUS;
	/*
	 * -1 * 1 = -1
	 * 
	 * 1 * -1 = -1
	 */
	return MINUS;
    }
}