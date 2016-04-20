package com.oneliang.util.jxl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class JxlMappingBean{

	public static final String TAG_BEAN="bean";
	public static final String USE_FOR_IMPORT="import";
	public static final String USE_FOR_EXPORT="export";
	
	private String useFor=USE_FOR_IMPORT;
	private String type=null;
	private List<JxlMappingColumnBean> jxlMappingColumnBeanList=new CopyOnWriteArrayList<JxlMappingColumnBean>();
	
	/**
	 * get header
	 * @param field
	 * @return header
	 */
	public String getHeader(String field){
		String header=null;
		if(field!=null){
			for(JxlMappingColumnBean jxlMappingColumnBean:jxlMappingColumnBeanList){
				String columnField=jxlMappingColumnBean.getField();
				if(columnField!=null&&columnField.equals(field)){
					header=jxlMappingColumnBean.getHeader();
					break;
				}
			}
		}
		return header;
	}
	
	/**
	 * get field
	 * @param header
	 * @return field
	 */
	public String getField(String header){
		String field=null;
		if(header!=null){
			for(JxlMappingColumnBean jxlMappingColumnBean:jxlMappingColumnBeanList){
				String columnHeader=jxlMappingColumnBean.getHeader();
				if(columnHeader!=null&&columnHeader.equals(header)){
					field=jxlMappingColumnBean.getField();
					break;
				}
			}
		}
		return field;
	}

	/**
	 * get index
	 * @param field
	 * @return field index
	 */
	public int getIndex(String field){
		int index=-1;
		if(field!=null){
			for(JxlMappingColumnBean jxlMappingColumnBean:jxlMappingColumnBeanList){
				String columnField=jxlMappingColumnBean.getField();
				if(columnField!=null&&columnField.equals(field)){
					index=jxlMappingColumnBean.getIndex();
					break;
				}
			}
		}
		return index;
	}
	/**
	 * @return the useFor
	 */
	public String getUseFor() {
		return useFor;
	}

	/**
	 * @param useFor the useFor to set
	 */
	public void setUseFor(String useFor) {
		this.useFor = useFor;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param jxlMappingColumnBean
	 * @return boolean
	 */
	public boolean addJxlMappingColumnBean(JxlMappingColumnBean jxlMappingColumnBean){
		return this.jxlMappingColumnBeanList.add(jxlMappingColumnBean);
	}
	
	/**
	 * @return the jxlMappingColumnBeanList
	 */
	public List<JxlMappingColumnBean> getJxlMappingColumnBeanList() {
		return jxlMappingColumnBeanList;
	}
}
