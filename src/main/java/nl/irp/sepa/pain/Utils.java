package nl.irp.sepa.pain;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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
	
}
