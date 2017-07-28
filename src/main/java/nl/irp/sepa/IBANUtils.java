package nl.irp.sepa;

import java.math.BigInteger;
import java.util.regex.Pattern;

import com.neovisionaries.i18n.CountryCode;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class IBANUtils {

	private static final int IBANNUMBER_MIN_SIZE = 15;
	private static final int IBANNUMBER_MAX_SIZE = 34;
	private static final BigInteger IBANNUMBER_MAGIC_NUMBER = new BigInteger("97");

	/**
	 * Format the code as a human readable string.
	 * The IBAN should not contain spaces when transmitted electronically. 
	 * However, when printed on paper, the IBAN is expressed in groups of four characters 
	 * separated by a single space, the last group being of variable length.
	 * <code>GB29 NWBK 6016 1331 9268 19</code>
	 * @param iban
	 * @return
	 */
	public static String makeReadable(String iban) {
		Iterable<String> parts = Splitter.fixedLength(4).split(iban);
		return Joiner.on(" ").join(parts);
	}
	
	public static String clean(String iban) {
		// remove any strange characters
		iban = removeNonAlpha(iban);
		validate(iban);
		return iban;
	}
	
	public static boolean validate(String iban) {
		// Check ISO 3166-1 country code
		try {
			final String country = iban.substring(0, 2);
			CountryCode.valueOf(country);
		}
		catch (IllegalArgumentException e) {
			return false;
		}

		String newAccountNumber = iban.trim();

		// Check that the total IBAN length is correct as per the country. If not, the IBAN is invalid. We could also check
		// for specific length according to country, but for now we won't
		if (newAccountNumber.length() < IBANNUMBER_MIN_SIZE || newAccountNumber.length() > IBANNUMBER_MAX_SIZE) {
			return false;
		}

		// Move the four initial characters to the end of the string.
		newAccountNumber = newAccountNumber.substring(4) + newAccountNumber.substring(0, 4);

		// Replace each letter in the string with two digits, thereby expanding the string, where A = 10, B = 11, ..., Z = 35.
		StringBuilder numericAccountNumber = new StringBuilder();
		for (int i = 0; i < newAccountNumber.length(); i++) {
			numericAccountNumber.append(Character.getNumericValue(newAccountNumber.charAt(i)));
		}

		// Interpret the string as a decimal integer and compute the remainder of that number on division by 97.
		try {
			BigInteger ibanNumber = new BigInteger(numericAccountNumber.toString());
			return ibanNumber.mod(IBANNUMBER_MAGIC_NUMBER).intValue() == 1;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static String removeNonAlpha(final String iban) {
		final StringBuffer result = new StringBuffer();
		for (int i=0;i<iban.length();i++) {
			char c = iban.charAt(i);
			if (Character.isLetter(c) || Character.isDigit(c) ) {
				result.append((char)c);
			}
		}
		return result.toString();
	}
	
	/**
	 * Translate letters to numbers, also ignoring non alphanumeric characters
	 * 
	 * @param bban
	 * @return the translated value
	 */
	public String translateChars(final StringBuffer bban) {
		final StringBuffer result = new StringBuffer();
		for (int i=0;i<bban.length();i++) {
			char c = bban.charAt(i);
			if (Character.isLetter(c)) {
				result.append(Character.getNumericValue(c));
			} else {
				result.append((char)c);
			}
		}
		return result.toString();
	}
	
	private static Pattern ibanRegex = 
			Pattern.compile("[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}");
	
}
