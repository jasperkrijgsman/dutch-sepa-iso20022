package nl.irp.sepa.pain;

import static com.google.common.base.Preconditions.checkArgument;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.irp.sepa.pain.model.AccountIdentification4Choice;
import nl.irp.sepa.pain.model.ActiveOrHistoricCurrencyAndAmount;
import nl.irp.sepa.pain.model.AmountType3Choice;
import nl.irp.sepa.pain.model.BranchAndFinancialInstitutionIdentification4;
import nl.irp.sepa.pain.model.CashAccount16;
import nl.irp.sepa.pain.model.FinancialInstitutionIdentification7;
import nl.irp.sepa.pain.model.PartyIdentification32;
import nl.irp.sepa.pain.model.RemittanceInformation5;

public class Utils {

	public static XMLGregorianCalendar createXMLGregorianCalendar(Date currentDateTime) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(currentDateTime);

		XMLGregorianCalendar createDate;
		try {
			createDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}

		return createDate;
	}
	
	/**
	 * Information supplied to enable the matching of an entry with the items that the
	 * transfer is intended to settle, such as commercial invoices in an accounts' receivable
	 * system
	 * max length: 140
	 * @return
	 */
	public static RemittanceInformation5 createRmtInf(String info) {
		checkArgument(info.length() <= 140); //maxLength: 140
		checkArgument(info.length() >= 1);   //minLength: 1
		
		RemittanceInformation5 remittanceInformation = new RemittanceInformation5();
		remittanceInformation.getUstrd().add(info);
		return remittanceInformation;
	}
	
	/**
	 * Unambiguous identification of a account
	 * @return 
	 */
	public static CashAccount16 createAccount(String iban) {
		CashAccount16 account = new CashAccount16();
		AccountIdentification4Choice creditorAccountId = new AccountIdentification4Choice();
		// Only IBAN is allowed.
		creditorAccountId.setIBAN(iban);
		account.setId(creditorAccountId);
		return account;
	}
	
	public static PartyIdentification32 createParty(String nm) {
		PartyIdentification32 party = new PartyIdentification32();
		party.setNm(nm);
		return party;
	}
	
	public static BranchAndFinancialInstitutionIdentification4 createFinInstnId(String bic) {
		BranchAndFinancialInstitutionIdentification4 creditorAgent = new BranchAndFinancialInstitutionIdentification4();
		FinancialInstitutionIdentification7 creditorfinancialInstitutionIdentification = new FinancialInstitutionIdentification7();
		// Only BIC is allowed.
		creditorfinancialInstitutionIdentification.setBIC(bic);
		creditorAgent.setFinInstnId(creditorfinancialInstitutionIdentification);
		return creditorAgent;
	}
	
	public static AmountType3Choice createAmount(BigDecimal amount) {
		AmountType3Choice amt = new AmountType3Choice();
		ActiveOrHistoricCurrencyAndAmount instdAmt = new ActiveOrHistoricCurrencyAndAmount();
		instdAmt.setValue(amount);
		instdAmt.setCcy("EUR");
		amt.setInstdAmt(instdAmt);
		return amt;
	}
	
}
