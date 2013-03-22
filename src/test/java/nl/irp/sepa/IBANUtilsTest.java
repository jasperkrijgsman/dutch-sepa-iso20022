package nl.irp.sepa;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

public class IBANUtilsTest {
	
	String iban_nl = "NL05123412341234123400";
	String iban_nl_spaces = "NL05 1234 1234 1234 1234 00";
	String iban_nl_mess = "NL05.1234-12341234_1234 00";
	
	@Test
	public void testMakeReadable() {
		String result = IBANUtils.makeReadable(iban_nl);
		assertThat(result, is(iban_nl_spaces));
		
		String result2 = IBANUtils.makeReadable(iban_nl_mess);
		assertThat(result2, is(iban_nl_spaces));
	}
	
	@Test
	public void testClean() {
		String result = IBANUtils.clean(iban_nl_mess);
		assertThat(result, is(iban_nl));
	}
	
	@Test
	public void testValidate() {
		assertThat(IBANUtils.validate(iban_nl), is(true));
	}

}
