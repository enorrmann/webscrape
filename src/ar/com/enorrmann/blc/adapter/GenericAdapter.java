package ar.com.enorrmann.blc.adapter;

import java.util.Map;

import org.jsoup.nodes.Document;

public class GenericAdapter extends HtmlAdapter {
	
	public GenericAdapter(String url, Document doc,Map<String, Object> adapterMap) {
		genericDto.add("url", url);
		for (String aKey : adapterMap.keySet()) {
			Object value = adapterMap.get(aKey);
			if (value instanceof String){
				genericDto.add(aKey,getFirstMatchingvalue(doc, (String)adapterMap.get(aKey)));
//			} else if (value instanceof Map){
//				genericDto.add(aKey,getGenericDTO((Map)adapterMap.get(aKey)));
			}
		}
	}
	
	private GenericDTO getGenericDTO(Map<String,Object> map){
		GenericDTO childDto = new GenericDTO();
		for (String aKey : map.keySet()) {
			Object value = map.get(aKey);
			if (value instanceof String){
				childDto.add(aKey, value);
			} else if (value instanceof Map){
				childDto.add(aKey, getGenericDTO((Map)value));
			} else if (value instanceof Object[]){
				for (Object anObject:(Object[])value){ 
					childDto.add(aKey, getGenericDTO((Map)anObject));
				}
			}
		}
		return childDto;
	}
	
	
}
