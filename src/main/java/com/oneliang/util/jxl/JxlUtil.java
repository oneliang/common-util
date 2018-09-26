package com.oneliang.util.jxl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.oneliang.Constants;
import com.oneliang.util.common.ObjectUtil;
import com.oneliang.util.common.StringUtil;

public final class JxlUtil{

	private static final JxlProcessor DEFAULT_JXL_PROCESSOR=new DefaultJxlProcessor();

	/**
	 * excel copy
	 * @param excelFile
	 * @param newExcelFile
	 * @param jxlProcessor
	 */
	public static <T extends Object> void excelCopy(String excelFile,String newExcelFile,JxlProcessor jxlProcessor,T object){
		try {
			Workbook workbook=Workbook.getWorkbook(new File(excelFile));
			WritableWorkbook writableWorkbook=Workbook.createWorkbook(new File(newExcelFile),workbook);
			Sheet[] sheets=writableWorkbook.getSheets();
			for(Sheet sheet:sheets){
				int columns=sheet.getColumns();
				for(int i=0;i<columns;i++){
					Cell[] cells=sheet.getColumn(i);
					for(Cell cell:cells){
						if(jxlProcessor!=null){
							jxlProcessor.copyingProcess(cell,object);
						}
					}
				}
			}
			writableWorkbook.write();
			writableWorkbook.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * simpleExcelImport
	 * @param <T>
	 * @param file
	 * @param clazz
	 * @param jxlMappingBean
	 * @param jxlProcessor
	 * @return List<T>
	 */
	public static <T extends Object> List<T> simpleExcelImport(String file,Class<T> clazz,JxlMappingBean jxlMappingBean,JxlProcessor jxlProcessor){
		return simpleExcelImport(file, clazz, jxlMappingBean, 0, jxlProcessor);
	}

	/**
	 * simpleExcelImport
	 * @param <T>
	 * @param file
	 * @param clazz
	 * @param jxlMappingBean
	 * @param offset
	 * @param jxlProcessor
	 * @return List<T>
	 */
	public static <T extends Object> List<T> simpleExcelImport(String file,Class<T> clazz,JxlMappingBean jxlMappingBean,int offset,JxlProcessor jxlProcessor){
		List<T> list=new ArrayList<T>();
		try{
			Workbook workbook=Workbook.getWorkbook(new File(file));
			Sheet[] sheets=workbook.getSheets();
			Sheet sheet=sheets.length>0?sheets[0]:null;
			if(sheet!=null){
				int rows=sheet.getRows();
				for(int i=offset;i<rows;i++){
					T object=clazz.newInstance();
					Method[] methods=clazz.getMethods();
					for (Method method:methods) {
						String methodName=method.getName();
						String fieldName=null;
						if(methodName.startsWith(Constants.Method.PREFIX_SET)){
							fieldName=ObjectUtil.methodNameToFieldName(Constants.Method.PREFIX_SET, methodName);
						}
						if(fieldName!=null){
							int columnIndex=jxlMappingBean.getIndex(fieldName);
							if(columnIndex>=0){
								Cell cell=sheet.getCell(columnIndex, i);
								Class<?>[] classes=method.getParameterTypes();
								if(classes.length==1){
									if(jxlProcessor!=null){
										Object value=jxlProcessor.importingProcess(classes[0],cell);
										method.invoke(object, value);
									}
								}
							}
						}
					}
					list.add(object);
				}
			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	/**
	 * simple excel export
	 * @param <T>
	 * @param iterable
	 * @param jxlMappingBean
	 * @param file
	 */
	public static <T extends Object> void simpleExcelExport(Iterable<T> iterable,JxlMappingBean jxlMappingBean,String file){
		simpleExcelExport(iterable, jxlMappingBean, file, DEFAULT_JXL_PROCESSOR);
	}

	/**
	 * simple excel export
	 * @param <T>
	 * @param iterable
	 * @param jxlMappingBean
	 * @param file
	 */
	public static <T extends Object> void simpleExcelExport(Iterable<T> iterable,JxlMappingBean jxlMappingBean,String file,JxlProcessor jxlProcessor){
		simpleExcelExport(null, iterable, jxlMappingBean, file, jxlProcessor);
	}

	/**
	 * simple excel export
	 * @param <T>
	 * @param headers
	 * @param iterable
	 * @param jxlMappingBean
	 * @param file
	 */
	public static <T extends Object> void simpleExcelExport(String[] headers,Iterable<T> iterable,JxlMappingBean jxlMappingBean,String file){
		simpleExcelExport(headers, iterable, jxlMappingBean, file, DEFAULT_JXL_PROCESSOR);
	}

	/**
	 * simple excel export
	 * @param <T>
	 * @param headers
	 * @param iterable
	 * @param jxlMappingBean
	 * @param file
	 */
	public static <T extends Object> void simpleExcelExport(String[] headers,Iterable<T> iterable,JxlMappingBean jxlMappingBean,String file,JxlProcessor jxlProcessor){
		if(headers==null||headers.length==0){
			List<JxlMappingColumnBean> jxlMappingColumnBeanList=jxlMappingBean.getJxlMappingColumnBeanList();
			int max=0;
			for(JxlMappingColumnBean jxlMappingColumnBean:jxlMappingColumnBeanList){
				if(max<=jxlMappingColumnBean.getIndex()){
					max=jxlMappingColumnBean.getIndex();
				}
			}
			headers=new String[max+1];
			for(JxlMappingColumnBean jxlMappingColumnBean:jxlMappingColumnBeanList){
				headers[jxlMappingColumnBean.getIndex()]=StringUtil.nullToBlank(jxlMappingColumnBean.getHeader());
			}
		}
		try{
			WritableWorkbook writableWorkbook=Workbook.createWorkbook(new File(file));
			WritableSheet sheet=writableWorkbook.createSheet("sheet", 0);
			int row=0;
			if(headers!=null&&headers.length>0){
				int column=0;
				for(String header:headers){
					WritableCell cell=new Label(column,row,header);
					sheet.addCell(cell);
					column++;
				}
				row++;
			}
			if(iterable!=null&&jxlMappingBean!=null){
				List<JxlMappingColumnBean> jxlMappingColumnBeanList=jxlMappingBean.getJxlMappingColumnBeanList();
				int currentRow=row;
				for(T object:iterable){
					for(JxlMappingColumnBean jxlMappingColumnBean:jxlMappingColumnBeanList){
						String fieldName=jxlMappingColumnBean.getField();
						int columnIndex=jxlMappingColumnBean.getIndex();
						Object methodReturnValue=ObjectUtil.getterOrIsMethodInvoke(object, fieldName);
						if(jxlProcessor!=null){
							methodReturnValue=jxlProcessor.exportingProcess(fieldName,methodReturnValue);
						}
						WritableCell cell=new Label(columnIndex,currentRow,methodReturnValue==null?StringUtil.BLANK:methodReturnValue.toString());
						sheet.addCell(cell);
					}
					currentRow++;
				}
			}
			writableWorkbook.write();
			writableWorkbook.close();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public abstract interface JxlProcessor{

		/**
		 * copying process
		 * @param <T>
		 * @param cell
		 * @param object
		 */
		public abstract <T extends Object> void copyingProcess(Cell cell,T object);
		
		/**
		 * importing process
		 * @param <T>
		 * @param parameterClass
		 * @param cell
		 * @return Object
		 */
		public abstract <T extends Object> Object importingProcess(Class<?> parameterClass,Cell cell);

		/**
		 * exporting process
		 * @param <T>
		 * @param fieldName
		 * @param value
		 * @return String
		 */
		public abstract <T extends Object> String exportingProcess(final String fieldName, final Object value);
	}
}
