package nl.irp.sepa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class SEPACreditTransferTest extends XMLTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		HashMap<String, String> ns = new HashMap<String, String>();
		ns.put("ns", "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03");

		NamespaceContext ctx = new SimpleNamespaceContext(ns);
		XMLUnit.setXpathNamespaceContext(ctx);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
	}
	
	@Test
	public void test() throws DatatypeConfigurationException, JAXBException, XpathException, SAXException, IOException {
		LocalDateTime today = new LocalDateTime(2010, 9, 28, 14, 07);
		SEPACreditTransfer transfer = new SEPACreditTransfer();
		
		transfer.buildGroupHeader("message-id-001", "Bedrijfsnaam", today.toDate());
		
		transfer
			.betaalgroep("minimaal gevuld", new LocalDate(2009,11,01), "Naam", "NL44RABO0123456789", "RABONL2U")
				.creditTransfer("non ref", new BigDecimal("10.1"), "ABNANL2A", "Naam creditor", "NL90ABNA0111111111", "vrije tekst");
		
		/*
		transfer
			.betaalgroep("maximaal gevuld", new LocalDate(2009,11,01), "Naam", "NL44RABO0123456789", "RABONL2U")
				.creditTransfer("End-to-end-id-debtor-to-creditor-01", new BigDecimal("20.2"), "ABNANL2A", 
						"Naam creditor", "NL90ABNA0111111111", "jlkjlkj");
		*/
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		transfer.write(stream);
		String xml = stream.toString("UTF-8");
		System.out.print(xml);
		
		///////
		String example = Resources.toString( Resources.getResource("example.xml"), Charsets.UTF_8);
		assertXMLEqual(example, xml);
	}

}
