import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Class to implement division, modulo and square root of BigNumbers
 * 
 * @author G94
 */
public class Division {
	/* Class to store the quotient and remainder of a division operation */
	public static class Result {
		public BigNumber quotient;
		public BigNumber remainder;

		public Result(BigNumber quotient, BigNumber remainder) {
			this.quotient = quotient;
			this.remainder = remainder;
		}
	}

	/* Divide BigNumber 'a' by BigNumber 'b' */
	public static Result divide(BigNumber a, BigNumber b) {
		if (a == null || b == null) {
			return null;
		}

		if (a.words == 0)
			/* 0 / b = 0 */
			return new Result(new BigNumber(0, a.base), a.clone());
		if (b.words == 0)
			/* a / 0 = Infinity */
			throw new ArithmeticException("Divide by zero");

		Sign aSign = a.sign;
		a.sign = Sign.PLUS;
		Sign bSign = b.sign;
		b.sign = Sign.PLUS;

		Result c = divideByBS(a, b);
		c.quotient.sign = Sign.product(aSign, bSign);
		c.remainder.sign = bSign;

		a.sign = aSign;
		b.sign = bSign;
		return c;
	}

	/* Divide BigNumber 'a' by Long 'b' */
	public static Result divide(BigNumber a, long b) {
		if (b < a.base) {
			ArrayList<Long> qList = new ArrayList<>();
			long r = 0;
			ListIterator<Long> wIterator = a.wordIterator(a.words);
			while (wIterator.hasPrevious()) {
				/* Divide 'a' by 'b' from the MSD to LSD word by word */
				long num = r * a.base + wIterator.previous();
				r = num % b;
				qList.add(num / b);
			}

			BigNumber quotient = new BigNumber(null, a.base, Sign.product(a.sign, Sign.from(b)));
			for (int i = qList.size() - 1; i >= 0; i--)
				/* Add words into the list from the LSD to MSD */
				quotient.addWord(qList.get(i));
			BigNumber remainder = new BigNumber(r, a.base, Sign.from(b));
			return new Result(quotient, remainder);
		}
		return divide(a, new BigNumber(b, a.base, Sign.from(b)));
	}

	/*
	 * Internal method to divide BigNumber 'a' by BigNumber 'b' irrespective of
	 * their signs
	 */
	private static Result divideByBS(BigNumber a, BigNumber b) {
		int cValue = a.compare(b);
		BigNumber zero = new BigNumber(0, a.base);
		BigNumber one = new BigNumber(1, a.base);
		if (cValue == -1)
			/* a < b */
			return new Result(zero, a.clone());
		else if (cValue == 0)
			/* a = b */
			return new Result(one, zero);

		BigNumber bSquare = Product.power(b, 2);
		cValue = bSquare.compare(a);
		BigNumber start, end;
		if (cValue == 0) {
			/* b * b = a */
			return new Result(b.clone(), zero);
		} else if (cValue == 1) {
			/* b * b > a */
			start = one;
			end = b;
		} else {
			/* b * b < a */
			start = b;
			end = a;
		}

		while (start.compare(end) <= 0) {
			BigNumber mid = divide(Sum.add(start, end), 2).quotient;
			BigNumber midTimesB = Product.product(mid, b);
			cValue = midTimesB.compare(a);
			if (cValue == 0) {
				/* mid * b = a */
				return new Result(mid, zero);

			} else if (cValue == -1) {
				/* mid * b < a */
				BigNumber midPlusOne = Sum.add(mid, one);
				BigNumber midPlusOneTimesB = Sum.add(midTimesB, b);
				cValue = midPlusOneTimesB.compare(a);
				if (cValue == 0)
					/* mid+1 * b = a */
					return new Result(midPlusOne, zero);
				else if (cValue == 1)
					/* mid+1 * b > a */
					return new Result(mid, Sum.subtract(a, midTimesB));
				else
					/* mid+1 * b < a */
					start = midPlusOne;

			} else {
				/* mid * b > a */
				BigNumber midMinusOne = Sum.subtract(mid, one);
				BigNumber midMinusOneTimesB = Sum.subtract(midTimesB, b);
				cValue = midMinusOneTimesB.compare(a);
				if (cValue == 0)
					/* mid-1 * b = a */
					return new Result(midMinusOne, zero);
				else if (cValue == -1)
					/* mid-1 * b < a */
					return new Result(midMinusOne, Sum.subtract(a, midMinusOneTimesB));
				else
					/* mid-1 * b > a */
					end = midMinusOne;

			}
		}
		/* The execution shouldn't reach here */
		return null;
	}

	/* Square Root of BigNumber 'a' truncated to the floor value */
	public static BigNumber squareRoot(BigNumber a) {
		if (a.sign == Sign.MINUS) {
			/* square root of a -ve number is complex */
			throw new ArithmeticException("Square root of a -ve number");
		}

		int cValue;
		BigNumber one = new BigNumber(1, a.base);
		BigNumber start = one;
		BigNumber end = divide(a, 2).quotient;
		while (start.compare(end) <= 0) {
			BigNumber mid = divide(Sum.add(start, end), 2).quotient;
			BigNumber midSquare = Product.product(mid, mid);
			cValue = midSquare.compare(a);
			if (cValue == 0) {
				/* mid^2 = a */
				return mid;

			} else if (cValue == -1) {
				/* mid^2 < a */
				BigNumber midPlusOne = Sum.add(mid, one);
				BigNumber midPlusOneSquare = Product.product(midPlusOne, midPlusOne);
				cValue = midPlusOneSquare.compare(a);
				if (cValue == 0)
					/* (mid+1)^2 = a */
					return midPlusOne;
				else if (cValue == 1)
					/* (mid+1)^2 > a */
					return mid;
				else
					/* (mid+1)^2 < a */
					start = midPlusOne;

			} else {
				/* mid^2 > a */
				BigNumber midMinusOne = Sum.subtract(mid, one);
				BigNumber midMiusOneSquare = Product.product(midMinusOne, midMinusOne);
				cValue = midMiusOneSquare.compare(a);
				if (cValue <= 0)
					/* (mid-1)^2 <= a */
					return midMinusOne;
				else
					/* (mid-1)^2 > a */
					end = midMinusOne;

			}
		}
		/* The execution shouldn't reach here */
		return null;
	}
}