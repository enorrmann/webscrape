package ar.com.enorrmann.blc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import ar.com.enorrmann.blc.adapter.BlcAdapter;
import ar.com.enorrmann.blc.adapter.GenericDTO;
import ar.com.enorrmann.blc.adapter.HtmlAdapter;
import ar.com.enorrmann.blc.adapter.LoanAdapter;
import ar.com.enorrmann.blc.adapter.LoanInvestmentsAdapter;
import ar.com.enorrmann.blc.adapter.LoanPaymentsAdapter;
import ar.com.enorrmann.blc.adapter.UserAdapter;
import ar.com.enorrmann.blc.adapter.UserInvestmentsAdapter;
import ar.com.enorrmann.blc.adapter.UserLoansAdapter;
import ar.com.enorrmann.blc.web.WebSource;

import com.cedarsoftware.util.io.JsonReader;

public class Logic {

	public static final String BLC_BASE_URL = "https://bitlendingclub.com";

	public List<GenericDTO> getPayments(List<GenericDTO> loans) {
		List<GenericDTO> payments = new ArrayList<GenericDTO>();
		for (GenericDTO aLoan : loans) {
			payments.addAll((List<GenericDTO>) aLoan.get("payments"));
		}
		return payments;
	}

	public GenericDTO getInvestmentPage(Document doc) {
		HtmlAdapter adapter = new UserInvestmentsAdapter("", doc);
		GenericDTO page1 = adapter.getGenericDto();
		return page1;
	}

	public GenericDTO getInvestmentPage(Long userId, Long pageNum) {
		if (userId == null) {
			return getErrorDto("user id required");
		}
		if (pageNum == null) {
			return getErrorDto("parameter pageNum required");
		}
		Long pageNumber = pageNum != null ? pageNum : 1L;
		final String baseUrl = "https://bitlendingclub.com/user/get-investments-ajax/id/";
		String page1Url = baseUrl + userId + "/page/" + pageNumber;
		Document doc = getDocFromUrl(page1Url);
		HtmlAdapter adapter = new UserInvestmentsAdapter(page1Url, doc);
		GenericDTO page1 = adapter.getGenericDto();
		page1.add("pageNumber", pageNumber);
		return page1;
	}
	
	public List<GenericDTO> getInvestmentsAll(Long userId) {
		List<GenericDTO> dtoList = new ArrayList<GenericDTO>();
		if (userId == null) {
			dtoList.add(getErrorDto("user id required"));
			return dtoList;
		}
		int pageNumber = 1;
		final String baseUrl = "https://bitlendingclub.com/user/get-investments-ajax/id/"+ userId + "/page/";
		
		String page1Url = baseUrl + pageNumber;
		Document doc = getDocFromUrl(page1Url);
		
		HtmlAdapter adapter = new UserInvestmentsAdapter(page1Url, doc);
		GenericDTO page1 = adapter.getGenericDto();
		page1.add("pageNumber", pageNumber);
		dtoList.add(page1);
		
		int pageCount = (Integer) page1.get("pageCount");
		dtoList = (List<GenericDTO>)page1.get("investments");
		
		List<String> links = new ArrayList<String>();		
		for (int i=2;i<pageCount;i++){
			links.add(baseUrl + i);
		}
		List<Document> docs = getDocFromUrl(links.toArray(new String[0]));
		for (Document aDocument:docs){
			dtoList.addAll((List<GenericDTO>)getInvestmentPage(aDocument).get("investments"));
		}
		return dtoList;

	}

	public GenericDTO getUserLoans(Long userId, Long pageNum) {
		if (userId == null) {
			return getErrorDto("user id required");
		}
		if (pageNum == null) {
			return getErrorDto("parameter pageNum required");
		}

		String baseUrl = "https://bitlendingclub.com/user/get-loans-ajax/id/";
		String page1Url = baseUrl + userId + "/page/"+pageNum+"/1/true/2/true/3/true/4/true/5/true/6/true";
		Document doc = getDocFromUrl(page1Url);
		HtmlAdapter adapter = new UserLoansAdapter(page1Url, doc);

		GenericDTO page1 = adapter.getGenericDto();
		// int pageCount = (Integer) page1.get("pageCount");

		return page1;

	}

	private List<String> getInvestLinks(List<GenericDTO> pages) {
		final String BASE_URL = "https://bitlendingclub.com";
		List<String> linkList = new ArrayList<String>();
		for (GenericDTO aPage : pages) {
			List<GenericDTO> aPageLinks = (List<GenericDTO>) aPage
					.get("investments");
			for (GenericDTO aLink : aPageLinks) {
				linkList.add(BASE_URL + aLink.get("url").toString());
			}

		}
		return linkList;
	}

	public Document getDocFromUrl(String url){
		String [] urls = new String [1];
		urls[0] = url;
		return getDocFromUrl(urls).get(0);
	}
	public List<Document> getDocFromUrl(String [] urls){
		List<WebSource> threads = new ArrayList<WebSource>();
		List<Document> docs = new ArrayList<Document>();
		for (String url:urls){
			WebSource ws = new WebSource(url);
			threads.add(ws);
			ws.start();
		}
		for (WebSource aWebSource:threads){
			try {
				aWebSource.join();
				Document aDoc =Jsoup.parse(aWebSource.getSourceCode());
				aDoc.setBaseUri(aWebSource.getUrl());
				docs.add(aDoc);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return docs;
	}

	public GenericDTO getLoan(Long loanId) throws IOException {
		if (loanId == null) {
			return getErrorDto("loan id required");
		}
		final String url = "https://bitlendingclub.com/loan/browse/lid/" + loanId;
		Document doc = getDocFromUrl(url);
		
		InputStream is = getClass().getClassLoader().getResourceAsStream("ar/com/enorrmann/blc/adapter/LoanAdapter.json");
		JsonReader jr = new JsonReader(is, true);
		Map map = (Map) jr.readObject();

		HtmlAdapter adapter = new LoanAdapter(url, doc,map);
		GenericDTO loanDto = adapter.getGenericDto();

		return loanDto;
	}

	private GenericDTO getErrorDto(String message) {
		GenericDTO errorDto = new GenericDTO();
		errorDto.add("message", message);
		return errorDto;
	}

	public List<GenericDTO> getLoansFromLinks(List<String> links) {
		System.out.println("getLoansFromLinks ");
		HtmlAdapter adapter = new BlcAdapter();
		GenericDTO aLoan = null;
		List<GenericDTO> loans = new ArrayList<GenericDTO>();
		for (String url : links) {
			Document doc = getDocFromUrl(url);
			aLoan = adapter.getGenericDto();
			loans.add(aLoan);
		}
		return loans;
	}

	public List<GenericDTO> getLoanInvestments(Long loanId) {
		final String url = "https://bitlendingclub.com/loan/browse/lid/"+ loanId;
		Document doc = getDocFromUrl(url);
		HtmlAdapter adapter = new LoanInvestmentsAdapter(url, doc);
		GenericDTO loanInvestmentsDto = adapter.getGenericDto();
		List<GenericDTO> investments = (List<GenericDTO>) loanInvestmentsDto
				.get("investments");
		if (investments == null) {
			investments = new ArrayList<GenericDTO>();
		}
		return investments;
	}
	
	public List<GenericDTO> getUserReceivables(Long userId){
		List<GenericDTO> investments = getInvestmentsAll(userId);
		keep(investments, "loanStatus","Funded");
		List<GenericDTO> payments = getLoanPayments(investments);
		keep(payments, "status","Pending");
		return payments;
		
	}
	
	private void keep(List<GenericDTO> investmentsAll, String attrKey,String attrVal) {
		Iterator<GenericDTO> it = investmentsAll.iterator();
		while (it.hasNext()){
			GenericDTO anInvestment = it.next();
			String value =(String) anInvestment.get(attrKey); 
			if (value!=null&&!value.equals(attrVal)){
				it.remove();
			}
		}
	}

	public List<GenericDTO> getLoanPayments(List<GenericDTO> loans) {
		List<String> links = new ArrayList<String>();		
		for (GenericDTO aLoan:loans){
			links.add(BLC_BASE_URL + aLoan.get("url"));
		}
		List<Document> docs = getDocFromUrl(links.toArray(new String[0]));
		List<GenericDTO> payments = new ArrayList<GenericDTO>(); 
		for (Document aDocument:docs){
			payments.addAll((List<GenericDTO>)getLoanPayments(aDocument));
		}
		return payments;

		
	}
	public List<GenericDTO> getLoanPayments(Long loanId) {
		final String url = "https://bitlendingclub.com/loan/browse/lid/"+ loanId;
		Document doc = getDocFromUrl(url);
		HtmlAdapter adapter = new LoanPaymentsAdapter(url, doc);
		GenericDTO loanInvestmentsDto = adapter.getGenericDto();
		List<GenericDTO> payments = (List<GenericDTO>) loanInvestmentsDto.get("payments");
		if (payments == null) {
			payments = new ArrayList<GenericDTO>();
		}
		return payments;
	}
	public List<GenericDTO> getLoanPayments(Document doc) {
		HtmlAdapter adapter = new LoanPaymentsAdapter("", doc);
		GenericDTO loanInvestmentsDto = adapter.getGenericDto();
		List<GenericDTO> payments = (List<GenericDTO>) loanInvestmentsDto.get("payments");
		if (payments == null) {
			payments = new ArrayList<GenericDTO>();
		}
		return payments;
	}

	public GenericDTO getUser(Long userId) throws IOException {
		if (userId == null) {
			return getErrorDto("user id required");
		}
		final String url = "https://bitlendingclub.com/user/index/id/" + userId;

		InputStream is = getClass().getClassLoader().getResourceAsStream("ar/com/enorrmann/blc/adapter/UserAdapter.json");
		JsonReader jr = new JsonReader(is, true);
		Map map = (Map) jr.readObject();


		Document doc = getDocFromUrl(url);
		HtmlAdapter adapter = new UserAdapter(url, doc, map);
		GenericDTO loanDto = adapter.getGenericDto();

		return loanDto;
	}

	public GenericDTO getApiDto() {
		List<GenericDTO> links = new ArrayList<GenericDTO>();
		GenericDTO apiDto = new GenericDTO();
		GenericDTO users = new GenericDTO();
		users.add("href", UserAdapter.USER_PATH);

		GenericDTO loans = new GenericDTO();
		loans.add("href", LoanAdapter.LOAN_PATH);

		links.add(users);
		links.add(loans);

		apiDto.add("links", links);

		return apiDto;
	}
}
