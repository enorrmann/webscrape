package ar.com.enorrmann.blc.adapter;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UserInvestmentsAdapter extends HtmlAdapter {

	public final static String INVESTMENTS_PATH = "/investments";
	final String SITE_INVESTMENTS_PATH = "/loan/browse/lid/";
	
	public UserInvestmentsAdapter(String url, Document doc) {
		genericDto.add("url", doc.baseUri());
		
		Elements pages = doc.select("tfoot li");
		int pageCount = pages.size()==0?pages.size():pages.size()-2;
		genericDto.add("pageCount", pageCount);

		Elements rows = doc.select("tbody tr");
		genericDto.add("investments", getInvestments(rows));
	}

	private List<GenericDTO> getInvestments(Elements rows) {
		List<GenericDTO> investList = new ArrayList<GenericDTO>();
		// no investments
		if (rows.size()==1) {
			return investList;
		}

		for (int i=0;i<rows.size();i++){
			Element aRow =rows.get(i);
			GenericDTO unGenericDTO = new GenericDTO();
			Elements cells = aRow.getElementsByTag("td"); 
			Elements loanLink = aRow.getElementsByTag("a");
			unGenericDTO.add("dateInvested", cells.get(0).text()); 
			unGenericDTO.add("amount", cells.get(2).text());
			unGenericDTO.add("rate", cells.get(3).text());
			unGenericDTO.add("apr", cells.get(4).text());
			unGenericDTO.add("loanStatus", cells.get(5).text());
			unGenericDTO.add("title", loanLink.attr("title"));
			unGenericDTO.add("url", loanLink.attr("href"));
			unGenericDTO.add("links", getApiLink(loanLink.attr("href")));
			
			investList.add(unGenericDTO);
		}
		return investList;
	}
	
	private GenericDTO getApiLink(String url){
		GenericDTO self = new GenericDTO();
		String id = extractFirstNumber(url,SITE_INVESTMENTS_PATH);
		self.add("rel", "self");
		self.add("href", LoanAdapter.LOAN_PATH+"/"+id);
		
		return self;
		
	}
}
