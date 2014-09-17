package ar.com.enorrmann.blc.adapter;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UserLoansAdapter extends HtmlAdapter {

	final static String INVESTMENTS_PATH = "/investments";
	public static final String SITE_INVESTMENTS_PATH = "/loan/browse/lid/";
	
	public UserLoansAdapter(String url, Document doc) {
//		unGenericDTO.add("url", url);
		
		Elements pages = doc.select("tfoot li");
		int pageCount = pages.size()==0?pages.size():pages.size()-2;
		genericDto.add("pageCount", pageCount);

		Elements rows = doc.select("tbody tr");
		genericDto.add("loans", getLoans(rows));
	}

	private List<GenericDTO> getLoans(Elements rows) {
		List<GenericDTO> loans = new ArrayList<GenericDTO>();
		// no loans
		if (rows.size()==1) {
			return loans;
		}
		for (int i=0;i<rows.size();i++){
			Element aRow =rows.get(i);
			GenericDTO unGenericDTO = new GenericDTO();
			Elements cells = aRow.getElementsByTag("td");
			Elements loanLink = aRow.getElementsByTag("a");
			// unGenericDTO.add("dateInvested", cells.get(0).text()); // title 
			unGenericDTO.add("amount", cells.get(1).text());
			unGenericDTO.add("percentFunded", cells.get(2).text());
			unGenericDTO.add("term", cells.get(3).text());
			//unGenericDTO.add("investors", cells.get(4).text());
			unGenericDTO.add("loanStatus", cells.get(3).text());
			unGenericDTO.add("title", loanLink.attr("title"));
			unGenericDTO.add("url", loanLink.attr("href"));
			unGenericDTO.add("link", getApiLink(loanLink.attr("href")));
			
			loans.add(unGenericDTO);
		}
		return loans;
	}
	
	private GenericDTO getApiLink(String url){
		GenericDTO self = new GenericDTO();
		String id = extractFirstNumber(url,SITE_INVESTMENTS_PATH);
		self.add("rel", "self");
		self.add("href", LoanAdapter.LOAN_PATH+"/"+id);
		
		return self;
		
	}

}
