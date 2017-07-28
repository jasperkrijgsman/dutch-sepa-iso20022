package nl.irp.sepa;

import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import nl.irp.sepa.BankToCustomerStatement.AccountStatement;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BankToCustomerStatementTest {

	@Test
	public void test() throws JAXBException, IOException {
		InputSupplier<InputStream> input = 
				Resources.newInputStreamSupplier(Resources.getResource("camt.053.001.02.xml"));
		
		BankToCustomerStatement bankToCustomerStatement = BankToCustomerStatement.read(input.getInput());
		
		// GrpHdr
		assertThat(bankToCustomerStatement.getMsgId(), is("AAAASESS-FP-STAT001"));
		assertThat(bankToCustomerStatement.getCreDtTm(), is(Date.from(ZonedDateTime.parse("2010-10-18T17:00:00+01:00").toInstant())));
		assertThat(bankToCustomerStatement.getMsgPgntn().getPgNb(), is("1"));
		assertThat(bankToCustomerStatement.getMsgPgntn().isLastPgInd(), is(true));

		AccountStatement stmt = bankToCustomerStatement.getStmt().get(0);
		
		assertThat(stmt.getId(), is("AAAASESS-FP-STAT001"));
		assertThat(stmt.getCreDtTm(), is(Date.from(ZonedDateTime.parse("2010-10-18T17:00:00+01:00").toInstant())));
		
		assertThat(stmt.getFrDt(), is(Date.from(ZonedDateTime.parse("2010-10-18T08:00:00+01:00").toInstant())));
		assertThat(stmt.getToDt(), is(Date.from(ZonedDateTime.parse("2010-10-18T17:00:00+01:00").toInstant())));
	}

}
