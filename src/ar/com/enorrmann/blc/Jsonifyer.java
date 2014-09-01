package ar.com.enorrmann.blc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cedarsoftware.util.io.JsonWriter;

public class Jsonifyer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Logic logic = new Logic();
	final String USERS_PATH = "/users";
	final String PAYMENTS_PATH = "/payments";
	final String LOANS_PATH = "/loans";
	final String INVESTMENTS_PATH = "/investments";
	final String RATINGS_PATH = "/ratings";
	final String RECEIVABLES_PATH = "/receivables";
	final String BIG_PATH = "/big";

	public Jsonifyer() {
		super();
	}

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");

		// full url, like http://localhost:8080/blcExtractorWeb/asJson
		final String requestUrl = request.getRequestURL().toString();

		if (urlContainsPath(requestUrl, USERS_PATH)) {
			processUsers(request, response, requestUrl);
		} else if (urlContainsPath(requestUrl, LOANS_PATH)) {
			processLoan(request, response, requestUrl);
		} else {
			showApiLinks(request, response);
		}

	}

	private void showApiLinks(HttpServletRequest request,HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		String json = logic.getApiDto().toString();
		out.println(JsonWriter.formatJson(json));
		
	}

	private void processUsers(HttpServletRequest request, HttpServletResponse response, final String requestUrl) throws IOException {
		PrintWriter out = response.getWriter();
		String userid = extractFirstNumber(requestUrl, USERS_PATH);
		String pageNum = request.getParameter("pageNum");

		Long pageNumber = asLong(pageNum);
		
		if (urlContainsPath(requestUrl, INVESTMENTS_PATH)) {
			String json = null;
			if (pageNumber!=null){
				 json = logic.getInvestmentPage(asLong(userid), pageNumber).toString();
			} else {
				 json = logic.getInvestmentsAll(asLong(userid)).toString();
			}
			out.println(JsonWriter.formatJson(json));
		} else if (urlContainsPath(requestUrl, RECEIVABLES_PATH)) {
			String json = logic.getUserReceivables(asLong(userid)).toString();
			out.println(JsonWriter.formatJson(json));
		} else if (urlContainsPath(requestUrl, LOANS_PATH)) {
			String json = logic.getUserLoans(asLong(userid), pageNumber).toString();
			out.println(JsonWriter.formatJson(json));
		} else{
			String json = logic.getUser(asLong(userid)).toString();
			out.println(JsonWriter.formatJson(json));
		}
	}

	private void processLoan(HttpServletRequest request, HttpServletResponse response, final String requestUrl) throws IOException {
		PrintWriter out = response.getWriter();
		String loanId = extractFirstNumber(requestUrl, LOANS_PATH);

		if (urlContainsPath(requestUrl, INVESTMENTS_PATH)) {
			String json = logic.getLoanInvestments(asLong(loanId)).toString();
			out.println(JsonWriter.formatJson(json));
		}else if (urlContainsPath(requestUrl, PAYMENTS_PATH)) {
			String json = logic.getLoanPayments(asLong(loanId)).toString();
			out.println(JsonWriter.formatJson(json));
		}else if (urlContainsPath(requestUrl, BIG_PATH)) {
			String json = logic.getBigLoans().toString();
			out.println(JsonWriter.formatJson(json));
		} else {
			if (asLong(loanId)!=null){
				String json = logic.getLoan(asLong(loanId)).toString();
				out.println(JsonWriter.formatJson(json));
			} else {
				String json = logic.getLoansAll().toString();
				out.println(JsonWriter.formatJson(json));
			}
		}
	}

	private Long asLong(String text) {
		Long num = null;
		try {
			num = Long.valueOf(text);
		} catch (Exception e) {

		}
		return num;
	}

	private boolean urlContainsPath(String url, String path) {
		if (url == null || path == null) {
			return false;
		}
		return url.indexOf(path) >= 0;
	}

	private String extractFirstNumber(String fullUrl, String beginFrom) {
		String text = fullUrl.substring(fullUrl.indexOf(beginFrom)
				+ beginFrom.length());
		StringBuilder sb = new StringBuilder();
		boolean found = false;
		for (char c : text.toCharArray()) {
			if (Character.isDigit(c)) {
				sb.append(c);
				found = true;
			} else {
				if (found) {
					break;
				}
			}

		}
		return sb.toString();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
