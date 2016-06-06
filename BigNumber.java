import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Class to implement discrete arithmetics for very large numbers
 * 
 * @author G94
 */
public class BigNumber {
    private List<Long> number;
    public final long base;
    public Sign sign;
    public int words;

    public static long DEFAULT_BASE = (long) Math.sqrt(Long.MAX_VALUE);

    BigNumber() {
	this(null, DEFAULT_BASE, Sign.PLUS);
    }

    BigNumber(List<Long> number) {
	this(number, DEFAULT_BASE, Sign.PLUS);
    }

    BigNumber(long number) {
	this(number, DEFAULT_BASE, Sign.PLUS);
    }

    BigNumber(List<Long> number, long base) {
	this(number, base, Sign.PLUS);
    }

    BigNumber(long number, long base) {
	this(number, base, Sign.PLUS);
    }

    BigNumber(List<Long> number, long base, Sign sign) {
	this.number = new ArrayList<>();
	if (number != null)
	    addWords(number);
	this.base = base;
	this.sign = sign;
    }

    BigNumber(long number, long base, Sign sign) {
	this.number = new ArrayList<>();
	while (number >= base) {
	    addWord(number % base);
	    number /= base;
	}
	addWord(number);
	this.base = base;
	this.sign = sign;
    }

    BigNumber(String s) {
	BigNumber nBaseB = new BigNumber();
	BigNumber tenBaseB = new BigNumber(10);
	for (Character c : s.toCharArray()) {
	    /*
	     * n = s[0]*base^n + s[1]*base^n-1 + ... + s[n-1]*base^1 + s[n]
	     * 
	     * n = (s[0]*base^n-1 + s[1]*base^n-2 + ... + s[n-1]) * base + s[n]
	     */
	    int digit = Character.getNumericValue(c);
	    nBaseB = add(product(nBaseB, tenBaseB), new BigNumber(digit));
	}
	this.number = nBaseB.number;
	this.words = nBaseB.words;
	this.base = DEFAULT_BASE;
	this.sign = Sign.PLUS;
    }

    /* Size of the underlying list */
    public int size() {
	return number.size();
    }

    /* Add a word(digit) to the end of the list */
    public void addWord(long value) {
	number.add(value);
	if (value != 0)
	    words = number.size();
    }

    /* Add a list of words(digits) to the end of the list */
    public void addWords(List<Long> values) {
	for (Long value : values)
	    addWord(value);
    }

    /* Add zeros to the least significant side of the number */
    public void addLSZeros(int count) {
	words += count;
	while (count > 0) {
	    number.add(0, 0L);
	    count--;
	}
    }

    /* Add zeros to the most significant side of the number */
    public void addMSZeros(int count) {
	while (count > 0) {
	    number.add(0L);
	    count--;
	}
    }

    /* Return the least significant word */
    public long getLSW() {
	return number.get(0);
    }

    /* Return the most significant word */
    public long getMSW() {
	return number.get(words - 1);
    }

    /* Compare this number with the given number 'b' */
    public int compare(BigNumber b) {
	if (this.words > b.words)
	    return 1;
	if (this.words < b.words)
	    return -1;

	BigNumber difference = subtract(this, b);
	if (difference.words == 0)
	    return 0;
	return difference.sign.value;
    }

    /* Convert this BigNumber from current base to given base B */
    public BigNumber toBase(long B) {
	ListIterator<Long> wIterator = number.listIterator(words);
	BigNumber nBaseB = new BigNumber(null, B);
	BigNumber bBaseB = new BigNumber(base, B);
	while (wIterator.hasPrevious()) {
	    /*
	     * n = s[0]*base^n + s[1]*base^n-1 + ... + s[n-1]*base^1 + s[n]
	     * 
	     * n = (s[0]*base^n-1 + s[1]*base^n-2 + ... + s[n-1]) * base + s[n]
	     */
	    long word = wIterator.previous();
	    nBaseB = add(product(nBaseB, bBaseB), new BigNumber(word, B));
	}
	return nBaseB;
    }

    /* Print the number in decimal system (Base 10) */
    public String toString() {
	if (words == 0)
	    return "0";

	BigNumber nBase10 = null;
	if (base == 10)
	    /* Number already in Base 10 */
	    nBase10 = this;
	else
	    /* Convert number from current base to base 10 */
	    nBase10 = toBase(10);

	StringBuilder sb = new StringBuilder();
	if (sign == Sign.MINUS)
	    /* Negative number */
	    sb.append("-");
	ListIterator<Long> wIterator = nBase10.number.listIterator(nBase10.words);
	while (wIterator.hasPrevious())
	    /* Print the digits from MSD -> LSD */
	    sb.append(wIterator.previous());
	return sb.toString();
    }

    /* Print the underlying list of words in LSD -> MSD order */
    public void printList() {
	StringBuilder sb = new StringBuilder();
	sb.append(base + ":");
	if (words == 0) {
	    sb.append("0");

	} else {
	    Iterator<Long> wIterator = wordIterator();
	    while (wIterator.hasNext())
		/* Print the digits from LSD -> MSD */
		sb.append(wIterator.next() + " ");
	    if (sign == Sign.MINUS)
		/* Negative number */
		sb.append("-");

	}
	System.out.println(sb);
    }

    /* Create a similar BigNumber with the same value as this */
    @Override
    public BigNumber clone() {
	return new BigNumber(number, base, sign);
    }

    /*
     * Return ListIterator to iterate over only the significant words from LSD
     * -> MSD
     */
    public ListIterator<Long> wordIterator() {
	return new WordIterator(number.listIterator());
    }

    /*
     * Return ListIterator to iterate over only the significant words with start
     * initialized to index
     */
    public ListIterator<Long> wordIterator(int index) {
	return new WordIterator(number.listIterator(index));
    }

    /* Create a new BigNumber with numbers between from and to indices */
    public BigNumber splitByIndex(int from, int to) {
	return new BigNumber(number.subList(from, to), base, sign);
    }

    /* Add given numbers 'a' and 'b' */
    public static BigNumber add(BigNumber a, BigNumber b) {
	return Sum.add(a, b);
    }

    /* Subtract given numbers 'a' and 'b' */
    public static BigNumber subtract(BigNumber a, BigNumber b) {
	return Sum.subtract(a, b);
    }

    /* Multiply given numbers 'a' and 'b' */
    public static BigNumber product(BigNumber a, BigNumber b) {
	return Product.product(a, b);
    }

    /* Number 'a' raised to the power 'b' */
    public static BigNumber power(BigNumber a, long b) {
	return Product.power(a, b);
    }

    /* Number 'a' raised to the power 'b', where 'b' is also a BigNumber */
    public static BigNumber power(BigNumber a, BigNumber b) {
	return Product.power(a, b);
    }

    /* Factorial of number 'a' */
    public static BigNumber factorial(BigNumber a) {
	return Product.factorial(a);
    }

    /* Divide number 'a' by 'b' */
    public static BigNumber divide(BigNumber a, BigNumber b) {
	return Division.divide(a, b).quotient;
    }

    /* Modulo of number 'a' by 'b' */
    public static BigNumber modulo(BigNumber a, BigNumber b) {
	return Division.divide(a, b).remainder;
    }

    /* Square root of number 'a' */
    public static BigNumber squareRoot(BigNumber a) {
	return Division.squareRoot(a);
    }

    /**
     * An Iterator for external classes to access only the significant words in
     * the list leaving out the leading zeros of any
     */
    private class WordIterator implements ListIterator<Long> {
	ListIterator<Long> iterator;

	public WordIterator(ListIterator<Long> iterator) {
	    super();
	    this.iterator = iterator;
	}

	@Override
	public void add(Long e) {
	    iterator.add(e);
	    if (e != 0)
		words = number.size();
	}

	@Override
	public boolean hasNext() {
	    return iterator.nextIndex() < words;
	}

	@Override
	public boolean hasPrevious() {
	    return iterator.hasPrevious();
	}

	@Override
	public Long next() {
	    if (hasNext())
		return iterator.next();
	    throw new NoSuchElementException("The list has reached the last significant word");
	}

	@Override
	public int nextIndex() {
	    return iterator.nextIndex();
	}

	@Override
	public Long previous() {
	    return iterator.previous();
	}

	@Override
	public int previousIndex() {
	    return iterator.previousIndex();
	}

	@Override
	public void remove() {
	    iterator.remove();
	    words = Math.min(words, number.size());
	}

	@Override
	public void set(Long e) {
	    iterator.set(e);
	}
    }
}