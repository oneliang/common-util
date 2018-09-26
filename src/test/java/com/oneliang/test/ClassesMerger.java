package com.oneliang.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

import com.oneliang.Constants;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.FileUtil;
import com.oneliang.util.file.FileUtil.MatchOption;

public class ClassesMerger {

	public void mergeClasses() throws Exception {
		String inputFullFilename = "/D:/Dandelion/git/wechat/boot/build/intermediates/transforms/customProguard/debug/jars/3/1f/inputList1501143421616.jar";
		String outputJarFullFilename = "/D:/main.jar";
		FileUtil.createFile(outputJarFullFilename);
		List<String> mergeZipFullFilenameList = new ArrayList<String>();
		List<String> classesDirectoryList = new ArrayList<String>();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFullFilename)));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			if(StringUtil.isBlank(line)){
				continue;
			}
			String fullFilename = line.trim();
			File file = new File(fullFilename);
			if (file.isFile() && file.getName().endsWith(".jar")) {
				mergeZipFullFilenameList.add(fullFilename);
			} else if (file.isDirectory()) {
				classesDirectoryList.add(fullFilename);
				String newJar = "/D:/" + file.getName() + ".jar";
				FileUtil.zip(newJar, fullFilename);
				mergeZipFullFilenameList.add(newJar);
			}
		}
		bufferedReader.close();
		FileUtil.mergeZip(outputJarFullFilename, mergeZipFullFilenameList);
	}

	public static void main(String[] args) throws Exception {
		new ClassesMerger().mergeClasses();
	}
}
