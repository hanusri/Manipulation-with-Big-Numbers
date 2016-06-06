import java.util.Iterator;
import java.util.ListIterator;

/**
 * Class to implement product, power and factorial operations of BigNumbers
 * 
 * @author G94
 */
public class Product {
	/* Product of BigNumber 'a' and BigNumber 'b' */
	public static BigNumber product(BigNumber a, BigNumber b) {
		if (a == null || b == null)
			return null;

		Sign aSign = a.sign;
		a.sign = Sign.PLUS;
		Sign bSign = b.sign;
		b.sign = Sign.PLUS;

		BigNumber c = productNbyN(a, b);
		c.sign = Sign.product(aSign, bSign);

		a.sign = aSign;
		b.sign = bSign;
		return c;
	}

	/* Product of BigNumber 'a' and Long 'b' */
	public static BigNumber product(BigNumber a, long b) {
		if (b < a.base) {
			BigNumber c = new BigNumber(null, a.base, Sign.product(a.sign, Sign.from(b)));
			long carry = 0;
			Iterator<Long> wIterator = a.wordIterator();
			while (wIterator.hasNext()) {
				/* Multiply 'a' by 'b' from the LSD to MSD word by word */
				long prod = wIterator.next() * b + carry;
				carry = prod / a.base;
				c.addWord(prod % a.base);

			}
			if (carry != 0)
				c.addWord(carry);
			return c;
		}
		return product(a, new BigNumber(b, a.base, Sign.from(b)));
	}

	/*
	 * Internal method to find the product of BigNumber 'a' and BigNumber 'b',
	 * irrespective of the signs. This method uses Karatsuba algorithm to find
	 * the product in O(n^log(3)) time.
	 */
	private static BigNumber productNbyN(BigNumber a, BigNumber b) {
		if (a.words == 0 || b.words == 0)
			/* Multiplication by zero */
			return new BigNumber(0, a.base);

		/* Base condition */
		if (a.words == 1)
			return product(b, a.getMSW());
		if (b.words == 1)
			return product(a, b.getMSW());

		int middle = (Math.min(a.words, b.words) + 1) / 2;
		/* a = a2*base^m + a1 */
		BigNumber a1 = a.splitByIndex(0, middle);
		BigNumber a2 = a.splitByIndex(middle, a.words);
		/* b = b2*base^m + b1 */
		BigNumber b1 = b.splitByIndex(0, middle);
		BigNumber b2 = b.splitByIndex(middle, b.words);

		/* c1 = a1 * b1 */
		BigNumber c1 = productNbyN(a1, b1);
		/* c2 = a2 * b2 */
		BigNumber c2 = productNbyN(a2, b2);
		/* c12 = (a1+a2) * (b1+b2) */
		BigNumber c12 = productNbyN(BigNumber.add(a1, a2), BigNumber.add(b1, b2));

		/* c = c2*base^2m + (c12 - c2 - c1)*base^m + c1 */
		c12 = BigNumber.subtract(c12, BigNumber.add(c2, c1));
		c12.addLSZeros(middle); /* multiplication by Base^middle */
		c2.addLSZeros(middle * 2); /* multiplication by Base^(2*middle) */

		return BigNumber.add(BigNumber.add(c2, c12), c1);
	}

	/* BigNumber 'a' raised to the power 'b', where 'b' is Long number */
	public static BigNumber power(BigNumber a, long n) {
		if (a.words == 0)
			/* 0^n = 0 */
			return new BigNumber(0, a.base);

		if (n == 0)
			/* a^0 = 1 */
			return new BigNumber(1, a.base);

		if (n % 2 == 0) {
			/* n = 2*m => a^n = a^m * a^m */
			BigNumber temp = power(a, n / 2);
			return BigNumber.product(temp, temp);

		} else {
			/* n = 2*m + 1 => a^n = a * a^(2*m) */
			return BigNumber.product(a, power(a, n - 1));

		}
	}

	/* BigNumber 'a' raised to the power 'b', where 'b' is also a BigNumber */
	public static BigNumber power(BigNumber a, BigNumber b) {
		BigNumber result = new BigNumber(1, a.base);
		ListIterator<Long> wIterator = b.wordIterator(b.words);
		while (wIterator.hasPrevious())
			/* a^(b2*base + b1) = (a^b2)^base * a^b1 */
			result = Product.product(power(result, a.base), power(a, wIterator.previous()));

		return result;
	}

	/* Factorial of BigNumber 'a' */
	public static BigNumber factorial(BigNumber a) {
		if (a.sign == Sign.MINUS)
			/* Factorial of -ve numbers is undefined */
			throw new ArithmeticException("Factorial of -ve number");

		if (a.words == 0)
			/* 0! = 1 */
			return new BigNumber(1, a.base);

		BigNumber one = new BigNumber(1, a.base);
		BigNumber c = a.clone();
		for (BigNumber i = BigNumber.subtract(a, one); i.words > 0; i = BigNumber.subtract(i, one))
			c = Product.product(c, i);
		c.sign = Sign.PLUS;
		return c;
	}
}