package ar.com.enorrmann.blc.adapter;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LoanInvestmentsAdapter extends HtmlAdapter {

	final static String INVESTMENTS_PATH = "/investments";
	final String SITE_INVESTMENTS_PATH = "/loan/browse/lid/";

	public LoanInvestmentsAdapter(String url, Document doc) {
				super.genericDto.add("url", url);


				Elements disclaimer = doc.select(".disclaimer");
				String investmentsQuery=null;
				if (disclaimer.isEmpty()){
					 investmentsQuery = "div.profile-content-right:nth-child(9) > table:nth-child(2) > tbody:nth-child(2)"; 
				} else {
					 investmentsQuery = "div.profile-content-right:nth-child(10) > table:nth-child(2) > tbody:nth-child(2)";
				}
				Element investmentsTable = doc.select(investmentsQuery).first();
		
		if (investmentsTable==null)return ; 
		Elements rows = investmentsTable.getElementsByTag("tr"); 
		List<GenericDTO> investments = getInvestments(rows);
		genericDto.add("investments", investments);

	}
	private List<GenericDTO> getInvestments(Elements rows) {
		List<GenericDTO> investList = new ArrayList<GenericDTO>();
		for (int i=0;i<rows.size();i++){
			Element aRow =rows.get(i);
			GenericDTO unGenericDTO = new GenericDTO();
			Elements cells = aRow.getElementsByTag("td"); 
			unGenericDTO.add("user", cells.get(0).text()); 

			Elements userLink = cells.get(0).getElementsByTag("a");
			String userUrl = userLink.attr("href");
			String userId = extractFirstNumber(userUrl,UserAdapter.USER_PATH);
			unGenericDTO.add("userId", userId); 

			//unGenericDTO.add("1", cells.get(1).text()); titulo repetido 
			unGenericDTO.add("amount", cells.get(2).text()); 
			unGenericDTO.add("rate", cells.get(3).text()); 
			unGenericDTO.add("apr", cells.get(4).text()); 
			investList.add(unGenericDTO);
		}
		return investList;
	}
	private GenericDTO getApiLink(String url) {
		GenericDTO self = new GenericDTO();
		String id = extractFirstNumber(url, SITE_INVESTMENTS_PATH);
		self.add("rel", "self");
		self.add("href", INVESTMENTS_PATH + "/" + id);

		return self;

	}
}
