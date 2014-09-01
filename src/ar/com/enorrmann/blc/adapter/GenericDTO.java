package ar.com.enorrmann.blc.adapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GenericDTO {

	private Map<String, Object> atributos = new HashMap<String, Object>();

	public void add(String name, Object atributo) {
		this.atributos.put(name, atributo);
	}

	public Object get(String atributo) {
		return this.atributos.get(atributo);
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		final String QUOT = "\"";
		sb.append("{");
		if( this.atributos != null) {
			int count=0;
			for(String clave : (Set<String>) this.atributos.keySet()){
				Object valor = atributos.get(clave); 
				sb.append(QUOT);
				sb.append(clave);
				sb.append(QUOT);
				sb.append(":");
				if (!isString(valor)){
					sb.append(valor);
				} else {
					sb.append(QUOT);
					sb.append(valor);
					sb.append(QUOT);
				}

				if (++count<atributos.keySet().size()){
					sb.append(",");
				}
			}
			sb.append("}");
		}
		return sb.toString();
	}
	private boolean isString(Object o){
		return o instanceof String;
		
	}
}
