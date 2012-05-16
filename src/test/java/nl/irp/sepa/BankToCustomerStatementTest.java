package nl.irp.sepa;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import nl.irp.sepa.BankToCustomerStatement.AccountStatement;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;

public class BankToCustomerStatementTest {

	@Test
	public void test() throws JAXBException, IOException {
		InputSupplier<InputStream> input = 
				Resources.newInputStreamSupplier(Resources.getResource("camt.053.001.02.xml"));
		
		BankToCustomerStatement bankToCustomerStatement = BankToCustomerStatement.read(input.getInput());
		
		// GrpHdr
		assertThat(bankToCustomerStatement.getMsgId(), is("AAAASESS-FP-STAT001"));
		assertThat(bankToCustomerStatement.getCreDtTm(), is(new DateTime("2010-10-18T17:00:00+01:00").toDate()));
		assertThat(bankToCustomerStatement.getMsgPgntn().getPgNb(), is("1"));
		assertThat(bankToCustomerStatement.getMsgPgntn().isLastPgInd(), is(true));

		AccountStatement stmt = bankToCustomerStatement.getStmt().get(0);
		
		assertThat(stmt.getId(), is("AAAASESS-FP-STAT001"));
		assertThat(stmt.getCreDtTm(), is(new DateTime("2010-10-18T17:00:00+01:00").toDate()));
		
		assertThat(stmt.getFrDt(), is(new DateTime("2010-10-18T08:00:00+01:00").toDate()));
		assertThat(stmt.getToDt(), is(new DateTime("2010-10-18T17:00:00+01:00").toDate()));
		
		
	}

}
