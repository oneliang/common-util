package com.oneliang.toolkit.seperator;

import java.util.ArrayList;
import java.util.List;

import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.FileUtil;

public class Main {

    public static void main(String[] args) {
        String string = "[I][2017-10-10 +8.0 23:50:44.677][12453, 1*][MicroMsg.Alarm][, , 12453][Alarm.onReceive [id: 69, delta miss time: -145356, size: 0]";
        String regex = "\\[[\\w+-.:\\s,*]+\\]";
        List<String> groupList = StringUtil.parseStringGroup(string, regex);
        for (String group : groupList) {
            System.out.println(group);
        }

        System.exit(0);
        String copyFrom = "/D:/Dandelion/java/hardcoder";
        String copyTo = "/D:/hardcoder";
        FileUtil.deleteAllFile(copyTo);
        final List<SeperatorConfig> seperatorConfigList = new ArrayList<SeperatorConfig>();
        seperatorConfigList.add(new SeperatorConfig("*.java", "//begin", "//end"));
        FileUtil.copyFile(copyFrom, copyTo, FileUtil.FileCopyType.PATH_TO_PATH, new FileContentSeperator(seperatorConfigList));
    }
}
