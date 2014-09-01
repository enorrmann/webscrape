package ar.com.enorrmann.blc.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BlcAdapter extends HtmlAdapter {

	public GenericDTO getGenericDto(String url, Document doc) {
		GenericDTO unGenericDTO = new GenericDTO();
		Elements funding = doc.select("span.green:contains(Funding)");
		Elements funded = doc.select("span.green:contains(Funded)");
		if (funding.isEmpty()&&funded.isEmpty()){
			unGenericDTO.add("status", "closed");
		} else {
			unGenericDTO.add("status", "open");
		}
		Element titleElement = doc.select(".loan-page-title h1 a").first();
		String title = titleElement.childNode(0).toString(); 
		unGenericDTO.add("url", url);
		//unGenericDTO.add("investments", getInvestments(doc));
		unGenericDTO.add("payments", getPayments(doc,url,title));
		unGenericDTO.add("descripcion", "desc");

		return unGenericDTO;
	}
	
	private List<GenericDTO> getPayments(Document doc,String url,String title){
		Elements rows = doc.select(".amortization-table tr");
		List<GenericDTO> payments = new ArrayList<GenericDTO>();
		for (int i=1;i<rows.size()-1;i++){
			Element aRow =rows.get(i);
			payments.add(getPayment(aRow,url,title));
		}
		return payments;
	}
	
	private List<GenericDTO> getInvestments(Document doc){
		Elements rows = doc.select("table .avatar-name-container span");
		List<GenericDTO> investments = new ArrayList<GenericDTO>();
		for (int i=0;i<rows.size();i++){
			Element aRow =rows.get(i);
			investments.add(getInvestment(aRow));
		}
		return investments;
	}
	
	private GenericDTO getInvestment(Element aRow){
		GenericDTO anInvestment = new GenericDTO();
		anInvestment.add("investor", aRow.childNode(0));
		return anInvestment; 
		
	}
	private GenericDTO getPayment(Element aRow,String url,String title){
		GenericDTO aPayment = new GenericDTO();
		Elements cells = aRow.select("td");
		aPayment.add("payNum", cells.get(0).childNode(0));
		aPayment.add("dueDate", cells.get(1).childNode(0));
		aPayment.add("datePosted", cells.get(2).childNode(0));
		aPayment.add("start", reformatDate(cells.get(1).childNode(0).toString()));
		aPayment.add("title", title);
		aPayment.add("url", url);
		return aPayment; 
		
	}
	
	private String reformatDate(String oldDateString){
		SimpleDateFormat oldFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);
		SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
		try {
			Date oldDate = oldFormat.parse(oldDateString);
			return newFormat.format(oldDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public GenericDTO getGenericDto(String url) {
		return null;
	}

}
