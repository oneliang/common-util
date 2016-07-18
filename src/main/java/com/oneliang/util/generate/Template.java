package com.oneliang.util.generate;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.oneliang.Constant;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.FileUtil;
import com.oneliang.util.json.JsonUtil;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class Template {

	private static final Logger logger=LoggerManager.getLogger(Template.class);
	private static final ScriptEngine scriptEngine;
	static{
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		scriptEngine = scriptEngineManager.getEngineByExtension("js");
	}


	public void generate(TemplateParameter templateParameter){
		if(templateParameter!=null){
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(templateParameter.getTemplateFile()));
				StringBuilder stringBuilder = new StringBuilder();
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
					stringBuilder.append(StringUtil.CRLF_STRING);
				}
				bufferedReader.close();
				Bindings bindings = scriptEngine.createBindings();
				scriptEngine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
				String json=null;
				if(templateParameter.getObject()!=null){
					json=JsonUtil.objectToJson(templateParameter.getObject());
				}else{
					if(StringUtil.isNotBlank(templateParameter.getJson())){
						json=templateParameter.getJson();
					}
				}
				scriptEngine.eval(JavaScriptFunctionGenerator.getObject(json));
				scriptEngine.eval(JavaScriptFunctionGenerator.template());
				Invocable invocable = (Invocable) scriptEngine;
				logger.debug(stringBuilder.toString());
				Object object = invocable.invokeFunction(JavaScriptFunctionGenerator.FUNCTION_TEMPLATE, stringBuilder.toString());
				logger.debug(JavaScriptFunctionGenerator.getResult(object.toString()));
				scriptEngine.eval(JavaScriptFunctionGenerator.getResult(object.toString()));
				object = invocable.invokeFunction(JavaScriptFunctionGenerator.FUNCTION_GET_RESULT);
				logger.debug(object);
				if(object!=null){
					String toFile=templateParameter.getToFile();
					FileUtil.createFile(toFile);
					OutputStream outputStream=new FileOutputStream(toFile);
					outputStream.write(object.toString().getBytes(Constant.Encoding.UTF8));
					outputStream.flush();
					outputStream.close();
				}
			} catch (Exception e) {
				logger.error(Constant.Base.EXCEPTION, e);
			}
		}
	}

	public static class TemplateParameter{
		private String templateFile=null;
		private String toFile=null;
		private Object object=null;
		private String json=null;
		/**
		 * @return the templateFile
		 */
		public String getTemplateFile() {
			return templateFile;
		}
		/**
		 * @param templateFile the templateFile to set
		 */
		public void setTemplateFile(String templateFile) {
			this.templateFile = templateFile;
		}
		/**
		 * @return the toFile
		 */
		public String getToFile() {
			return toFile;
		}
		/**
		 * @param toFile the toFile to set
		 */
		public void setToFile(String toFile) {
			this.toFile = toFile;
		}
		/**
		 * @return the object
		 */
		public Object getObject() {
			return object;
		}
		/**
		 * @param object the object to set
		 */
		public void setObject(Object object) {
			this.object = object;
		}
		/**
		 * @return the json
		 */
		public String getJson() {
			return json;
		}
		/**
		 * @param json the json to set
		 */
		public void setJson(String json) {
			this.json = json;
		}
	}

	private static class JavaScriptFunctionGenerator{
		public static final String FUNCTION_TEMPLATE="template";
		public static final String FUNCTION_GET_OBJECT="getObject";
		public static final String FUNCTION_GET_RESULT="getResult";
		public static String template(){
			StringBuilder stringBuilder=new StringBuilder();
			stringBuilder.append("function "+FUNCTION_TEMPLATE+"(string){");
			stringBuilder.append("string=string.replace(/[\\r\\t\\n]/g, \" \");");
			stringBuilder.append("string=string.split(\"<%\").join(\"\\t\");");
			stringBuilder.append("string=string.replace(/((^|%>)[^\\t]*)'/g, \"$1\\r\");");
			stringBuilder.append("string=string.replace(/\\t=(.*?)%>/g, \"',$1,'\");");
			stringBuilder.append("string=string.split(\"\\t\").join(\"');\");");
			stringBuilder.append("string=string.split(\"%>\").join(\"p.push('\");");
			stringBuilder.append("string=string.split(\"\\r\").join(\"\\'\");");
			stringBuilder.append("return \"var p=[];var object="+FUNCTION_GET_OBJECT+"();if(object!==null){with(object){p.push('\" +string+ \"');}}return p.join('');\"");
			stringBuilder.append("}");
			return stringBuilder.toString();
		}
		public static String getObject(String json){
			StringBuilder stringBuilder=new StringBuilder();
			stringBuilder.append("function "+FUNCTION_GET_OBJECT+"(){");
			stringBuilder.append("var object="+json+";");
			stringBuilder.append("return object;");
			stringBuilder.append("}");
			return stringBuilder.toString();
		}
		public static String getResult(String string){
			StringBuilder stringBuilder=new StringBuilder();
			stringBuilder.append("function "+FUNCTION_GET_RESULT+"(){");
			stringBuilder.append(string);
			stringBuilder.append("}");
			return stringBuilder.toString();
		}
	}
}
