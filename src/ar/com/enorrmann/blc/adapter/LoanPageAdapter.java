package ar.com.enorrmann.blc.adapter;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LoanPageAdapter extends HtmlAdapter {

	public LoanPageAdapter(String url, Document doc) {

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
			Elements loanLink = cells.get(1).getElementsByTag("a");
			String description = cells.get(1).text();
			String title = loanLink.attr("title");
			unGenericDTO.add("user", cells.get(0).text()); 
			unGenericDTO.add("description", title); 
			unGenericDTO.add("type", cells.get(2).text()); 
			unGenericDTO.add("percentFunded", cells.get(4).text()); 
			unGenericDTO.add("timeLeft", cells.get(6).text()); 
			unGenericDTO.add("reputation", cells.get(7).text()); 

			unGenericDTO.add("title", title);
			String url = loanLink.attr("href");
			//unGenericDTO.add("url", HtmlAdapter.BLC_BASE_URL+url);
			unGenericDTO.add("links",getApiLink(url));
			
			loans.add(unGenericDTO);
		}
		return loans;
	}
	private List<GenericDTO> getApiLink(String url){
		List<GenericDTO> links = new ArrayList<GenericDTO>();
		
		GenericDTO self = new GenericDTO();
		String id = extractFirstNumber(url,LoanAdapter.SITE_LOAN_PATH);
		self.add("rel", "self");
		self.add("href", LoanAdapter.LOAN_PATH+"/"+id);

		GenericDTO urlDto = new GenericDTO();
		urlDto.add("rel", "url");
		urlDto.add("href", HtmlAdapter.BLC_BASE_URL+url);
		links.add(self);
		links.add(urlDto);
		return links;
		
	}
}
