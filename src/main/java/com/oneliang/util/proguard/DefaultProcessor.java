package com.oneliang.util.proguard;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.oneliang.util.proguard.Retrace.FieldInfo;
import com.oneliang.util.proguard.Retrace.MethodInfo;
import com.oneliang.util.proguard.Retrace.Processor;

public class DefaultProcessor implements Processor {

	public Map<String, String> classMap = new HashMap<String, String>();
	public Map<String, Map<String, Set<FieldInfo>>> classFieldMap = new HashMap<String, Map<String, Set<FieldInfo>>>();
	public Map<String, Map<String, Set<MethodInfo>>> classMethodMap = new HashMap<String, Map<String, Set<MethodInfo>>>();

	/**
	 * process class mapping
	 * 
	 * @param className
	 * @param newClassName
	 */
	public void processClassMapping(String className, String newClassName) {
		this.classMap.put(newClassName, className);
	}

	/**
	 * process field mapping
	 * 
	 * @param className
	 * @param fieldType
	 * @param fieldName
	 * @param newFieldName
	 */
	public void processFieldMapping(String className, String fieldType, String fieldName, String newFieldName) {
		// Original class name -> obfuscated field names.
		Map<String, Set<FieldInfo>> fieldMap = this.classFieldMap.get(className);
		if (fieldMap == null) {
			fieldMap = new HashMap<String, Set<FieldInfo>>();
			this.classFieldMap.put(className, fieldMap);
		}

		// Obfuscated field name -> fields.
		Set<FieldInfo> fieldSet = fieldMap.get(newFieldName);
		if (fieldSet == null) {
			fieldSet = new LinkedHashSet<FieldInfo>();
			fieldMap.put(newFieldName, fieldSet);
		}

		// Add the field information.
		fieldSet.add(new FieldInfo(fieldType, fieldName));
	}

	/**
	 * process method mapping
	 * 
	 * @param className
	 * @param firstLineNumber
	 * @param lastLineNumber
	 * @param methodReturnType
	 * @param methodName
	 * @param methodArguments
	 * @param newMethodName
	 */
	public void processMethodMapping(String className, int firstLineNumber, int lastLineNumber, String methodReturnType, String methodName, String methodArguments, String newMethodName) {
		// Original class name -> obfuscated method names.
		Map<String, Set<MethodInfo>> methodMap = this.classMethodMap.get(className);
		if (methodMap == null) {
			methodMap = new HashMap<String, Set<MethodInfo>>();
			this.classMethodMap.put(className, methodMap);
		}

		// Obfuscated method name -> methods.
		Set<MethodInfo> methodSet = methodMap.get(newMethodName);
		if (methodSet == null) {
			methodSet = new LinkedHashSet<MethodInfo>();
			methodMap.put(newMethodName, methodSet);
		}

		// Add the method information.
		methodSet.add(new MethodInfo(firstLineNumber, lastLineNumber, methodReturnType, methodArguments, methodName));
	}

}
