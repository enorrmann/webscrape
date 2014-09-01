package ar.com.enorrmann.blc.adapter;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public abstract class HtmlAdapter {

	protected GenericDTO genericDto = new GenericDTO();

	protected String getFirstMatchingvalue(Document doc, String selector) {
		String returnValue = "";
		Elements elements = doc.select(selector);
		if (!elements.isEmpty()) {
			returnValue = elements.first().text();
		}
		return returnValue;
	}

	protected String extractFirstNumber(String fullUrl, String beginFrom) {
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

	public GenericDTO getGenericDto() {
		return genericDto;
	}
}
