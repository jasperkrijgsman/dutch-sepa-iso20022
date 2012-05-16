package nl.irp.sepa;



import iso.std.iso._20022.tech.xsd.camt_053_001.AccountStatement2;
import iso.std.iso._20022.tech.xsd.camt_053_001.BankToCustomerStatementV02;
import iso.std.iso._20022.tech.xsd.camt_053_001.Document;
import iso.std.iso._20022.tech.xsd.camt_053_001.GroupHeader42;
import iso.std.iso._20022.tech.xsd.camt_053_001.Pagination;
import iso.std.iso._20022.tech.xsd.camt_053_001.PartyIdentification32;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;

import org.joda.time.base.AbstractDateTime;


/**
 * ISO20022 - "camt.053.001.02" in the Netherlands.
 * @author "Jasper Krijgsman <jasper@irp.nl>"
 *
 */
public class BankToCustomerStatement {

	private Document document;
	private BankToCustomerStatementV02 bankToCustomerStatement;
	private GroupHeader42 groupHeader;

	private BankToCustomerStatement(Document document) {
		this.document = document;
		this.bankToCustomerStatement = document.getBkToCstmrStmt();
		this.groupHeader = bankToCustomerStatement.getGrpHdr();
	}

	public static BankToCustomerStatement read(InputStream is) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(Document.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		
		// The UTF-8 character encoding standard must be used in the UNIFI messages.
		//unmarshaller.setProperty(Unm .JAXB_ENCODING, "UTF-8");
		
		StreamSource streamSource = new StreamSource(is);
		JAXBElement<Document> root = unmarshaller.unmarshal(streamSource, Document.class);
		
		return new BankToCustomerStatement(root.getValue());
	}
	
	public String getMsgId() {
		return groupHeader.getMsgId();
	}

	public Date getCreDtTm() {
		return groupHeader.getCreDtTm().toGregorianCalendar().getTime();
	}

	public PartyIdentification32 getMsgRcpt() {
		return groupHeader.getMsgRcpt();
	}

	public Pagination getMsgPgntn() {
		return groupHeader.getMsgPgntn();
	}

	public String getAddtlInf() {
		return groupHeader.getAddtlInf();
	}

	public List<AccountStatement2> getStmt() {
		return bankToCustomerStatement.getStmt();
	}
}
