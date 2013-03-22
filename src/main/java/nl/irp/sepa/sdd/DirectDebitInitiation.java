package nl.irp.sepa.sdd;

import static com.google.common.base.Preconditions.checkArgument;
import static nl.irp.sepa.sdd.Utils.createAccount;
import static nl.irp.sepa.sdd.Utils.createAmount;
import static nl.irp.sepa.sdd.Utils.createFinInstnId;
import static nl.irp.sepa.sdd.Utils.createParty;
import static nl.irp.sepa.sdd.Utils.createPaymentIdentification;
import static nl.irp.sepa.sdd.Utils.createRmtInf;
import static nl.irp.sepa.sdd.Utils.createXMLGregorianCalendar;
import static nl.irp.sepa.sdd.Utils.createXMLGregorianCalendarDate;
import iso.std.iso._20022.tech.xsd.pain_008_001.CustomerDirectDebitInitiationV02;
import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransactionInformation9;
import iso.std.iso._20022.tech.xsd.pain_008_001.Document;
import iso.std.iso._20022.tech.xsd.pain_008_001.GroupHeader39;
import iso.std.iso._20022.tech.xsd.pain_008_001.ObjectFactory;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentInstructionInformation4;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentMethod2Code;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


/**
 * This document describes the Implementation Guidelines for the XML SEPA Direct Debit Initiation message
 * UNIFI (ISO20022) - “pain.008.001.02” in the Netherlands.

 * @author "Jasper Krijgsman <jasper@irp.nl>"
 *
 */
public class DirectDebitInitiation {

	private Document document = new Document();
	private CustomerDirectDebitInitiationV02 customerDirectDebitInitiationV02;
	private GroupHeader39 groupHeader;
	
	public DirectDebitInitiation() {
		customerDirectDebitInitiationV02 = new CustomerDirectDebitInitiationV02();
		document.setCstmrDrctDbtInitn(customerDirectDebitInitiationV02);
	}
	
	/**
	 * Set of characteristics shared by all individual transactions included in the message.
	 * @param msgId Point to point reference, assigned by the instructing party and sent to 
	 * the next party in the chain, to unambiguously identify the message.
	 * @param name
	 * @param date
	 */
	public void buildGroupHeader(String msgId, String name, Date date) {
		groupHeader = new GroupHeader39();
		
		// if no msgId is given create one
		if(msgId==null)
			msgId = UUID.randomUUID().toString().replaceAll("-", "");
		checkArgument(msgId.length()<=35, "length of setMsgId is more than 35");
		checkArgument(msgId.length()>1, "length of setMsgId is less than 1");
		groupHeader.setMsgId(msgId);
		
		// Date and time at which the message was created.
		groupHeader.setCreDtTm( createXMLGregorianCalendar(date));
		
		// Number of individual transactions contained in the message.
		groupHeader.setNbOfTxs("0");
		
		//Total of all individual amounts included in the message.
		groupHeader.setCtrlSum(BigDecimal.ZERO);
		
		// Party that initiates the payment.
		groupHeader.setInitgPty( createParty(name) );
		
		customerDirectDebitInitiationV02.setGrpHdr(groupHeader);
	}
	
	public void write(OutputStream os) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(Document.class);
		Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        // The UTF-8 character encoding standard must be used in the UNIFI messages.
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(new ObjectFactory().createDocument(document), os);
	}
	
	public PaymentInstruction paymentInstruction(String pmtInfId, Date reqdColltnDt, String creditor, String creditorAccount, String creditorBic) {
		PaymentInstruction paymentInstruction = new PaymentInstruction(pmtInfId, reqdColltnDt, creditor, creditorAccount, creditorBic);
		this.customerDirectDebitInitiationV02.getPmtInf().add(paymentInstruction.getPaymentInstructionInformation());
		return paymentInstruction;
	}
	
	public class PaymentInstruction {
		
		private PaymentInstructionInformation4 paymentInstructionInformation;
		
		/**
		 * 
		 * @param pmtInfId
		 * @param reqdColltnDt Date and time at which the creditor requests that the amount of money is to be 
		 * collected from the debtor.
		 */
		public PaymentInstruction(String pmtInfId, Date reqdColltnDt, String creditor, String creditorAccount, String creditorBic) {
			paymentInstructionInformation = new PaymentInstructionInformation4(); 
			
			// Unique identification, as assigned by a sending party, to
			// unambiguously identify the payment information group within the message.
			checkArgument(pmtInfId.length()<=35, "length of pmtInfId is more than 35");
			checkArgument(pmtInfId.length()>1, "length of pmtInfId is less than 1");
			paymentInstructionInformation.setPmtInfId(pmtInfId);
			
			// Specifies the means of payment that will be used to move the amount of money.
			// DD=DirectDebit 
			paymentInstructionInformation.setPmtMtd(PaymentMethod2Code.DD);
			
			// Date and time at which the creditor requests that the amount of money is to be
			// collected from the debtor.
			paymentInstructionInformation.setReqdColltnDt( createXMLGregorianCalendarDate(reqdColltnDt) );
			
			// Party to which an amount of money is due.
			paymentInstructionInformation.setCdtr( createParty(creditor) );
			// Unambiguous identification of the account of the creditor to which a credit entry will
			// be posted as a result of the payment transaction. Only IBAN is allowed.
			paymentInstructionInformation.setCdtrAcct( createAccount(creditorAccount) );
			
			paymentInstructionInformation.setCdtrAgt( createFinInstnId(creditorBic) );		
		}
		
		public void addTransaction(BigDecimal amount, String debtor, String debtorIban, String debtorBic, String remittanceInformation) {
			DirectDebitTransactionInformation9 directDebitTransactionInformation = new DirectDebitTransactionInformation9();
			
			// Set of elements used to reference a payment instruction.
			String instructionIdentification = UUID.randomUUID().toString().replaceAll("-", "");
			String endToEndIdentification = UUID.randomUUID().toString().replaceAll("-", "");
			directDebitTransactionInformation.setPmtId( createPaymentIdentification(instructionIdentification, endToEndIdentification));
			
			// Amount of money to be moved between the debtor and creditor, before deduction
			// of charges, expressed in the currency as ordered by the initiating party.
			directDebitTransactionInformation.setInstdAmt( createAmount(amount) );
			
			// Financial institution servicing an account for the debtor.
			directDebitTransactionInformation.setDbtrAgt( createFinInstnId(debtorBic) );
			
			// Party that owes an amount of money to the (ultimate) creditor.
			directDebitTransactionInformation.setDbtr( createParty(debtor) );
			directDebitTransactionInformation.setDbtrAgtAcct( createAccount(debtorIban) );
			
			directDebitTransactionInformation.setRmtInf( createRmtInf(remittanceInformation) );
			
			paymentInstructionInformation.getDrctDbtTxInf().add(directDebitTransactionInformation);
			
			BigDecimal ctrlSum = groupHeader.getCtrlSum();
			ctrlSum = ctrlSum.add(amount);
			groupHeader.setCtrlSum(ctrlSum);
			
			int nbOfTxs = Integer.parseInt(groupHeader.getNbOfTxs());
			nbOfTxs = nbOfTxs+1;
			groupHeader.setNbOfTxs(String.valueOf(nbOfTxs));
			
		}
		
		public PaymentInstructionInformation4 getPaymentInstructionInformation() {
			return paymentInstructionInformation;
		}
		
	}
}
