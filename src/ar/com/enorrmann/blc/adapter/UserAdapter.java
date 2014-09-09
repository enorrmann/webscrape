package ar.com.enorrmann.blc.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

public class UserAdapter extends GenericAdapter {

	public final static String USER_PATH = "/users";
	final String PAYMENTS_PATH = "/payments";
	final String RATINGS_PATH = "/ratings";
	final String RECEIVABLES_PATH = "/receivables";
	final String PAYABLES_PATH = "/payables";
	final String CALENDAR_PATH = "/ratings";
	public final String SITE_USER_PATH = "/user/index/id/";

	public UserAdapter(String url, Document doc, Map<String, Object> adapterMap) {
		super(url, doc, adapterMap);
		genericDto.add("links", getLinks(url));
	}

	
	private List<GenericDTO> getLinks(String url){
		List<GenericDTO> linksDto = new ArrayList<GenericDTO>();
		
		GenericDTO self = new GenericDTO();
		String userId = extractFirstNumber(url,SITE_USER_PATH);
		self.add("rel", "self");
		self.add("href", USER_PATH+"/"+userId);
		linksDto.add(self);
		
		GenericDTO investmentsLink = new GenericDTO();
		investmentsLink.add("rel", "collection/investments");
		investmentsLink.add("href", USER_PATH+"/"+userId+UserInvestmentsAdapter.INVESTMENTS_PATH);
		linksDto.add(investmentsLink);
		
		
		GenericDTO paymentsLink = new GenericDTO();
		paymentsLink.add("rel", "collection/loans");
		paymentsLink.add("href", USER_PATH+"/"+userId+LoanAdapter.LOAN_PATH);
		linksDto.add(paymentsLink);
		
		GenericDTO receivablesLink = new GenericDTO();
		receivablesLink.add("rel", "collection/receivables");
		receivablesLink.add("href", USER_PATH+"/"+userId+RECEIVABLES_PATH);
		linksDto.add(receivablesLink);
		
		GenericDTO payablesLink = new GenericDTO();
		payablesLink.add("rel", "collection/receivables");
		payablesLink.add("href", USER_PATH+"/"+userId+PAYABLES_PATH);
		linksDto.add(payablesLink);
		
		return linksDto;
		
	}
	
}
