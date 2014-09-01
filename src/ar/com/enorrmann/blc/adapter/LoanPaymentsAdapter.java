package ar.com.enorrmann.blc.adapter;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LoanPaymentsAdapter extends HtmlAdapter {

	final static String INVESTMENTS_PATH = "/investments";
	final String SITE_INVESTMENTS_PATH = "/loan/browse/lid/";

	public LoanPaymentsAdapter(String url, Document doc) {

		genericDto.add("url", url);

		Element paymentsTable = doc
				.select("table.text-center > tbody:nth-child(3)")
				.first();
		if (paymentsTable==null)return ;
		Elements rows = paymentsTable.getElementsByTag("tr"); 
		List<GenericDTO> investments = getInvestments(rows,doc.baseUri());
		genericDto.add("payments", investments);

	}
	private List<GenericDTO> getInvestments(Elements rows,String baseUri) {
		List<GenericDTO> investList = new ArrayList<GenericDTO>();
		// last value is just a sum
		for (int i=0;i<rows.size()-1;i++){
			Element aRow =rows.get(i);
			GenericDTO unGenericDTO = new GenericDTO();
			Elements cells = aRow.getElementsByTag("td"); 
			unGenericDTO.add("url", baseUri); 
			unGenericDTO.add("number", cells.get(0).text()); 
			unGenericDTO.add("dueDate", cells.get(1).text()); 
			unGenericDTO.add("start", cells.get(1).text().substring(0, 10));
			unGenericDTO.add("title", getTitle(baseUri));
			
			String datePosted = cells.get(2).text();
			unGenericDTO.add("datePosted", datePosted);
			
			String status = (datePosted!=null&&!datePosted.equals("- - -"))?"Paid":"Pending";
			unGenericDTO.add("status", status);
			investList.add(unGenericDTO);
		}
		return investList;
	}
	private String getTitle(String baseUri) {
		String title =  baseUri.substring(baseUri.lastIndexOf("/")+1);
		return title.replaceAll("-", " " );
	}
	private GenericDTO getApiLink(String url) {
		GenericDTO self = new GenericDTO();
		String id = extractFirstNumber(url, SITE_INVESTMENTS_PATH);
		self.add("rel", "self");
		self.add("href", INVESTMENTS_PATH + "/" + id);

		return self;

	}


}
