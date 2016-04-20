package com.oneliang.util.file;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.oneliang.Constant;
import com.oneliang.util.common.Generator;
import com.oneliang.util.common.StringUtil;

/**
 * @author Dandelion
 * @since 2008-06-17 file operate
 *        File类.list()返回String[]不好用,发现有D问题,用File类.listFile()返回File[]
 *        File类.getPath()为相对路径,getAbsolutePath()为绝对路径
 *        FileInputStream与FileOutputStream,用二进制流可以复制任何文件,当使用这两个类时,要保证目录存在,不然复制出错
 */
public final class FileUtil {

	private static final FileCopyProcessor DEFAULT_FILE_COPY_PROCESSOR = new DefaultFileCopyProcessor();

	private FileUtil() {
	}

	/**
	 * is file exist,include directory or file
	 * 
	 * @param path
	 *            directory or file
	 * @return boolean
	 */
	public static boolean isExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * is has file from directory
	 * 
	 * @param directory
	 * @param fileSuffix
	 * @return boolean
	 */
	public static boolean isHasFile(String directory, String fileSuffix) {
		boolean result = false;
		File directoryFile = new File(directory);
		Queue<File> queue = new ConcurrentLinkedQueue<File>();
		queue.add(directoryFile);
		while (!queue.isEmpty()) {
			File file = queue.poll();
			if (file.isDirectory()) {
				File[] fileArray = file.listFiles();
				if (fileArray != null) {
					queue.addAll(Arrays.asList(fileArray));
				}
			} else if (file.isFile()) {
				if (file.getName().toLowerCase().endsWith(fileSuffix.toLowerCase())) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * create directory
	 * 
	 * @param directoryPath
	 */
	public static void createDirectory(final String directoryPath) {
		File file = new File(directoryPath);
		if (!file.exists()) {
			file.setReadable(true, false);
			file.setWritable(true, true);
			file.mkdirs();
		}
	}

	/**
	 * create file,full filename,signle empty file.
	 * 
	 * @param fullFilename
	 * @return boolean
	 */
	public static boolean createFile(final String fullFilename) {
		boolean result = false;
		File file = new File(fullFilename);
		createDirectory(file.getParent());
		try {
			file.setReadable(true, false);
			file.setWritable(true, true);
			result = file.createNewFile();
		} catch (Exception e) {
			throw new FileUtilException(e);
		}
		return result;
	}

	/**
	 * delete all file
	 * 
	 * @param directory
	 */
	public static void deleteAllFile(String directory) {
		List<File> fileList = new ArrayList<File>();
		File directoryFile = new File(directory);
		Queue<File> queue = new ConcurrentLinkedQueue<File>();
		queue.add(directoryFile);
		while (!queue.isEmpty()) {
			File file = queue.poll();
			if (file.isDirectory()) {
				File[] fileArray = file.listFiles();
				if (fileArray != null) {
					queue.addAll(Arrays.asList(fileArray));
				}
			}
			fileList.add(file);
		}
		for (int i = fileList.size() - 1; i >= 0; i--) {
			fileList.get(i).delete();
		}
	}

	/**
	 * copy file,default path to path
	 * 
	 * @param from
	 * @param to
	 */
	public static void copyFile(final String from, final String to) {
		copyFile(from, to, FileCopyType.PATH_TO_PATH, DEFAULT_FILE_COPY_PROCESSOR);
	}

	/**
	 * copy file
	 * 
	 * @param from
	 * @param to
	 * @param fileCopyType
	 */
	public static void copyFile(final String from, final String to, final FileCopyType fileCopyType) {
		copyFile(from, to, fileCopyType, DEFAULT_FILE_COPY_PROCESSOR);
	}

	/**
	 * copy file
	 * 
	 * @param from
	 * @param to
	 * @param fileCopyType
	 * @param fileCopyProcessor
	 */
	public static void copyFile(final String from, final String to, final FileCopyType fileCopyType, FileCopyProcessor fileCopyProcessor) {
		switch (fileCopyType) {
		case FILE_TO_PATH:
			copyFileToPath(from, to, fileCopyProcessor);
			break;
		case FILE_TO_FILE:
			copyFileToFile(from, to, fileCopyProcessor);
			break;
		case PATH_TO_PATH:
		default:
			copyPathToPath(from, to, fileCopyProcessor);
			break;
		}
	}

	/**
	 * copy path to path,copy process include directory copy
	 * 
	 * @param fromPath
	 * @param toPath
	 * @param fileCopyProcessor
	 */
	public static void copyPathToPath(final String fromPath, final String toPath, FileCopyProcessor fileCopyProcessor) {
		File fromDirectoryFile = new File(fromPath);
		File toDirectoryFile = new File(toPath);
		String fromDirectoryPath = fromDirectoryFile.getAbsolutePath();
		String toDirectoryPath = toDirectoryFile.getAbsolutePath();
		if (fromDirectoryPath.equals(toDirectoryPath)) {
			toDirectoryPath = toDirectoryPath + "_copy";
		}
		Queue<File> queue = new ConcurrentLinkedQueue<File>();
		queue.add(fromDirectoryFile);
		while (!queue.isEmpty()) {
			File file = queue.poll();
			String fromFilePath = file.getAbsolutePath();
			String toFilePath = toDirectoryPath + fromFilePath.substring(fromDirectoryPath.length());
			if (file.isDirectory()) {
				boolean result = true;
				if (fileCopyProcessor != null) {
					result = fileCopyProcessor.copyFileToFileProcess(fromFilePath, toFilePath, false);
				}
				if (result) {
					File[] fileArray = file.listFiles();
					if (fileArray != null) {
						queue.addAll(Arrays.asList(fileArray));
					}
				}
			} else if (file.isFile()) {
				if (fileCopyProcessor != null) {
					fileCopyProcessor.copyFileToFileProcess(fromFilePath, toFilePath, true);
				}
			}
		}
	}

	/**
	 * @param fromFile
	 * @param toPath
	 * @param fileCopyProcessor
	 */
	private static void copyFileToPath(final String fromFile, final String toPath, final FileCopyProcessor fileCopyProcessor) {
		File from = new File(fromFile);
		File to = new File(toPath);
		if (from.exists() && from.isFile()) {
			createDirectory(toPath);
			String tempFromFile = from.getAbsolutePath();
			String tempToFile = to.getAbsolutePath() + File.separator + from.getName();
			copyFileToFile(tempFromFile, tempToFile, fileCopyProcessor);
		}
	}

	/**
	 * unzip
	 * 
	 * @param zipFullFilename
	 * @param outputDirectory
	 * @return List<String>
	 */
	public static List<String> unzip(String zipFullFilename, String outputDirectory) {
		return unzip(zipFullFilename, outputDirectory, null);
	}

	/**
	 * unzip
	 * 
	 * @param zipFullFilename
	 * @param outputDirectory
	 * @param zipEntryNameList,if
	 *            it is null or empty,will unzip all
	 * @return List<String>
	 */
	public static List<String> unzip(String zipFullFilename, String outputDirectory, List<String> zipEntryNameList) {
		if (outputDirectory == null) {
			throw new NullPointerException("out put directory can not be null.");
		}
		createDirectory(outputDirectory);
		List<String> storeFileList = null;
		ZipFile zipFile = null;
		try {
			storeFileList = new ArrayList<String>();
			zipFile = new ZipFile(zipFullFilename);
			String outputDirectoryAbsolutePath = new File(outputDirectory).getAbsolutePath();
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();
				String zipEntryName = zipEntry.getName();
				boolean contains = false;
				if (zipEntryNameList == null || zipEntryNameList.isEmpty()) {
					contains = true;
				} else {
					if (zipEntryNameList.contains(zipEntryName)) {
						contains = true;
					}
				}
				if (contains) {
					InputStream inputStream = zipFile.getInputStream(zipEntry);
					String outputFullFilename = outputDirectoryAbsolutePath + Constant.Symbol.SLASH_LEFT + zipEntryName;
					if (zipEntry.isDirectory()) {
						createDirectory(outputFullFilename);
					} else {
						createFile(outputFullFilename);
						OutputStream outputStream = new FileOutputStream(outputFullFilename);
						try {
							byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
							int length = -1;
							while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
								outputStream.write(buffer, 0, length);
								outputStream.flush();
							}
						} finally {
							if (inputStream != null) {
								inputStream.close();
							}
							if (outputStream != null) {
								outputStream.close();
							}
						}
						storeFileList.add(outputFullFilename);
					}
				}
			}
		} catch (Exception e) {
			throw new FileUtilException(e);
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return storeFileList;
	}

	/**
	 * zip
	 * 
	 * @param outputZipFullFilename
	 * @param directory
	 */
	public static void zip(String outputZipFullFilename, String directory) {
		zip(outputZipFullFilename, directory, StringUtil.BLANK);
	}

	/**
	 * zip
	 * 
	 * @param outputZipFullFilename
	 * @param directory
	 * @param fileSuffix
	 */
	public static void zip(String outputZipFullFilename, String directory, String fileSuffix) {
		zip(outputZipFullFilename, directory, fileSuffix, null);
	}

	/**
	 * zip
	 * 
	 * @param outputZipFullFilename
	 * @param directory
	 * @param fileSuffix
	 * @param zipProcessor
	 */
	public static void zip(String outputZipFullFilename, String directory, String fileSuffix, ZipProcessor zipProcessor) {
		List<String> fileList = FileUtil.findMatchFile(directory, fileSuffix);
		if (fileList != null && !fileList.isEmpty()) {
			List<ZipEntryPath> zipEntryPathList = new ArrayList<ZipEntryPath>();
			int outputFullFilenameLength = new File(directory).getAbsolutePath().length() + 1;
			for (String file : fileList) {
				String zipEntryName = file.substring(outputFullFilenameLength, file.length());
				zipEntryName = zipEntryName.replace(Constant.Symbol.SLASH_RIGHT, Constant.Symbol.SLASH_LEFT);
				zipEntryPathList.add(new ZipEntryPath(file, new ZipEntry(zipEntryName), true));
			}
			zip(outputZipFullFilename, null, zipEntryPathList, zipProcessor);
		}
	}

	/**
	 * zip
	 * 
	 * @param outputZipFullFilename
	 * @param zipEntryPathList
	 */
	public static void zip(String outputZipFullFilename, List<ZipEntryPath> zipEntryPathList) {
		zip(outputZipFullFilename, null, zipEntryPathList);
	}

	/**
	 * zip
	 * 
	 * @param outputZipFullFilename
	 * @param inputZipFullFilename,can
	 *            null,the entry will not from the input file
	 * @param zipEntryPathList
	 */
	public static void zip(String outputZipFullFilename, String inputZipFullFilename, List<ZipEntryPath> zipEntryPathList) {
		zip(outputZipFullFilename, inputZipFullFilename, zipEntryPathList, null);
	}

	/**
	 * zip
	 * 
	 * @param outputZipFullFilename
	 * @param inputZipFullFilename,can
	 *            null,the entry will not from the input file
	 * @param zipProcessor
	 */
	public static void zip(String outputZipFullFilename, String inputZipFullFilename, ZipProcessor zipProcessor) {
		zip(outputZipFullFilename, inputZipFullFilename, (List<ZipEntryPath>)null, zipProcessor);
	}

	/**
	 * zip
	 * 
	 * @param outputZipFullFilename
	 * @param inputZipFullFilename,can
	 *            null,the entry will not from the input file
	 * @param zipEntryPathList
	 * @param zipProcessor
	 */
	public static void zip(String outputZipFullFilename, String inputZipFullFilename, List<ZipEntryPath> zipEntryPathList, ZipProcessor zipProcessor) {
		ZipOutputStream zipOutputStream = null;
		ZipFile zipFile = null;
		Map<String, ZipEntryPath> zipEntryPathMap = new HashMap<String, ZipEntryPath>();
		List<String> needToAddEntryNameList = new CopyOnWriteArrayList<String>();
		if (zipEntryPathList != null) {
			for (ZipEntryPath zipEntryPath : zipEntryPathList) {
				zipEntryPathMap.put(zipEntryPath.zipEntry.getName(), zipEntryPath);
				needToAddEntryNameList.add(zipEntryPath.zipEntry.getName());
			}
		}
		try {
			createFile(outputZipFullFilename);
			zipOutputStream = new ZipOutputStream(new FileOutputStream(outputZipFullFilename));
			if (inputZipFullFilename != null) {
				zipFile = new ZipFile(inputZipFullFilename);
				Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
				while (enumeration.hasMoreElements()) {
					ZipEntry zipEntry = enumeration.nextElement();
					String zipEntryName = zipEntry.getName();
					InputStream inputStream = null;
					if (zipEntryPathMap.containsKey(zipEntryName)) {
						ZipEntryPath zipEntryPath = zipEntryPathMap.get(zipEntryName);
						needToAddEntryNameList.remove(zipEntryName);
						if (zipEntryPath.replace) {
							zipEntry = zipEntryPath.zipEntry;
							inputStream = new FileInputStream(zipEntryPath.fullFilename);
						}
					}
					if (inputStream == null) {
						inputStream = zipFile.getInputStream(zipEntry);
						if(zipProcessor!=null){
							inputStream = zipProcessor.zipEntryProcess(zipEntryName, inputStream);
						}
					}
					ZipEntry newZipEntry=new ZipEntry(zipEntryName);
					addZipEntry(zipOutputStream, newZipEntry, inputStream);
				}
			}
			for (String zipEntryName : needToAddEntryNameList) {
				ZipEntryPath zipEntryPath = zipEntryPathMap.get(zipEntryName);
				ZipEntry zipEntry = zipEntryPath.zipEntry;
				InputStream inputStream = new FileInputStream(zipEntryPath.fullFilename);
				if(zipProcessor!=null){
					inputStream = zipProcessor.zipEntryProcess(zipEntry.getName(), inputStream);
				}
				addZipEntry(zipOutputStream, zipEntry, inputStream);
			}
		} catch (Exception e) {
			throw new FileUtilException(e);
		} finally {
			try {
				if (zipOutputStream != null) {
					zipOutputStream.finish();
					zipOutputStream.flush();
					zipOutputStream.close();
				}
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (Exception e) {
				throw new FileUtilException(e);
			}
		}
	}

	/**
	 * merge zip file
	 * 
	 * @param zipOutputFullFilename
	 * @param mergeZipFullFilenameList
	 */
	public static void mergeZip(String zipOutputFullFilename, List<String> mergeZipFullFilenameList) {
		FileUtil.createFile(zipOutputFullFilename);
		ZipOutputStream zipOutputStream = null;
		try {
			zipOutputStream = new ZipOutputStream(new FileOutputStream(zipOutputFullFilename));
			if (mergeZipFullFilenameList != null) {
				for (String zipFullFilename : mergeZipFullFilenameList) {
					if (isExist(zipFullFilename)) {
						ZipFile zipFile = new ZipFile(zipFullFilename);
						Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
						while (enumeration.hasMoreElements()) {
							ZipEntry zipEntry = enumeration.nextElement();
							InputStream inputStream = zipFile.getInputStream(zipEntry);
							addZipEntry(zipOutputStream, zipEntry, inputStream);
						}
						zipFile.close();
					}
				}
			}
		} catch (Exception e) {
			throw new FileUtilException(e);
		} finally {
			try {
				if (zipOutputStream != null) {
					zipOutputStream.close();
				}
			} catch (Exception e) {
				throw new FileUtilException(e);
			}
		}
	}

	/**
	 * add zip entry
	 * 
	 * @param zipOutputStream
	 * @param zipEntry
	 * @param inputStream
	 * @throws Exception
	 */
	public static void addZipEntry(ZipOutputStream zipOutputStream, ZipEntry zipEntry, InputStream inputStream) throws Exception {
		try {
			zipOutputStream.putNextEntry(zipEntry);
			byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
			int length = -1;
			while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
				zipOutputStream.write(buffer, 0, length);
				zipOutputStream.flush();
			}
		} catch (ZipException e) {
			// do nothing
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			zipOutputStream.closeEntry();
		}
	}

	/**
	 * read file
	 * 
	 * @param fullFilename
	 * @return byte[]
	 */
	public static byte[] readFile(String fullFilename) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fullFilename);
			copyStream(inputStream, byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			throw new FileUtilException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new FileUtilException(e);
				}
			}
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
				} catch (IOException e) {
					throw new FileUtilException(e);
				}
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * write file
	 * 
	 * @param outputFullFilename
	 * @param byteArray
	 */
	public static void writeFile(String outputFullFilename, byte[] byteArray) {
		InputStream inputStream = new ByteArrayInputStream(byteArray);
		FileUtil.createFile(outputFullFilename);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(outputFullFilename);
			copyStream(inputStream, outputStream);
		} catch (FileNotFoundException e) {
			throw new FileUtilException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new FileUtilException(e);
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					throw new FileUtilException(e);
				}
			}
		}
	}

	/**
	 * copy stream , from input to output,it don't close
	 * 
	 * @param inputStream
	 * @param outputStream
	 */
	public static void copyStream(InputStream inputStream, OutputStream outputStream) {
		if (inputStream != null && outputStream != null) {
			try {
				int length = -1;
				byte[] buffer = new byte[Constant.Capacity.BYTES_PER_MB];
				while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
					outputStream.write(buffer, 0, length);
					outputStream.flush();
				}
			} catch (Exception e) {
				throw new FileUtilException(e);
			}
		}
	}

	/**
	 * merge file
	 * 
	 * @param outputFullFilename
	 * @param fullFilenameList
	 */
	public static void mergeFile(String outputFullFilename, List<String> fullFilenameList) {
		if (fullFilenameList != null && outputFullFilename != null) {
			OutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(outputFullFilename);
				for (String fullFilename : fullFilenameList) {
					InputStream inputStream = null;
					try {
						inputStream = new FileInputStream(fullFilename);
						copyStream(inputStream, outputStream);
					} catch (Exception e) {
						throw new FileUtilException(e);
					} finally {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (IOException e) {
								throw new FileUtilException(e);
							}
						}
					}
				}
			} catch (Exception e) {
				throw new FileUtilException(e);
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						throw new FileUtilException(e);
					}
				}
			}
		}
	}

	/**
	 * find match file directory
	 * 
	 * @param sourceDirectory
	 * @param fileSuffix
	 * @return List<String>
	 */
	public static List<String> findMatchFileDirectory(String sourceDirectory, String fileSuffix) {
		return findMatchFileOrMatchFileDirectory(sourceDirectory, fileSuffix, null, false, true);
	}

	/**
	 * find match file directory
	 * 
	 * @param sourceDirectory
	 * @param fileSuffix
	 * @param includeHidden
	 * @return List<String>
	 */
	public static List<String> findMatchFileDirectory(String sourceDirectory, String fileSuffix, boolean includeHidden) {
		return findMatchFileOrMatchFileDirectory(sourceDirectory, fileSuffix, null, false, includeHidden);
	}

	/**
	 * find match file directory and append some string to rear
	 * 
	 * @param sourceDirectory
	 * @param fileSuffix
	 * @param somethingAppendToRear
	 * @return List<String>
	 */
	public static List<String> findMatchFileDirectory(String sourceDirectory, String fileSuffix, String somethingAppendToRear) {
		return findMatchFileOrMatchFileDirectory(sourceDirectory, fileSuffix, somethingAppendToRear, false, true);
	}

	/**
	 * find match file directory and append some string to rear
	 * 
	 * @param sourceDirectory
	 * @param fileSuffix
	 * @param somethingAppendToRear
	 * @param includeHidden
	 * @return List<String>
	 */
	public static List<String> findMatchFileDirectory(String sourceDirectory, String fileSuffix, String somethingAppendToRear, boolean includeHidden) {
		return findMatchFileOrMatchFileDirectory(sourceDirectory, fileSuffix, somethingAppendToRear, false, includeHidden);
	}

	/**
	 * find match file
	 * 
	 * @param sourceDirectory
	 * @param fileSuffix
	 * @return List<String>
	 */
	public static List<String> findMatchFile(String sourceDirectory, String fileSuffix) {
		return findMatchFileOrMatchFileDirectory(sourceDirectory, fileSuffix, null, true, true);
	}

	/**
	 * find match file
	 * 
	 * @param sourceDirectory
	 * @param fileSuffix
	 * @param includeHidden
	 * @return List<String>
	 */
	public static List<String> findMatchFile(String sourceDirectory, String fileSuffix, boolean includeHidden) {
		return findMatchFileOrMatchFileDirectory(sourceDirectory, fileSuffix, null, true, includeHidden);
	}

	/**
	 * find match file and append some string to rear
	 * 
	 * @param sourceDirectory
	 * @param fileSuffix
	 * @param somethingAppendToRear
	 * @return List<String>
	 */
	public static List<String> findMatchFile(String sourceDirectory, String fileSuffix, String somethingAppendToRear) {
		return findMatchFileOrMatchFileDirectory(sourceDirectory, fileSuffix, somethingAppendToRear, true, false);
	}

	/**
	 * find match file and append some string to rear
	 * 
	 * @param sourceDirectory
	 * @param fileSuffix
	 * @param somethingAppendToRear
	 * @param includeHidden
	 * @return List<String>
	 */
	public static List<String> findMatchFile(String sourceDirectory, String fileSuffix, String somethingAppendToRear, boolean includeHidden) {
		return findMatchFileOrMatchFileDirectory(sourceDirectory, fileSuffix, somethingAppendToRear, true, includeHidden);
	}

	/**
	 * find match file or match file directory
	 * 
	 * @param sourceDirectory
	 * @param fileSuffix
	 * @param somethingAppendToRear
	 * @param isFindMatchFile
	 * @param includeHidden
	 * @return List<String>
	 */
	private static List<String> findMatchFileOrMatchFileDirectory(String sourceDirectory, String fileSuffix, String somethingAppendToRear, boolean isFindMatchFile, boolean includeHidden) {
		fileSuffix = StringUtil.nullToBlank(fileSuffix);
		somethingAppendToRear = StringUtil.nullToBlank(somethingAppendToRear);
		List<String> list = new ArrayList<String>();
		File sourceDirectoryFile = new File(sourceDirectory);
		Queue<File> queue = new ConcurrentLinkedQueue<File>();
		queue.add(sourceDirectoryFile);
		while (!queue.isEmpty()) {
			File file = queue.poll();
			boolean result = false;
			if (!file.isHidden() || includeHidden) {
				result = true;
			}
			if (result) {
				if (file.isDirectory()) {
					File[] fileArray = file.listFiles();
					if (fileArray != null) {
						queue.addAll(Arrays.asList(fileArray));
					}
				} else if (file.isFile()) {
					if (file.getName().toLowerCase().endsWith(fileSuffix.toLowerCase())) {
						if (isFindMatchFile) {
							list.add(file.getAbsolutePath() + somethingAppendToRear);
						} else {
							String parentPath = file.getParent();
							parentPath = parentPath + somethingAppendToRear;
							if (!list.contains(parentPath)) {
								list.add(parentPath);
							}
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * get zip entry hash map
	 * 
	 * @param zipFile
	 * @return Map<String, String>
	 */
	private static Map<String, String> getZipEntryHashMap(String zipFullFilename) {
		ZipFile zipFile = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			zipFile = new ZipFile(zipFullFilename);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) entries.nextElement();
				if (!zipEntry.isDirectory()) {
					String key = zipEntry.getName();
					String value = zipEntry.getCrc() + Constant.Symbol.DOT + zipEntry.getSize();
					map.put(key, value);
				}
			}
		} catch (Exception e) {
			throw new FileUtilException(e);
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					throw new FileUtilException(e);
				}
			}
		}
		return map;
	}

	/**
	 * differ zip
	 * 
	 * @param differentOutputFullFilename
	 * @param oldZipFullFilename
	 * @param newZipFullFilename
	 */
	public static void differZip(String differentOutputFullFilename, String oldZipFullFilename, String newZipFullFilename) {
		Map<String, String> map = getZipEntryHashMap(oldZipFullFilename);
		ZipFile newZipFile = null;
		ZipOutputStream zipOutputStream = null;
		try {
			newZipFile = new ZipFile(newZipFullFilename);
			Enumeration<? extends ZipEntry> entries = newZipFile.entries();
			FileUtil.createFile(differentOutputFullFilename);
			zipOutputStream = new ZipOutputStream(new FileOutputStream(differentOutputFullFilename));
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				if (!zipEntry.isDirectory()) {
					String zipEntryName = zipEntry.getName();
					String oldZipEntryHash = map.get(zipEntryName);
					String newZipEntryHash = zipEntry.getCrc() + Constant.Symbol.DOT + zipEntry.getSize();
					// old zip entry hash not exist is a new zip entry,if exist
					// is a modified zip entry
					if (oldZipEntryHash == null || (!newZipEntryHash.equals(oldZipEntryHash))) {
						System.out.println(String.format("found modified entry, key=%s(%s/%s)", new Object[] { zipEntryName, oldZipEntryHash, newZipEntryHash }));
						addZipEntry(zipOutputStream, zipEntry, newZipFile.getInputStream(zipEntry));
					}
				}
			}
		} catch (Exception e) {
			throw new FileUtilException(e);
		} finally {
			if (newZipFile != null) {
				try {
					newZipFile.close();
				} catch (IOException e) {
					throw new FileUtilException(e);
				}
			}
			if (zipOutputStream != null) {
				try {
					zipOutputStream.finish();
				} catch (IOException e) {
					throw new FileUtilException(e);
				}
			}
		}
	}

	/**
	 * differ directory
	 * @param differenOutputDirectory
	 * @param oldDirectory
	 * @param newDirectory
	 */
	public static void differDirectory(String differenOutputDirectory, String oldDirectory, String newDirectory){
		List<String> oldFileList=FileUtil.findMatchFile(oldDirectory, StringUtil.BLANK);
		String oldDirectoryAbsolutePath=new File(oldDirectory).getAbsolutePath();
		Map<String,String> oldFileMD5Map=new HashMap<String,String>();
		differenOutputDirectory=new File(differenOutputDirectory).getAbsolutePath();
		for(String oldFile:oldFileList){
			String key=new File(oldFile).getAbsolutePath().substring(oldDirectoryAbsolutePath.length()+1);
			key=key.replace(Constant.Symbol.SLASH_RIGHT, Constant.Symbol.SLASH_LEFT);
			String value=Generator.MD5File(oldFile);
			oldFileMD5Map.put(key, value);
		}
		List<String> newFileList=FileUtil.findMatchFile(newDirectory, StringUtil.BLANK);
		String newDirectoryAbsolutePath=new File(newDirectory).getAbsolutePath();
		for(String newFile:newFileList){
			String key=new File(newFile).getAbsolutePath().substring(newDirectoryAbsolutePath.length()+1);
			key=key.replace(Constant.Symbol.SLASH_RIGHT, Constant.Symbol.SLASH_LEFT);
			String value=Generator.MD5File(newFile);
			String oldValue=oldFileMD5Map.get(key);
			if (oldValue == null || (!oldValue.equals(value))) {
				String toFile=differenOutputDirectory+Constant.Symbol.SLASH_LEFT+key;
				System.out.println("key:"+key+",oldValue:"+oldValue+",value:"+value);
				copyFile(newFile, toFile, FileCopyType.FILE_TO_FILE);
			}
		}
	}

	/**
	 * generate simple file
	 * 
	 * @param templateFullFilename
	 * @param outputFullFilename
	 * @param valueMap
	 */
	public static void generateSimpleFile(String templateFullFilename, String outputFullFilename, Map<String, String> valueMap) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(templateFullFilename);
			generateSimpleFile(inputStream, outputFullFilename, valueMap);
		} catch (Exception e) {
			throw new FileUtilException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new FileUtilException(e);
				}
			}
		}
	}

	/**
	 * generate simple file
	 * 
	 * @param templateInputStream
	 * @param outputFullFilename
	 * @param valueMap
	 */
	public static void generateSimpleFile(InputStream templateInputStream, String outputFullFilename, Map<String, String> valueMap) {
		BufferedReader bufferedReader = null;
		OutputStream outputStream = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(templateInputStream, Constant.Encoding.UTF8));
			StringBuilder content = new StringBuilder();
			String line = null;
			Set<Entry<String, String>> entrySet = valueMap.entrySet();
			while ((line = bufferedReader.readLine()) != null) {
				for (Entry<String, String> entry : entrySet) {
					String key = entry.getKey();
					String value = entry.getValue();
					line = line.replace(key, value);
				}
				content.append(line);
				content.append(StringUtil.CRLF_STRING);
			}
			createFile(outputFullFilename);
			outputStream = new FileOutputStream(outputFullFilename);
			outputStream.write(content.toString().getBytes(Constant.Encoding.UTF8));
			outputStream.flush();
		} catch (Exception e) {
			throw new FileUtilException(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					throw new FileUtilException(e);
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
					throw new FileUtilException(e);
				}
			}
		}
	}

	/**
	 * find file list with cache
	 * @param sourceDirectoryList
	 * @param cacheProperties
	 * @param file suffix it will search file in source directory list
	 * @param somethingAppendToRear
	 * @param isFile if true the return list is source file else is the source directory
	 * @return List<String>
	 */
	public static List<String> findFileListWithCache(List<String> sourceDirectoryList,Properties cacheProperties,String fileSuffix,String somethingAppendToRear,boolean isFile){
		return findFileListWithCache(sourceDirectoryList, cacheProperties, fileSuffix, somethingAppendToRear, isFile, null);
	}

	/**
	 * find file list with cache
	 * @param sourceDirectoryList
	 * @param cacheProperties
	 * @param fileSuffix
	 * @param somethingAppendToRear
	 * @param isFile
	 * @param cacheProcessor
	 * @return List<String>
	 */
	public static List<String> findFileListWithCache(List<String> sourceDirectoryList,Properties cacheProperties,String fileSuffix,String somethingAppendToRear,boolean isFile,CacheProcessor cacheProcessor){
		return findFileListWithCache(sourceDirectoryList, cacheProperties, fileSuffix, somethingAppendToRear, isFile, false, cacheProcessor);
	}

	/**
	 * find file list with cache
	 * @param sourceDirectoryList
	 * @param cacheProperties
	 * @param file suffix it will search file in source directory list
	 * @param somethingAppendToRear
	 * @param isFile if true the return list is source file else is the source directory
	 * @param includeHidden
	 * @return List<String>
	 */
	public static List<String> findFileListWithCache(List<String> sourceDirectoryList,Properties cacheProperties,String fileSuffix,String somethingAppendToRear,boolean isFile,boolean includeHidden,CacheProcessor cacheProcessor){
		List<String> sourceList=new ArrayList<String>();
		//no cache
		if(cacheProperties==null){
			if(sourceDirectoryList!=null&&!sourceDirectoryList.isEmpty()){
				for(String sourceDirectory:sourceDirectoryList){
					if(isFile){
						sourceList.addAll(FileUtil.findMatchFile(sourceDirectory,fileSuffix,includeHidden));
					}else{
						sourceList.addAll(FileUtil.findMatchFileDirectory(sourceDirectory,fileSuffix,somethingAppendToRear,includeHidden));
					}
				}
			}
		}else if(cacheProperties.isEmpty()){
			List<String> fileList=new ArrayList<String>();
			if(sourceDirectoryList!=null&&!sourceDirectoryList.isEmpty()){
				for(String sourceDirectory:sourceDirectoryList){
					fileList.addAll(FileUtil.findMatchFile(sourceDirectory, fileSuffix,includeHidden));
				}
			}
			for(String fullFilename:fileList){
				String cacheKey=fullFilename;
				if(cacheProcessor!=null){
					cacheKey=cacheProcessor.keyProcess(cacheKey);
				}
				cacheProperties.setProperty(cacheKey, Generator.MD5File(fullFilename));
			}
			if(isFile){
				sourceList.addAll(fileList);
			}else{
				if(sourceDirectoryList!=null&&!sourceDirectoryList.isEmpty()){
					for(String sourceDirectory:sourceDirectoryList){
						sourceList.addAll(FileUtil.findMatchFileDirectory(sourceDirectory,fileSuffix,somethingAppendToRear,includeHidden));
					}
				}
			}
		}else{//with cache
			List<String> fileList=new ArrayList<String>();
			if(sourceDirectoryList!=null&&!sourceDirectoryList.isEmpty()){
				for(String sourceDirectory:sourceDirectoryList){
					fileList.addAll(FileUtil.findMatchFile(sourceDirectory, fileSuffix,includeHidden));
				}
			}
			for(String fullFilename:fileList){
				String cacheKey=fullFilename;
				if(cacheProcessor!=null){
					cacheKey=cacheProcessor.keyProcess(cacheKey);
				}
				String sourceFileMd5=Generator.MD5File(fullFilename);
				if(cacheProperties.containsKey(cacheKey)){
					String md5=cacheProperties.getProperty(cacheKey);
					if(!sourceFileMd5.equals(md5)){
						sourceList.add(fullFilename);
						cacheProperties.setProperty(cacheKey, sourceFileMd5);
					}
				}else{
					sourceList.add(fullFilename);
					cacheProperties.setProperty(cacheKey, sourceFileMd5);
				}
			}
		}
		return sourceList;
	}

	/**
	 * deal with file cache
	 * @param propertiesFileMappingFullFilename
	 * @param noCacheFileFinder
	 * @param noCacheFileProcessor
	 * @return List<String>
	 */
	public static List<String> dealWithFileCache(String propertiesFileMappingFullFilename, NoCacheFileFinder noCacheFileFinder, NoCacheFileProcessor noCacheFileProcessor){
		Properties propertiesFileMapping=getPropertiesAutoCreate(propertiesFileMappingFullFilename);
		List<String> noCacheFileList=null;
		if(noCacheFileFinder==null){
			throw new NullPointerException("noCacheFileFinder can not be null.");
		}
		noCacheFileList=noCacheFileFinder.findNoCacheFileList(propertiesFileMapping);
		boolean saveCache=false;
		if(noCacheFileProcessor!=null){
			saveCache=noCacheFileProcessor.process(noCacheFileList);
		}
		if(saveCache){
			saveProperties(propertiesFileMapping, propertiesFileMappingFullFilename);
		}
		return noCacheFileList;
	}

	/**
	 * get properties,if is not exist will auto create
	 * @param propertiesFullFilename
	 * @return Properties
	 */
	public static Properties getPropertiesAutoCreate(String propertiesFullFilename){
		if(!FileUtil.isExist(propertiesFullFilename)){
			FileUtil.createFile(propertiesFullFilename);
		}
		return getProperties(propertiesFullFilename);
	}

	/**
	 * get properties
	 * @param propertiesFullFilename
	 * @return Properties
	 */
	public static Properties getProperties(String propertiesFullFilename){
		Properties properties=null;
		if(propertiesFullFilename!=null){
			InputStream inputStream=null;
			try{
				inputStream=new FileInputStream(propertiesFullFilename);
				properties=new Properties();
				properties.load(inputStream);
			}catch(Exception e){
				throw new FileUtilException(e);
			}finally{
				if(inputStream!=null){
					try {
						inputStream.close();
					} catch (Exception e) {
						throw new FileUtilException(e);
					}
				}
			}
		}
		return properties;
	}

	/**
	 * get properties from properties file,will auto create
	 * @param file
	 * @return Properties
	 * @throws IOException 
	 */
	public static final Properties getProperties(File file){
		Properties properties=null;
		if(file!=null){
			properties=getProperties(file.getAbsolutePath());
		}
		return properties;
	}

	/**
	 * save properties
	 * @param properties
	 * @param outputFullFilename
	 */
	public static void saveProperties(Properties properties, String outputFullFilename){
		if(properties!=null&&outputFullFilename!=null){
			OutputStream outputStream=null;
			try {
				outputStream=new FileOutputStream(outputFullFilename);
				properties.store(outputStream, null);
			} catch (Exception e) {
				throw new FileUtilException(e);
			} finally {
				if(outputStream!=null){
					try{
						outputStream.flush();
						outputStream.close();
					}catch (Exception e) {
						throw new FileUtilException(e);
					}
				}
			}
		}
	}

	public static class ZipEntryPath {
		private String fullFilename = null;
		private ZipEntry zipEntry = null;
		private boolean replace = false;

		public ZipEntryPath(String fullFilename, ZipEntry zipEntry) {
			this(fullFilename, zipEntry, false);
		}

		public ZipEntryPath(String fullFilename, ZipEntry zipEntry, boolean replace) {
			this.fullFilename = fullFilename;
			this.zipEntry = zipEntry;
			this.replace = replace;
		}
	}

	public static class FileUtilException extends RuntimeException {
		private static final long serialVersionUID = 3884649425767533205L;

		public FileUtilException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * @param fromFile
	 * @param toFile
	 * @param fileCopyProcessor
	 */
	private static void copyFileToFile(final String fromFile, final String toFile, FileCopyProcessor fileCopyProcessor) {
		if (fileCopyProcessor != null) {
			createFile(toFile);
			fileCopyProcessor.copyFileToFileProcess(fromFile, toFile, true);
		}
	}

	public static enum FileCopyType {
		PATH_TO_PATH, FILE_TO_PATH, FILE_TO_FILE
	}

	public static interface FileCopyProcessor {

		/**
		 * copyFileToFileProcess
		 * 
		 * @param from,maybe
		 *            directory
		 * @param to,maybe
		 *            directory
		 * @param isFile,maybe
		 *            directory or file
		 * @return boolean,if true keep going copy,only active in directory so
		 *         far
		 */
		public abstract boolean copyFileToFileProcess(final String from, final String to, final boolean isFile);

	}

	public static interface ZipProcessor{

		/**
		 * zip entry process
		 * @param zipEntryName
		 * @param inputStream
		 * @return InputStream
		 */
		public abstract InputStream zipEntryProcess(final String zipEntryName,InputStream inputStream);
	}

	public static abstract interface CacheProcessor{
		/**
		 * key process,can change key to save cache
		 * @param cacheKey
		 * @return String
		 */
		public abstract String keyProcess(final String key);
	}


	public static abstract interface NoCacheFileProcessor{
		/**
		 * process
		 * @param uncachedFileList
		 * @return boolean,true is save cache else false
		 */
		public abstract boolean process(List<String> uncachedFileList);
	}

	public static abstract interface NoCacheFileFinder{

		/**
		 * find no cache file list
		 * @param cacheFileMapping
		 * @return List<String>
		 */
		public abstract List<String> findNoCacheFileList(Properties  cacheFileMapping);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String outputZipFullFilename = "/D:/a/b.zip";
		mergeZip(outputZipFullFilename, Arrays.asList("/D:/a.zip", "/D:/b.zip"));
	}

}
