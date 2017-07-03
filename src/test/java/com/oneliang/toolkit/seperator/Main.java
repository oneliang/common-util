package com.oneliang.toolkit.seperator;

import java.util.ArrayList;
import java.util.List;

import com.oneliang.util.file.FileUtil;

public class Main {

    public static void main(String[] args) {
        String copyFrom = "/D:/Dandelion/java/hardcoder";
        String copyTo = "/D:/hardcoder";
        FileUtil.deleteAllFile(copyTo);
        final List<SeperatorConfig> seperatorConfigList = new ArrayList<SeperatorConfig>();
        seperatorConfigList.add(new SeperatorConfig("*.java", "//begin", "//end"));
        FileUtil.copyFile(copyFrom, copyTo, FileUtil.FileCopyType.PATH_TO_PATH, new FileContentSeperator(seperatorConfigList));
    }
}
