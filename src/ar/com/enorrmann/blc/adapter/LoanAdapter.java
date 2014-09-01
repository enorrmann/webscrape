package ar.com.enorrmann.blc.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

public class LoanAdapter extends GenericAdapter {

	public final static String LOAN_PATH = "/loans";
	final String PAYMENTS_PATH = "/payments";
	final String COMMENTS_PATH = "/comments";
	final String SITE_LOAN_PATH = "/loan/browse/lid/";

	public LoanAdapter(String url, Document doc, Map<String, Object> adapterMap) {
		super(url, doc, adapterMap);
		genericDto.add("links", getLinks(url));
	}


	private List<GenericDTO> getLinks(String url){
		List<GenericDTO> linksDto = new ArrayList<GenericDTO>();
		
		GenericDTO self = new GenericDTO();
		String loanId = extractFirstNumber(url,SITE_LOAN_PATH);
		self.add("rel", "self");
		self.add("href", LOAN_PATH+"/"+loanId);
		linksDto.add(self);
		
		GenericDTO investmentsLink = new GenericDTO();
		investmentsLink.add("rel", "collection/investments");
		investmentsLink.add("href", LOAN_PATH+"/"+loanId+UserInvestmentsAdapter.INVESTMENTS_PATH);
		linksDto.add(investmentsLink);
		
		GenericDTO commentsLink = new GenericDTO();
		commentsLink.add("rel", "collection/comments");
		commentsLink.add("href", LOAN_PATH+"/"+loanId+COMMENTS_PATH);
		linksDto.add(commentsLink);
		
		GenericDTO paymentsLink = new GenericDTO();
		paymentsLink.add("rel", "collection/payments");
		paymentsLink.add("href", LOAN_PATH+"/"+loanId+PAYMENTS_PATH);
		linksDto.add(paymentsLink);
		
		
		return linksDto;
		
	}
	
}