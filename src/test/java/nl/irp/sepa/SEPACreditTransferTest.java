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
	public void testABN() throws DatatypeConfigurationException, JAXBException, XpathException, SAXException, IOException {
		LocalDateTime today = new LocalDateTime("2012-05-02T14:52:09"); 
		SEPACreditTransfer transfer = new SEPACreditTransfer();
		
		transfer.buildGroupHeader("000001", "Klantnaam", today.toDate());
		
		transfer
			.betaalgroep("12345", new LocalDate(2012, 5, 2), "Debiteur", "NL10ABNA1234567890", "ABNANL2A")
				.creditTransfer("Onze referentie: 123456", new BigDecimal("386.00"), "RABONL2U", "Crediteur", "NL91RABO1234567890", "Ref. 2012.0386");
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		transfer.write(stream);
		String xml = stream.toString("UTF-8");

		String example = Resources.toString( Resources.getResource("abn/pain.001.001.03 voorbeeldbestand.xml"), Charsets.UTF_8);
		assertXMLEqual(example, xml);
	}
	
	@Test
	public void testING() throws DatatypeConfigurationException, JAXBException, XpathException, SAXException, IOException {
		LocalDateTime today = new LocalDateTime("2013-04-02T14:52:09"); 
		SEPACreditTransfer transfer = new SEPACreditTransfer();
		
		transfer.buildGroupHeader("MSGID005", "IPNORGANIZTIONNAME", today.toDate());
		
		transfer
			.betaalgroep("PAYID001", new LocalDate("2013-04-19"), "NAAM Debtor", "NL28INGB0000000001", "INGBNL2A")
				.creditTransfer("E2EID001", new BigDecimal("1.01"), "INGBNL2A", "NAAM cdtr", "NL98INGB0000000002", "Ref. 2012.0386");
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		transfer.write(stream);
		String xml = stream.toString("UTF-8");

		String example = Resources.toString( Resources.getResource("ing/pain.001.001.03 voorbeeldbestand.xml"), Charsets.UTF_8);
		assertXMLEqual(example, xml);
	}

}
