import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Class to implement addition and subtraction operations of BigNumbers
 * 
 * @author G94
 */
public class Sum {
    /* Add BigNumber 'a' to BigNumber 'b' */
    public static BigNumber add(BigNumber a, BigNumber b) {
	if (a == null || b == null)
	    return null;

	if (a.words == 0)
	    /* 0 + b = b */
	    return b.clone();
	if (b.words == 0)
	    /* a + 0 = a */
	    return a.clone();

	if (a.sign != b.sign)
	    /* a + (-b) = a - b */
	    return difference(a, b);

	return sum(a, b);
    }

    /* Subtract BigNumber 'b' from BigNumber 'a' */
    public static BigNumber subtract(BigNumber a, BigNumber b) {
	if (a == null || b == null)
	    return null;

	if (a.words == 0) {
	    /* 0 - b = -b */
	    BigNumber c = b.clone();
	    c.sign = Sign.negate(b.sign);
	    return c;
	}
	if (b.words == 0)
	    /* a - 0 = a */
	    return a.clone();

	BigNumber c;
	b.sign = Sign.negate(b.sign);
	if (a.sign == b.sign)
	    /* a - (-b) = a + b */
	    c = sum(a, b);
	else
	    /* a - (b) = a - b */
	    c = difference(a, b);
	b.sign = Sign.negate(b.sign);
	return c;
    }

    /*
     * Internal method to find the sum of BigNumber 'a' and BigNumber 'b',
     * irrespective of the signs
     */
    private static BigNumber sum(BigNumber a, BigNumber b) {
	long base = a.base;
	BigNumber c = new BigNumber(null, base);
	c.sign = a.sign;
	Iterator<Long> aIterator = a.wordIterator();
	Iterator<Long> bIterator = b.wordIterator();
	long carry = 0; /* carry from the previous word */
	while (aIterator.hasNext() && bIterator.hasNext()) {
	    /* cComponent: partial sum of the current words */
	    long cComponent = aIterator.next() + bIterator.next() + carry;
	    carry = cComponent / base;
	    c.addWord(cComponent % base);
	}

	Iterator<Long> rIterator = null;
	if (aIterator.hasNext())
	    /* words left in list a */
	    rIterator = aIterator;
	else if (bIterator.hasNext())
	    /* words left in list b */
	    rIterator = bIterator;
	if (rIterator != null) {
	    while (rIterator.hasNext() && carry != 0) {
		long cComponent = rIterator.next() + carry;
		carry = cComponent / base;
		c.addWord(cComponent % base);
	    }
	    /* no more carry to add forward */
	    while (rIterator.hasNext())
		c.addWord(rIterator.next());
	}

	/* if the carry is non zero, add that to the final result */
	if (carry != 0)
	    c.addWord(carry);
	return c;
    }

    /*
     * Internal method to find the difference of BigNumber 'a' and BigNumber
     * 'b', irrespective of the signs and also with the assumption that 'a' >
     * 'b' to avoid borrow look-ahead.
     */
    private static BigNumber difference(BigNumber a, BigNumber b) {
	if (a.words == b.words && a.getMSW() == b.getMSW())
	    /*
	     * Unable to find max of a & b in O(1) time, so use B's complement
	     * subtraction, O(3n), instead of Regular subtraction with
	     * look-ahead, O(4n)
	     */
	    return differenceByBC(a, b);
	if (a.words < b.words || (a.words == b.words && a.getMSW() < b.getMSW()))
	    /* clearly b is larger than a => a - b = -(b - a) */
	    return difference(b, a);

	long base = a.base;
	BigNumber c = new BigNumber(null, base);
	c.sign = a.sign;
	Iterator<Long> aIterator = a.wordIterator();
	Iterator<Long> bIterator = b.wordIterator();
	long borrow = 0; /* borrow by the previous word */
	/* a is atleast as large as b, so enough to iterate over b */
	while (bIterator.hasNext()) {
	    /* cComponent: partial difference of the current words */
	    long cComponent = aIterator.next() - bIterator.next() - borrow;
	    if (cComponent < 0) {
		borrow = 1;
		c.addWord(base + cComponent);
	    } else {
		borrow = 0;
		c.addWord(cComponent);
	    }
	}

	while (aIterator.hasNext() && borrow != 0) {
	    /* words left in a */
	    long cComponent = aIterator.next() - borrow;
	    if (cComponent < 0) {
		borrow = 1;
		c.addWord(base + cComponent);
	    } else {
		borrow = 0;
		c.addWord(cComponent);
	    }
	}
	while (aIterator.hasNext())
	    c.addWord(aIterator.next());

	return c;
    }

    /*
     * Internal method to find the B's Complement difference of BigNumber 'a'
     * and BigNumber 'b', irrespective of the signs. B's complement method is
     * costlier because of the extra loop to convert negative numbers from B's
     * complement to direct numbers. Hence, this method is used only when we do
     * not know which of the 2 numbers is the larger.
     */
    private static BigNumber differenceByBC(BigNumber a, BigNumber b) {
	long base = a.base;
	ListIterator<Long> aIterator = a.wordIterator();
	ListIterator<Long> bIterator = b.wordIterator();
	List<Long> c = new ArrayList<>(a.words);
	long carry = 0; /* carry from the previous word */
	while (bIterator.hasNext() && bIterator.next() == 0)
	    /* add a's current word to c as long as b's current word is zero */
	    c.add(aIterator.next());

	/*
	 * cComponent: partial sum of a's current word and B's complement of b's
	 * least significant non-zero word
	 */
	long cComponent = aIterator.next() + base - bIterator.previous() + carry;
	carry = cComponent / base;
	c.add(cComponent % base);
	bIterator.next();

	while (aIterator.hasNext()) {
	    /*
	     * cComponent: partial sum of a's current word and (B-1)'s
	     * complement of b's current word
	     */
	    cComponent = aIterator.next() + base - 1 - bIterator.next() + carry;
	    carry = cComponent / base;
	    c.add(cComponent % base);
	}

	/* Positive difference, a > b */
	Sign sign = a.sign;
	if (carry == 0) {
	    /*
	     * Negative difference, a < b. Convert from B's complement to
	     * positive.
	     */
	    int i = 0;
	    /* Iterate over least significant zeroes */
	    for (; i < c.size() && c.get(i) == 0; i++)
		;
	    if (i < c.size()) {
		/* B's complement of c's least significant non-zero word */
		c.set(i, base - c.get(i));
		for (i++; i < c.size(); i++)
		    /*
		     * (B-1)'s complement of c's least significant non-zero word
		     */
		    c.set(i, base - 1 - c.get(i));
		sign = b.sign;

	    } else {
		/* List c has only 0's */
		c = null;

	    }
	}
	return new BigNumber(c, base, sign);
    }
}