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

		Element paymentsTable = doc.select("table.text-center > tbody:nth-child(3)").first();
		if (paymentsTable==null)return ;
		Elements rows = paymentsTable.getElementsByTag("tr"); 
		GenericDTO investmentDto = getInvestmentDto(doc,"1054");

		List<GenericDTO> payments = getPayments(rows,doc.baseUri(),investmentDto);
		genericDto.add("payments", payments);

	}

	private List<GenericDTO> getPayments(Elements rows,String baseUri,GenericDTO investmentDto) {
		List<GenericDTO> investList = new ArrayList<GenericDTO>();
		// last value is just a sum
		for (int i=0;i<rows.size()-1;i++){
			Element aRow =rows.get(i);
			GenericDTO unGenericDTO = new GenericDTO();
			Elements cells = aRow.getElementsByTag("td"); 
			unGenericDTO.add("url", baseUri); 
			unGenericDTO.add("number", cells.get(0).text()); 
			unGenericDTO.add("dueDate", cells.get(1).text());
			unGenericDTO.add("start", getStartDate(cells.get(1).text()));
			unGenericDTO.add("title", getTitle(baseUri));
			
			String datePosted = cells.get(2).text();
			unGenericDTO.add("datePosted", datePosted);
			if (investmentDto!=null){
				unGenericDTO.add("investedAmount", investmentDto.get("amount"));
				unGenericDTO.add("rate", investmentDto.get("rate"));
				unGenericDTO.add("numPayments", rows.size()-1);
			}
			
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

	private GenericDTO getInvestmentDto(Document doc, String userId) {
		List<GenericDTO> investments = (List<GenericDTO>)new LoanInvestmentsAdapter("",doc).getGenericDto().get("investments");
		for (GenericDTO anInvestment:investments){
			if (anInvestment.get("userId").equals(userId)){
				return anInvestment;
			}
		}
		return null;
	}
	
	private String getStartDate(String dueDate){
		final String timezone = ":00-06:00";
		return dueDate = dueDate.replaceFirst(" ", "T").replaceFirst(" CDT", timezone);
	}
}
