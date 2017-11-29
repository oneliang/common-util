package com.oneliang.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.oneliang.Constant;
import com.oneliang.util.common.Generator;
import com.oneliang.util.common.ObjectUtil;
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
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    /**
     * has file in directory
     * 
     * @param directory
     * @return boolean
     */
    public static boolean hasFile(String directory) {
        return hasFile(directory, null);
    }

    /**
     * has file in directory
     * 
     * @param directory
     * @param fileSuffix
     * @return boolean
     */
    public static boolean hasFile(String directory, String fileSuffix) {
        boolean result = false;
        fileSuffix = StringUtil.nullToBlank(fileSuffix);
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
        MatchOption matchOption = new MatchOption(directory);
        matchOption.fileSuffix = fileSuffix;
        List<String> fileList = FileUtil.findMatchFile(matchOption);
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
        zip(outputZipFullFilename, inputZipFullFilename, (List<ZipEntryPath>) null, zipProcessor);
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
                    InputStream newInputStream = null;
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
                        if (zipProcessor != null) {
                            newInputStream = zipProcessor.zipEntryProcess(zipEntryName, inputStream);
                            if (newInputStream != null && !newInputStream.equals(inputStream)) {
                                inputStream.close();
                            }
                        } else {
                            newInputStream = inputStream;
                        }
                    }
                    ZipEntry newZipEntry = new ZipEntry(zipEntryName);
                    addZipEntry(zipOutputStream, newZipEntry, newInputStream);
                }
            }
            for (String zipEntryName : needToAddEntryNameList) {
                ZipEntryPath zipEntryPath = zipEntryPathMap.get(zipEntryName);
                ZipEntry zipEntry = zipEntryPath.zipEntry;
                InputStream inputStream = new FileInputStream(zipEntryPath.fullFilename);
                InputStream newInputStream = null;
                if (zipProcessor != null) {
                    newInputStream = zipProcessor.zipEntryProcess(zipEntry.getName(), inputStream);
                    if (newInputStream != null && !newInputStream.equals(inputStream)) {
                        inputStream.close();
                    }
                } else {
                    newInputStream = inputStream;
                }
                addZipEntry(zipOutputStream, zipEntry, newInputStream);
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
     * @param zipFullFilenameList
     */
    public static void mergeZip(String zipOutputFullFilename, List<String> zipFullFilenameList) {
        FileUtil.createFile(zipOutputFullFilename);
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipOutputFullFilename));
            if (zipFullFilenameList != null) {
                for (String zipFullFilename : zipFullFilenameList) {
                    if (isExist(zipFullFilename)) {
                        ZipFile zipFile = new ZipFile(zipFullFilename);
                        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
                        while (enumeration.hasMoreElements()) {
                            ZipEntry zipEntry = enumeration.nextElement();
                            InputStream inputStream = zipFile.getInputStream(zipEntry);
                            ZipEntry newZipEntry = new ZipEntry(zipEntry.getName());
                            addZipEntry(zipOutputStream, newZipEntry, inputStream);
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
        if (zipOutputStream == null || inputStream == null) {
            return;
        }
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
        FileUtil.createFile(outputFullFilename);
        InputStream inputStream = new ByteArrayInputStream(byteArray);
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
     * read file content ignore line
     * 
     * @param fullFilename
     * @return String
     */
    public static String readFileContentIgnoreLine(String fullFilename) {
        return readFileContentIgnoreLine(fullFilename, StringUtil.BLANK);
    }

    /**
     * read file content ignore line
     * 
     * @param fullFilename
     * @param append
     * @return String
     */
    public static String readFileContentIgnoreLine(String fullFilename, final String append) {
        final StringBuilder stringBuilder = new StringBuilder();
        readFileContentIgnoreLine(fullFilename, new ReadFileContentProcessor() {
            public boolean afterReadLine(String line) {
                stringBuilder.append(line);
                stringBuilder.append(StringUtil.nullToBlank(append));
                return true;
            }
        });
        return stringBuilder.toString();
    }

    /**
     * read file content ignore line
     * 
     * @param fullFilename
     * @param readFileContentProcessor
     */
    public static void readFileContentIgnoreLine(String fullFilename, ReadFileContentProcessor readFileContentProcessor) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fullFilename);
            readFileContentIgnoreLine(inputStream, readFileContentProcessor);
        } catch (Exception e) {
            throw new FileUtilException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    throw new FileUtilException(e);
                }
            }
        }
    }

    /**
     * read file content ignore line
     * 
     * @param inputStream
     * @param readFileContentProcessor
     */
    public static void readFileContentIgnoreLine(InputStream inputStream, ReadFileContentProcessor readFileContentProcessor) {
        if (inputStream == null) {
            return;
        }
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (readFileContentProcessor != null) {
                    boolean continueRead = readFileContentProcessor.afterReadLine(line);
                    if (!continueRead) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new FileUtilException(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    throw new FileUtilException(e);
                }
            }
        }
    }

    /**
     * write file content,best for string content
     * 
     * @param fullFilename
     * @param writeFileContentProcessor
     */
    public static void writeFileContent(String fullFilename, WriteFileContentProcessor writeFileContentProcessor) {
        createFile(fullFilename);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullFilename)));
            if (writeFileContentProcessor != null) {
                writeFileContentProcessor.writeContent(bufferedWriter);
            }
        } catch (Exception e) {
            throw new FileUtilException(e);
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (Exception e) {
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
     * @param matchOption
     * @return List<String>
     */
    public static List<String> findMatchFileDirectory(MatchOption matchOption) {
        ObjectUtil.checkNotNull(matchOption, "matchOption can not be null.");
        matchOption.findType = MatchOption.FindType.FILE_DIRECTORY;
        return findMatchFileOrMatchFileDirectory(matchOption);
    }

    /**
     * find match file
     * 
     * @param matchOption
     * @return List<String>
     */
    public static List<String> findMatchFile(MatchOption matchOption) {
        ObjectUtil.checkNotNull(matchOption, "matchOption can not be null.");
        matchOption.findType = MatchOption.FindType.FILE;
        return findMatchFileOrMatchFileDirectory(matchOption);
    }

    /**
     * find match directory
     * 
     * @param matchOption
     * @return List<String>
     */
    public static List<String> findMatchDirectory(MatchOption matchOption) {
        ObjectUtil.checkNotNull(matchOption, "matchOption can not be null.");
        matchOption.findType = MatchOption.FindType.DIRECTORY;
        return findMatchFileOrMatchFileDirectory(matchOption);
    }

    /**
     * find match file or match file directory
     * 
     * @param matchOption
     * @return List<String>
     */
    private static List<String> findMatchFileOrMatchFileDirectory(MatchOption matchOption) {
        String fileSuffix = StringUtil.nullToBlank(matchOption.fileSuffix);
        List<String> list = new ArrayList<String>();
        Queue<File> queue = new ConcurrentLinkedQueue<File>();
        if (matchOption.directory != null) {
            File directoryFile = new File(matchOption.directory);
            queue.add(directoryFile);
        }
        while (!queue.isEmpty()) {
            File file = queue.poll();
            if (!file.exists()) {
                continue;
            }
            boolean result = false;
            if (!file.isHidden() || matchOption.includeHidden) {
                result = true;
            }
            if (!result) {
                continue;
            }
            if (file.isDirectory()) {
                File[] fileArray = file.listFiles();
                if (fileArray == null) {
                    continue;
                }
                for (File singleFile : fileArray) {
                    if (matchOption.findType == MatchOption.FindType.DIRECTORY) {
                        if (singleFile.isDirectory() && matchOption.deep) {
                            queue.add(singleFile);
                            if (!singleFile.getName().toLowerCase().endsWith(fileSuffix.toLowerCase())) {
                                continue;
                            }
                            list.add(singleFile.getAbsolutePath());
                        }
                    } else {
                        if (singleFile.isDirectory() && matchOption.deep) {
                            queue.add(singleFile);
                        } else if (singleFile.isFile()) {
                            queue.add(singleFile);
                        }
                    }
                }
            } else if (file.isFile()) {
                if (!file.getName().toLowerCase().endsWith(fileSuffix.toLowerCase())) {
                    continue;
                }
                if (matchOption.findType == MatchOption.FindType.FILE) {
                    String fullFilename = null;
                    if (matchOption.processor != null) {
                        fullFilename = matchOption.processor.onMatch(file);
                    } else {
                        fullFilename = file.getAbsolutePath();
                    }
                    // ignore when null
                    if (fullFilename != null) {
                        list.add(fullFilename);
                    }
                } else if (matchOption.findType == MatchOption.FindType.FILE_DIRECTORY) {
                    String parentFullFilename = null;
                    if (matchOption.processor != null) {
                        parentFullFilename = matchOption.processor.onMatch(file.getParentFile());
                    } else {
                        parentFullFilename = file.getParentFile().getAbsolutePath();
                    }
                    // ignore when null
                    if (parentFullFilename != null && !list.contains(parentFullFilename)) {
                        list.add(parentFullFilename);
                    }
                }
            }
        }
        return list;
    }

    /**
     * get zip entry map
     * 
     * @param zipFullFilename
     * @return Map<String, String>
     */
    private static Map<String, String> getZipEntryMap(String zipFullFilename) {
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
        differZip(differentOutputFullFilename, oldZipFullFilename, newZipFullFilename, null);
    }

    /**
     * differ zip
     * 
     * @param differentOutputFullFilename
     * @param oldZipFullFilename
     * @param newZipFullFilename
     * @param differZipProcessor
     */
    public static void differZip(String differentOutputFullFilename, String oldZipFullFilename, String newZipFullFilename, DifferZipProcessor differZipProcessor) {
        Map<String, String> map = getZipEntryMap(oldZipFullFilename);
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
                    if (oldZipEntryHash == null) {
                        DifferZipProcessor.ZipEntryInformation zipEntryInformation = null;
                        if (differZipProcessor != null) {
                            zipEntryInformation = differZipProcessor.foundAddedZipEntryProcess(zipEntryName);
                        }
                        if (zipEntryInformation != null && zipEntryInformation.needToSave) {
                            // System.out.println(String.format("found added
                            // entry, key=%s(%s/%s)", new Object[] {
                            // zipEntryName, oldZipEntryHash, newZipEntryHash
                            // }));
                            ZipEntry newZipEntry = zipEntryInformation.zipEntry == null ? new ZipEntry(zipEntryName) : zipEntryInformation.zipEntry;
                            addZipEntry(zipOutputStream, newZipEntry, zipEntryInformation.inputStream == null ? newZipFile.getInputStream(zipEntry) : zipEntryInformation.inputStream);
                        }
                    } else if (!newZipEntryHash.equals(oldZipEntryHash)) {
                        DifferZipProcessor.ZipEntryInformation zipEntryInformation = null;
                        if (differZipProcessor != null) {
                            zipEntryInformation = differZipProcessor.foundModifiedZipEntryProcess(zipEntryName);
                        }
                        if (zipEntryInformation != null && zipEntryInformation.needToSave) {
                            // System.out.println(String.format("found modified
                            // entry, key=%s(%s/%s)", new Object[] {
                            // zipEntryName, oldZipEntryHash, newZipEntryHash
                            // }));
                            ZipEntry newZipEntry = zipEntryInformation.zipEntry == null ? new ZipEntry(zipEntryName) : zipEntryInformation.zipEntry;
                            addZipEntry(zipOutputStream, newZipEntry, zipEntryInformation.inputStream == null ? newZipFile.getInputStream(zipEntry) : zipEntryInformation.inputStream);
                        }
                    }
                    map.remove(zipEntryName);
                }
            }
            Set<String> deleteKeySet = map.keySet();
            for (String deleteKey : deleteKeySet) {
                if (differZipProcessor != null) {
                    differZipProcessor.foundDeletedZipEntryProcess(deleteKey);
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
     * 
     * @param differenOutputDirectory
     * @param oldDirectory
     * @param newDirectory
     */
    public static void differDirectory(String differenOutputDirectory, String oldDirectory, String newDirectory) {
        List<String> oldFileList = FileUtil.findMatchFile(new MatchOption(oldDirectory));
        String oldDirectoryAbsolutePath = new File(oldDirectory).getAbsolutePath();
        Map<String, String> oldFileMD5Map = new HashMap<String, String>();
        differenOutputDirectory = new File(differenOutputDirectory).getAbsolutePath();
        for (String oldFile : oldFileList) {
            String key = new File(oldFile).getAbsolutePath().substring(oldDirectoryAbsolutePath.length() + 1);
            key = key.replace(Constant.Symbol.SLASH_RIGHT, Constant.Symbol.SLASH_LEFT);
            String value = Generator.MD5File(oldFile);
            oldFileMD5Map.put(key, value);
        }
        List<String> newFileList = FileUtil.findMatchFile(new MatchOption(newDirectory));
        String newDirectoryAbsolutePath = new File(newDirectory).getAbsolutePath();
        for (String newFile : newFileList) {
            String key = new File(newFile).getAbsolutePath().substring(newDirectoryAbsolutePath.length() + 1);
            key = key.replace(Constant.Symbol.SLASH_RIGHT, Constant.Symbol.SLASH_LEFT);
            String value = Generator.MD5File(newFile);
            String oldValue = oldFileMD5Map.get(key);
            if (oldValue == null || (!oldValue.equals(value))) {
                String toFile = differenOutputDirectory + Constant.Symbol.SLASH_LEFT + key;
                // System.out.println("key:"+key+",oldValue:"+oldValue+",value:"+value);
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
     * 
     * @param directoryList
     * @param cacheProperties
     * @param fileSuffix,file
     *            suffix it will search file in source directory list
     * @param isFile
     *            if true the return list is source file else is the source
     *            directory
     * @return List<String>
     */
    public static List<String> findFileListWithCache(List<String> directoryList, Properties cacheProperties, String fileSuffix, boolean isFile) {
        return findFileListWithCache(directoryList, cacheProperties, fileSuffix, isFile, null);
    }

    /**
     * find file list with cache
     * 
     * @param directoryList
     * @param cacheProperties
     * @param fileSuffix
     * @param isFile
     * @param cacheProcessor
     * @return List<String>
     */
    public static List<String> findFileListWithCache(List<String> directoryList, Properties cacheProperties, String fileSuffix, boolean isFile, CacheProcessor cacheProcessor) {
        return findFileListWithCache(directoryList, cacheProperties, fileSuffix, isFile, false, cacheProcessor);
    }

    /**
     * find file list with cache
     * 
     * @param directoryList
     * @param cacheProperties
     * @param fileSuffix,file
     *            suffix it will search file in source directory list
     * @param findFile
     *            if true the return list is source file else is the source
     *            directory
     * @param includeHidden
     * @return List<String>
     */
    public static List<String> findFileListWithCache(List<String> directoryList, Properties cacheProperties, String fileSuffix, boolean findFile, boolean includeHidden, CacheProcessor cacheProcessor) {
        List<String> sourceList = new ArrayList<String>();
        // no cache
        if (cacheProperties == null) {
            if (directoryList != null && !directoryList.isEmpty()) {
                for (String directory : directoryList) {
                    MatchOption matchOption = new MatchOption(directory);
                    matchOption.fileSuffix = fileSuffix;
                    matchOption.includeHidden = includeHidden;
                    matchOption.findType = findFile ? MatchOption.FindType.FILE : MatchOption.FindType.FILE_DIRECTORY;
                    sourceList.addAll(findMatchFileOrMatchFileDirectory(matchOption));
                }
            }
        } else if (cacheProperties.isEmpty()) {
            List<String> fileList = new ArrayList<String>();
            if (directoryList != null && !directoryList.isEmpty()) {
                for (String directory : directoryList) {
                    MatchOption matchOption = new MatchOption(directory);
                    matchOption.fileSuffix = fileSuffix;
                    matchOption.includeHidden = includeHidden;
                    fileList.addAll(FileUtil.findMatchFile(matchOption));
                }
            }
            for (String fullFilename : fileList) {
                String cacheKey = fullFilename;
                if (cacheProcessor != null) {
                    cacheKey = cacheProcessor.keyProcess(cacheKey);
                }
                cacheProperties.setProperty(cacheKey, Generator.MD5File(fullFilename));
            }
            if (findFile) {
                sourceList.addAll(fileList);
            } else {
                if (directoryList != null && !directoryList.isEmpty()) {
                    for (String directory : directoryList) {
                        MatchOption matchOption = new MatchOption(directory);
                        matchOption.fileSuffix = fileSuffix;
                        matchOption.includeHidden = includeHidden;
                        sourceList.addAll(findMatchFileDirectory(matchOption));
                    }
                }
            }
        } else {// with cache
            List<String> fileList = new ArrayList<String>();
            if (directoryList != null && !directoryList.isEmpty()) {
                for (String directory : directoryList) {
                    MatchOption matchOption = new MatchOption(directory);
                    matchOption.fileSuffix = fileSuffix;
                    matchOption.includeHidden = includeHidden;
                    fileList.addAll(findMatchFile(matchOption));
                }
            }
            for (String fullFilename : fileList) {
                String cacheKey = fullFilename;
                if (cacheProcessor != null) {
                    cacheKey = cacheProcessor.keyProcess(cacheKey);
                }
                String sourceFileMd5 = Generator.MD5File(fullFilename);
                if (cacheProperties.containsKey(cacheKey)) {
                    String md5 = cacheProperties.getProperty(cacheKey);
                    if (!sourceFileMd5.equals(md5)) {
                        sourceList.add(fullFilename);
                        cacheProperties.setProperty(cacheKey, sourceFileMd5);
                    }
                } else {
                    sourceList.add(fullFilename);
                    cacheProperties.setProperty(cacheKey, sourceFileMd5);
                }
            }
        }
        return sourceList;
    }

    /**
     * deal with file cache
     * 
     * @param propertiesFileMappingFullFilename
     * @param noCacheFileFinder
     * @param noCacheFileProcessor
     * @return List<String>
     */
    public static List<String> dealWithFileCache(String propertiesFileMappingFullFilename, NoCacheFileFinder noCacheFileFinder, NoCacheFileProcessor noCacheFileProcessor) {
        Properties propertiesFileMapping = getPropertiesAutoCreate(propertiesFileMappingFullFilename);
        List<String> noCacheFileList = null;
        if (noCacheFileFinder == null) {
            throw new NullPointerException("noCacheFileFinder can not be null.");
        }
        noCacheFileList = noCacheFileFinder.findNoCacheFileList(propertiesFileMapping);
        boolean saveCache = false;
        if (noCacheFileProcessor != null) {
            saveCache = noCacheFileProcessor.process(noCacheFileList);
        }
        if (saveCache) {
            saveProperties(propertiesFileMapping, propertiesFileMappingFullFilename);
        }
        return noCacheFileList;
    }

    /**
     * get properties,if is not exist will auto create
     * 
     * @param propertiesFullFilename
     * @return Properties
     */
    public static Properties getPropertiesAutoCreate(String propertiesFullFilename) {
        if (!FileUtil.isExist(propertiesFullFilename)) {
            FileUtil.createFile(propertiesFullFilename);
        }
        return getProperties(propertiesFullFilename);
    }

    /**
     * get properties
     * 
     * @param propertiesFullFilename
     * @return Properties
     */
    public static Properties getProperties(String propertiesFullFilename) {
        Properties properties = null;
        if (propertiesFullFilename != null) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(propertiesFullFilename);
                properties = new Properties();
                properties.load(inputStream);
            } catch (Exception e) {
                throw new FileUtilException(e);
            } finally {
                if (inputStream != null) {
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
     * 
     * @param file
     * @return Properties
     * @throws IOException
     */
    public static final Properties getProperties(File file) {
        Properties properties = null;
        if (file != null) {
            properties = getProperties(file.getAbsolutePath());
        }
        return properties;
    }

    /**
     * save properties
     * 
     * @param properties
     * @param outputFullFilename
     */
    public static void saveProperties(Properties properties, String outputFullFilename) {
        if (properties != null && outputFullFilename != null) {
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(outputFullFilename);
                properties.store(outputStream, null);
            } catch (Exception e) {
                throw new FileUtilException(e);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush();
                        outputStream.close();
                    } catch (Exception e) {
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

    public static interface ZipProcessor {

        /**
         * zip entry process
         * 
         * @param zipEntryName
         * @param inputStream
         * @return InputStream
         */
        public abstract InputStream zipEntryProcess(final String zipEntryName, InputStream inputStream);
    }

    public static abstract interface CacheProcessor {
        /**
         * key process,can change key to save cache
         * 
         * @param cacheKey
         * @return String
         */
        public abstract String keyProcess(final String key);
    }

    public static abstract interface NoCacheFileProcessor {
        /**
         * process
         * 
         * @param uncachedFileList
         * @return boolean,true is save cache else false
         */
        public abstract boolean process(List<String> uncachedFileList);
    }

    public static abstract interface NoCacheFileFinder {

        /**
         * find no cache file list
         * 
         * @param cacheFileMapping
         * @return List<String>
         */
        public abstract List<String> findNoCacheFileList(Properties cacheFileMapping);
    }

    /**
     * match option
     */
    public static class MatchOption {
        private static enum FindType {
            FILE, DIRECTORY, FILE_DIRECTORY
        }

        public final String directory;
        public String fileSuffix = null;
        private FindType findType = FindType.FILE;
        public boolean includeHidden = false;
        public boolean deep = true;
        public Processor processor = null;

        public MatchOption(String directory) {
            this.directory = directory;
        }

        public static interface Processor {
            /**
             * on match
             * 
             * @param file
             * @return String,return null then ignore the match file
             */
            public abstract String onMatch(File file);
        }
    }

    public static abstract interface DifferZipProcessor {
        /**
         * found added zip entry process
         * 
         * @param zipEntryName
         * @return boolean true is need to save in different.zip
         */
        public abstract ZipEntryInformation foundAddedZipEntryProcess(String zipEntryName);

        /**
         * found modified zip entry process
         * 
         * @param zipEntryName
         * @return boolean true is need to save in different.zip
         */
        public abstract ZipEntryInformation foundModifiedZipEntryProcess(String zipEntryName);

        /**
         * found deleted zip entry process
         * 
         * @param zipEntryName
         */
        public abstract void foundDeletedZipEntryProcess(String zipEntryName);

        public static class ZipEntryInformation {
            public final boolean needToSave;
            public final ZipEntry zipEntry;
            public final InputStream inputStream;

            public ZipEntryInformation(boolean needToSave, ZipEntry zipEntry, InputStream inputStream) {
                this.needToSave = needToSave;
                this.zipEntry = zipEntry;
                this.inputStream = inputStream;
            }
        }
    }

    public static abstract interface ReadFileContentProcessor {
        /**
         * after read line
         * 
         * @param line
         * @return boolean, if true continue read, false break read
         */
        public abstract boolean afterReadLine(String line);
    }

    public static abstract interface WriteFileContentProcessor {
        /**
         * write content
         * 
         * @param bufferedWriter
         * @throws Exception
         */
        public abstract void writeContent(BufferedWriter bufferedWriter) throws Exception;
    }
}
