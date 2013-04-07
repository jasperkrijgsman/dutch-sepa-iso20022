package nl.irp.sepa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import nl.irp.sepa.sdd.DirectDebitInitiation;
import nl.irp.sepa.sdd.DirectDebitInitiation.PaymentInstruction;

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
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public class DirectDebitInitiationTest extends XMLTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		HashMap<String, String> ns = new HashMap<String, String>();
		ns.put("ns", "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02");

		NamespaceContext ctx = new SimpleNamespaceContext(ns);
		XMLUnit.setXpathNamespaceContext(ctx);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
	}
	
	@Test
	public void test() throws DatatypeConfigurationException, JAXBException, XpathException, SAXException, IOException {
		LocalDateTime today = new LocalDateTime("2012-02-22T09:29:54"); 
		DirectDebitInitiation debitInitiation = new DirectDebitInitiation();
		
		debitInitiation.buildGroupHeader("1000004207", "Naam", today.toDate());
		
		String pmtInfId = "1000004207";
		PaymentInstruction paymentInstruction = debitInitiation
			.paymentInstruction(
				pmtInfId, new LocalDate(2012, 2, 21).toDate(),
				"Naam", "NL", ImmutableList.of("Dorpstraat 1", "Amsterdam"),
				"DE12345678901234567890", "ABNADEFFFRA");
		
		paymentInstruction.addTransaction(
			"01-E30220000000382012",     //InstrId
			"2000000038",                //EndToEndId
			new BigDecimal("1600.00"),
			"MANDAAT123456", new LocalDate("2010-09-05"), "NL89ZZZ011234567890",
			"FICO Customer account",
			"DE12345678901234567890",
			"NBAGDE3E",
			"DE", ImmutableList.of("123, ABC street", "32547 Frankfurt Germany"),
			"/INV/ 8/29/2011");
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		debitInitiation.write(stream);
		String xml = stream.toString("UTF-8");
		
		System.out.println(xml);

		String example = Resources.toString( Resources.getResource("pain.008.001.02 voorbeeldbestand.xml"), Charsets.UTF_8);
		assertXMLEqual(example, xml);
	}
	
	

}
