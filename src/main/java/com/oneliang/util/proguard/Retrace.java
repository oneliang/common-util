package com.oneliang.util.proguard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class Retrace {

	private Retrace() {
	}

	/**
	 * 
	 * @param mappingFile
	 * @throws Exception
	 */
	public static void readMapping(String mappingFile, Processor processor) throws Exception {
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new BufferedReader(new FileReader(mappingFile)));
			String className = null;

			// Read the subsequent class mappings and class member mappings.
			while (true) {
				String line = reader.readLine();

				if (line == null) {
					break;
				}

				line = line.trim();

				// The distinction between a class mapping and a class
				// member mapping is the initial whitespace.
				if (line.endsWith(":")) {
					// Process the class mapping and remember the class's
					// old name.
					className = processClassMapping(line, processor);
				} else if (className != null) {
					// Process the class member mapping, in the context of the
					// current old class name.
					processClassMemberMapping(className, line, processor);
				}
			}
		} catch (Exception ex) {
			throw new Exception("Can't process mapping file (" + ex.getMessage() + ")");
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				// This shouldn't happen.
			}
		}
	}

	/**
	 * process class mapping
	 * 
	 * @param line
	 * @param processor
	 * @return String
	 */
	private static String processClassMapping(String line, Processor processor) {
		// See if we can parse "___ -> ___:", containing the original
		// class name and the new class name.

		int arrowIndex = line.indexOf("->");
		if (arrowIndex < 0) {
			return null;
		}

		int colonIndex = line.indexOf(':', arrowIndex + 2);
		if (colonIndex < 0) {
			return null;
		}

		// Extract the elements.
		String className = line.substring(0, arrowIndex).trim();
		String newClassName = line.substring(arrowIndex + 2, colonIndex).trim();

		// Process this class name mapping.
		processor.processClassMapping(className, newClassName);

		return className;
	}

	/**
	 * Parses the given line with a class member mapping and processes the
	 * results with the given mapping processor.
	 */
	private static void processClassMemberMapping(String className, String line, Processor processor) {
		// See if we can parse "___:___:___ ___(___) -> ___",
		// containing the optional line numbers, the return type, the original
		// field/method name, optional arguments, and the new field/method name.

		int colonIndex1 = line.indexOf(':');
		int colonIndex2 = colonIndex1 < 0 ? -1 : line.indexOf(':', colonIndex1 + 1);
		int spaceIndex = line.indexOf(' ', colonIndex2 + 2);
		int argumentIndex1 = line.indexOf('(', spaceIndex + 1);
		int argumentIndex2 = argumentIndex1 < 0 ? -1 : line.indexOf(')', argumentIndex1 + 1);
		int arrowIndex = line.indexOf("->", Math.max(spaceIndex, argumentIndex2) + 1);

		if (spaceIndex < 0 || arrowIndex < 0) {
			return;
		}

		// Extract the elements.
		String type = line.substring(colonIndex2 + 1, spaceIndex).trim();
		String name = line.substring(spaceIndex + 1, argumentIndex1 >= 0 ? argumentIndex1 : arrowIndex).trim();
		String newName = line.substring(arrowIndex + 2).trim();

		// Process this class member mapping.
		if (type.length() > 0 && name.length() > 0 && newName.length() > 0) {
			// Is it a field or a method?
			if (argumentIndex2 < 0) {
				processor.processFieldMapping(className, type, name, newName);
			} else {
				int firstLineNumber = 0;
				int lastLineNumber = 0;

				if (colonIndex2 > 0) {
					firstLineNumber = Integer.parseInt(line.substring(0, colonIndex1).trim());
					lastLineNumber = Integer.parseInt(line.substring(colonIndex1 + 1, colonIndex2).trim());
				}

				String arguments = line.substring(argumentIndex1 + 1, argumentIndex2).trim();

				processor.processMethodMapping(className, firstLineNumber, lastLineNumber, type, name, arguments, newName);
			}
		}
	}

	/**
	 * A field record.
	 */
	public static class FieldInfo {
		private String type;
		private String originalName;

		public FieldInfo(String type, String originalName) {
			this.type = type;
			this.originalName = originalName;
		}

		private boolean matches(String type) {
			return type == null || type.equals(this.type);
		}
	}

	/**
	 * A method record.
	 */
	public static class MethodInfo {
		private int firstLineNumber;
		private int lastLineNumber;
		private String type;
		private String arguments;
		private String originalName;

		public MethodInfo(int firstLineNumber, int lastLineNumber, String type, String arguments, String originalName) {
			this.firstLineNumber = firstLineNumber;
			this.lastLineNumber = lastLineNumber;
			this.type = type;
			this.arguments = arguments;
			this.originalName = originalName;
		}

		private boolean matches(int lineNumber, String type, String arguments) {
			return (lineNumber == 0 || (firstLineNumber <= lineNumber && lineNumber <= lastLineNumber) || lastLineNumber == 0) && (type == null || type.equals(this.type)) && (arguments == null || arguments.equals(this.arguments));
		}
	}

	public static interface Processor {
		/**
		 * process class mapping
		 * 
		 * @param className
		 * @param newClassName
		 */
		public void processClassMapping(String className, String newClassName);

		/**
		 * process field mapping
		 * 
		 * @param className
		 * @param fieldType
		 * @param fieldName
		 * @param newFieldName
		 */
		public void processFieldMapping(String className, String fieldType, String fieldName, String newFieldName);

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
		public void processMethodMapping(String className, int firstLineNumber, int lastLineNumber, String methodReturnType, String methodName, String methodArguments, String newMethodName);
	}
}
