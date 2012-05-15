package nl.irp.sepa.pain;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.custommonkey.xmlunit.*;
import org.custommonkey.xmlunit.exceptions.XpathException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import sun.misc.Resource;

public class SEPACreditTransferTest extends XMLTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		HashMap<String, String> ns = new HashMap<String, String>();
		ns.put("ns", "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03");

		NamespaceContext ctx = new SimpleNamespaceContext(ns);
		XMLUnit.setXpathNamespaceContext(ctx);
	}
	
	@Test
	public void test() throws DatatypeConfigurationException, JAXBException, XpathException, SAXException, IOException {
		LocalDate today = new LocalDate(2012, 01, 01);
		SEPACreditTransfer transfer = new SEPACreditTransfer();
		
		transfer.buildGroupHeader("test-01", "Stedelijk Wonen", today.toDate());
		
		transfer
			.betaalgroep("test-01-a", today, "VvE accaciastraat", "NL44RABO0123456789", "RABONL2U")
				.creditTransfer("test-01-a-1", new BigDecimal("10.1"), "RABONL2U", "Jan Klaassen", "NL44RABO0123456789", "factuur 00001");
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		transfer.write(stream);
		String xml = stream.toString("UTF-8");
		System.out.print(xml);
		
		///////
		String example = Resources.toString( Resources.getResource("example.xml"), Charsets.UTF_8);
		assertXMLEqual(example, xml);
	}

}
