package nl.irp.sepa.pain;

import java.io.OutputStream;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import nl.irp.sepa.pain.model.AccountIdentification4Choice;
import nl.irp.sepa.pain.model.ActiveOrHistoricCurrencyAndAmount;
import nl.irp.sepa.pain.model.AmountType3Choice;
import nl.irp.sepa.pain.model.BranchAndFinancialInstitutionIdentification4;
import nl.irp.sepa.pain.model.CashAccount16;
import nl.irp.sepa.pain.model.CreditTransferTransactionInformation10;
import nl.irp.sepa.pain.model.CustomerCreditTransferInitiationV03;
import nl.irp.sepa.pain.model.Document;
import nl.irp.sepa.pain.model.FinancialInstitutionIdentification7;
import nl.irp.sepa.pain.model.GroupHeader32;
import nl.irp.sepa.pain.model.ObjectFactory;
import nl.irp.sepa.pain.model.PartyIdentification32;
import nl.irp.sepa.pain.model.PaymentIdentification1;
import nl.irp.sepa.pain.model.PaymentInstructionInformation3;
import nl.irp.sepa.pain.model.PaymentMethod3Code;
import nl.irp.sepa.pain.model.RemittanceInformation5;

/**
 * The Customer SEPA Credit Transfer Initiation message is sent by the initiating party to the debtor bank. It
 * is used to request movement of funds from the debtor account to a creditor account.
 * 
 * 
 * According to the Implementation Guidelines for the XML Customer Credit Transfer Initiation
 * message UNIFI (ISO20022) - “pain.001.001.03” in the Netherlands.
 * 
 * And: XML message for SEPA Credit Transfer Initiation Implementation Guidelines for the Netherlands
 * Version 5.0 – January 2012
 * 
 * @author Jasper Krijgsman <jasper@irp.nl>
 */
public class SEPACreditTransfer {
	
	private Document document = new Document();
	private CustomerCreditTransferInitiationV03 customerCreditTransferInitiation;
	private GroupHeader32 groupHeader;
	
	public SEPACreditTransfer() throws DatatypeConfigurationException {
		customerCreditTransferInitiation= new CustomerCreditTransferInitiationV03();
		document.setCstmrCdtTrfInitn(customerCreditTransferInitiation);
	}
	
	public void write(OutputStream os) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(Document.class);
		Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        // The UTF-8 character encoding standard must be used in the UNIFI messages.
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(new ObjectFactory().createDocument(document), os);
	}
		
	/**
	 * Group Header: This building block is mandatory and present once. It contains elements such as
	 * Message Identification, Creation Date and Time, Grouping Indicator.
	 * Set of characteristics shared by all individual transactions included in the message.
	 * @param msgId Point to point reference, as assigned by the instructing party
	 * @param name Name of the party that initiates the payment.
	 * @throws DatatypeConfigurationException 
	 */
	public void buildGroupHeader(String msgId, String name) throws DatatypeConfigurationException {
		groupHeader =  new GroupHeader32();
		// Point to point reference, as assigned by the instructing party, and sent to the next
		// party in the chain to unambiguously identify the message.
		// The instructing party has to make sure that MessageIdentification is unique per
		// instructed party for a pre-agreed period.
		groupHeader.setMsgId(msgId);
		
		// Date and time at which the message was created.
		DateTime datetime = new DateTime();
		groupHeader.setCreDtTm( 
				DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(datetime.toGregorianCalendar()));
		
		// Number of individual transactions contained in the message.
		groupHeader.setNbOfTxs("0");
		
		//Total of all individual amounts included in the message.
		groupHeader.setCtrlSum(BigDecimal.ZERO);
		
		// Party that initiates the payment.
		PartyIdentification32 partyIdentification = new PartyIdentification32();
		partyIdentification.setNm(name);
		groupHeader.setInitgPty(partyIdentification);
	
		customerCreditTransferInitiation.setGrpHdr(groupHeader);
	}
	
	/**
	 * Payment Information: This building block is mandatory and repetitive. It contains besides
	 * elements related to the debit side of the transaction, such as Debtor and Payment Type
	 * Information, also one or several Transaction Information Blocks.
	 * 
	 * Set of characteristics that applies to the debit side of the payment transactions
	 * included in the credit transfer initiation.
	 * @param pmtInfId Unique identification, as assigned by a sending party, to unambiguously identify the 
	 * payment information group within the message.
	 * @param date This is the date on which the debtor's account is to be debited. 
	 * @param debtorNm Party that owes an amount of money to the (ultimate) creditor.
	 * @param debtorAccountIBAN Unambiguous identification of the account of the debtor to which a debit 
	 * entry will be made as a result of the transaction.
	 * @return 
	 * @throws DatatypeConfigurationException
	 */
	public Betaalgroep betaalgroep(
			String pmtInfId, LocalDate reqdExctnDt,
			String debtorNm, String debtorAccountIBAN, String financialInstitutionBIC) throws DatatypeConfigurationException {
		PaymentInstructionInformation3 paymentInstructionInformation = new PaymentInstructionInformation3();
		//customerCreditTransferInitiation.getPmtInf().add(paymentInstructionInformation);
		
		// Unique identification, as assigned by a sending party, to unambiguously identify the
		// payment information group within the message.
		paymentInstructionInformation.setPmtInfId(pmtInfId);
		
		// Specifies the means of payment that will be used to move the amount of money.
		// Only ‘TRF’ is allowed.
		paymentInstructionInformation.setPmtMtd(PaymentMethod3Code.TRF);
		
		// Number of individual transactions contained in the payment information group.
		paymentInstructionInformation.setNbOfTxs("0");

		// Total of all individual amounts included in the group
		paymentInstructionInformation.setCtrlSum(BigDecimal.ZERO);

		// This is the date on which the debtor's account is to be debited. 
		paymentInstructionInformation.setReqdExctnDt(DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(reqdExctnDt.toDateTimeAtStartOfDay().toGregorianCalendar()));
		
		// Party that owes an amount of money to the (ultimate) creditor.
		PartyIdentification32 debtor = new PartyIdentification32();
		paymentInstructionInformation.setDbtr(debtor);
		debtor.setNm(debtorNm);

		// Unambiguous identification of the account of the debtor to which a debit entry will be
		// made as a result of the transaction.
		CashAccount16 debtorAccount = new CashAccount16();
		paymentInstructionInformation.setDbtrAcct(debtorAccount);
		// Account id,  Only IBAN is allowed.
		AccountIdentification4Choice debtorAccountId = new AccountIdentification4Choice();
		debtorAccountId.setIBAN(debtorAccountIBAN);
		debtorAccount.setId(debtorAccountId);
		paymentInstructionInformation.setDbtrAcct(debtorAccount);
		
		// Financial institution servicing an account for the debtor.
		BranchAndFinancialInstitutionIdentification4 debtorAgent = new BranchAndFinancialInstitutionIdentification4();
		paymentInstructionInformation.setDbtrAgt(debtorAgent);
		FinancialInstitutionIdentification7 financialInstitutionIdentification = new FinancialInstitutionIdentification7();
		// Only BIC is allowed.
		financialInstitutionIdentification.setBIC(financialInstitutionBIC);
		debtorAgent.setFinInstnId(financialInstitutionIdentification);
		paymentInstructionInformation.setDbtrAgt(debtorAgent);
		
		customerCreditTransferInitiation.getPmtInf().add(paymentInstructionInformation);
		
		return new Betaalgroep(paymentInstructionInformation);
	}
	
	
	public class Betaalgroep {
		
		private PaymentInstructionInformation3 paymentInstructionInformation3;
		
		public Betaalgroep(PaymentInstructionInformation3 paymentInstructionInformation3) {
			this.paymentInstructionInformation3 = paymentInstructionInformation3;
		}
		
		/**
		 * Set of elements used to provide information on the individual transaction(s) included in the message.
		 * 
		 * @param endToEndId Unique identification assigned by the initiating party to unambiguously identify the
		 * transaction. This identification is passed on, unchanged, throughout the entire end-to-end chain. maxLength: 35
		 * @param amount Amount of money to be moved between the debtor and creditor, before deduction of 
		 * charges, expressed in the currency as ordered by the initiating party.
		 * @param creditorfinancialInstitutionBic Financial institution servicing an account for the creditor.
		 * @param creditorNm Party to which an amount of money is due.
		 * @param iban Unambiguous identification of the account of the creditor to which a credit entry will
		 * be posted as a result of the payment transaction.
		 * @return 
		 * 
		 */
		public Betaalgroep creditTransfer(String endToEndId, BigDecimal amount,
				String creditorfinancialInstitutionBic,
				String creditorNm, String iban,
				String text) {
			
			CreditTransferTransactionInformation10 creditTransferTransactionInformation 
				= new CreditTransferTransactionInformation10();
			
			// Unique identification as assigned by an instructing party for an instructed party to
			// unambiguously identify the instruction.
			PaymentIdentification1 paymentIdentification = new PaymentIdentification1();
			paymentIdentification.setEndToEndId(endToEndId);
			creditTransferTransactionInformation.setPmtId(paymentIdentification);
			
			// Amount of money to be moved between the debtor and creditor, before deduction of 
			// charges, expressed in the currency as ordered by the initiating party.
			AmountType3Choice amountType = new AmountType3Choice();
			ActiveOrHistoricCurrencyAndAmount currencyAndAmount = new ActiveOrHistoricCurrencyAndAmount();
			currencyAndAmount.setCcy("EUR");
			currencyAndAmount.setValue(amount);
			amountType.setInstdAmt(currencyAndAmount);
			creditTransferTransactionInformation.setAmt(amountType);
			
			// Financial institution servicing an account for the creditor.
			BranchAndFinancialInstitutionIdentification4 creditorAgent = new BranchAndFinancialInstitutionIdentification4();
			FinancialInstitutionIdentification7 creditorfinancialInstitutionIdentification = new FinancialInstitutionIdentification7();
			// Only BIC is allowed.
			creditorfinancialInstitutionIdentification.setBIC(creditorfinancialInstitutionBic);
			creditorAgent.setFinInstnId(creditorfinancialInstitutionIdentification);
			creditTransferTransactionInformation.setCdtrAgt(creditorAgent);
			
			// Party to which an amount of money is due.
			PartyIdentification32 creditor = new PartyIdentification32();
			creditor.setNm(creditorNm);
			creditTransferTransactionInformation.setCdtr(creditor);
			
			// Unambiguous identification of the account of the creditor to which a credit entry will
			// be posted as a result of the payment transaction.
			CashAccount16 creditorAccount = new CashAccount16();
			AccountIdentification4Choice creditorAccountId = new AccountIdentification4Choice();
			// Only IBAN is allowed.
			creditorAccountId.setIBAN(iban);
			creditorAccount.setId(creditorAccountId);
			creditTransferTransactionInformation.setCdtrAcct(creditorAccount);
			
			// Information supplied to enable the matching of an entry with the items that the
			// transfer is intended to settle, such as commercial invoices in an accounts' receivable
			// system
			// TODO: structured
			RemittanceInformation5 remittanceInformation = new RemittanceInformation5();
			remittanceInformation.getUstrd().add(text);
			creditTransferTransactionInformation.setRmtInf(remittanceInformation);

			paymentInstructionInformation3.getCdtTrfTxInf().add(creditTransferTransactionInformation);
			
			// Control sum
			paymentInstructionInformation3.setCtrlSum( paymentInstructionInformation3.getCtrlSum().add(amount) );
			groupHeader.setCtrlSum( groupHeader.getCtrlSum().add(amount) );
			
			// Number of transactions
			paymentInstructionInformation3.setNbOfTxs( String.valueOf(paymentInstructionInformation3.getCdtTrfTxInf().size()) );
			Integer nbOfTxs = Integer.parseInt(groupHeader.getNbOfTxs());
			nbOfTxs += 1;
			groupHeader.setNbOfTxs(nbOfTxs.toString());
			
			return this;
		}
	}
	
}
