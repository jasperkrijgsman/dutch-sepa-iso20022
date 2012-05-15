package nl.irp.sepa.pain;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.joda.time.LocalDate;
import org.junit.Test;

public class SEPACreditTransferTest {

	@Test
	public void test() throws DatatypeConfigurationException, JAXBException, FileNotFoundException {
		SEPACreditTransfer transfer = new SEPACreditTransfer();
		
		transfer.buildGroupHeader("test-01", "Stedelijk Wonen");
		
		transfer
			.betaalgroep("test-01-a", new LocalDate(), "VvE accaciastraat", "NL44RABO0123456789", "RABONL2U")
				.creditTransfer("test-01-a-1", new BigDecimal("10.1"), "RABONL2U", "Jan Klaassen", "NL44RABO0123456789", "factuur 00001")
				.creditTransfer("test-01-a-2", new BigDecimal("15.1"), "RABONL2U", "Piet Janssen", "NL44RABO0987654321", "factuur 00002");
		
		transfer
			.betaalgroep("test-02-a", new LocalDate(), "VvE boulevard", "NL44RABO0123456789", "RABONL2U")
				.creditTransfer("test-01-b-1", new BigDecimal("20.1"), "RABONL2U", "Jan Klaassen",     "NL44RABO0123456789", "factuur 00003")
				.creditTransfer("test-01-b-2", new BigDecimal("25.1"), "INGBNL2A", "Jasper Krijgsman", "NL68INGB0008561374", "factuur 00004");
		
		transfer.write(System.out);		
	}

}
