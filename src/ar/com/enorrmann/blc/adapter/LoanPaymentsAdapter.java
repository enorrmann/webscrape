package ar.com.enorrmann.blc.adapter;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ar.com.enorrmann.blc.util.BlcCalculator;

public class LoanPaymentsAdapter extends HtmlAdapter {

	final static String INVESTMENTS_PATH = "/investments";
	final String SITE_INVESTMENTS_PATH = "/loan/browse/lid/";

	public LoanPaymentsAdapter(String url, Document doc,Long userId) {

		genericDto.add("url", url);

		Element paymentsTable = doc.select("table.text-center > tbody:nth-child(3)").first();
		if (paymentsTable==null)return ;
		Elements rows = paymentsTable.getElementsByTag("tr"); 
		
		String eachPayment = "";
		GenericDTO investmentDto = getInvestmentDto(doc,userId);
		if (investmentDto!=null){
			String investedAmount = (String)investmentDto.get("amount");
			String rate = (String)investmentDto.get("rate");
			int numPayments = rows.size()-1;
			 eachPayment = BlcCalculator.getEachPaymentAmount(investedAmount, rate, numPayments);
			investmentDto.add("investedAmount", investedAmount);
			investmentDto.add("rate", rate);
			investmentDto.add("numPayments", numPayments);
			investmentDto.add("eachPayment",eachPayment);
		}

		List<GenericDTO> payments = getPayments(rows,doc.baseUri(),eachPayment);
		genericDto.add("payments", payments);

	}

	private List<GenericDTO> getPayments(Elements rows,String baseUri,String eachPayment) {
		List<GenericDTO> investList = new ArrayList<GenericDTO>();
		// last value is just a sum
		for (int i=0;i<rows.size()-1;i++){
			Element aRow =rows.get(i);
			GenericDTO unGenericDTO = new GenericDTO();
			Elements cells = aRow.getElementsByTag("td"); 
			unGenericDTO.add("url", baseUri); 
			unGenericDTO.add("number", cells.get(0).text()); 
			unGenericDTO.add("dueDate", cells.get(1).text());
			String totalPayment = cells.get(5).text();
			unGenericDTO.add("totalPayment", totalPayment);
			unGenericDTO.add("start", getStartDate(cells.get(1).text()));
			unGenericDTO.add("title", getTitle(totalPayment,eachPayment,baseUri));
			unGenericDTO.add("description", getTitle("","",baseUri));
			unGenericDTO.add("amount", eachPayment);
			
			String datePosted = cells.get(2).text();
			unGenericDTO.add("datePosted", datePosted);
			boolean paid = datePosted!=null&&!datePosted.equals("- - -");
			String statusString = paid?"Paid":"Pending";
			String colorString = paid?"green":"gray";
			unGenericDTO.add("status", statusString);
			unGenericDTO.add("color", colorString);
			investList.add(unGenericDTO);
		}
		return investList;
	}
	private String getTitle(String totalPayment, String eachPayment, String baseUri) {
		String title =  baseUri.substring(baseUri.lastIndexOf("/")+1);
		title =  title.replaceAll("-", " " );
		if (eachPayment!=null&&!eachPayment.trim().isEmpty()){
			title = "("+eachPayment+") " + title;
		}else if (totalPayment!=null&&!totalPayment.trim().isEmpty()){
			title = "("+totalPayment+") " + title;
		}
		return title;
	}
	private GenericDTO getApiLink(String url) {
		GenericDTO self = new GenericDTO();
		String id = extractFirstNumber(url, SITE_INVESTMENTS_PATH);
		self.add("rel", "self");
		self.add("href", INVESTMENTS_PATH + "/" + id);

		return self;

	}

	private GenericDTO getInvestmentDto(Document doc, Long userId) {
		if (userId==null)return null;
		List<GenericDTO> investments = (List<GenericDTO>)new LoanInvestmentsAdapter("",doc).getGenericDto().get("investments");
		if (investments==null)return null;
		for (GenericDTO anInvestment:investments){
			if (anInvestment.get("userId").equals(userId+"")){
				return anInvestment;
			}
		}
		return null;
	}
	
	private String getStartDate(String dueDate){
		// server time
		if (dueDate.indexOf("CDT")>0){
			final String timezone = ":00-06:00";
			return dueDate = dueDate.replaceFirst(" ", "T").replaceFirst(" CDT", timezone);
		} else if (dueDate.indexOf("CST")>0){
			final String timezone = ":00-05:00";
			return dueDate = dueDate.replaceFirst(" ", "T").replaceFirst(" CST", timezone);
		}
		return "";

	}
}
