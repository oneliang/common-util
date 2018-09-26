package com.oneliang.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import com.oneliang.Constants;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.FileUtil;

public class FileCaculator {
    private static class Counter {
        int count = 0;
    }

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream("/D:/log.txt")));
        final String directory = "/E:/Dandelion/java/gitOtherWorkspace/biz-app";
        final Counter ktLineCounter = new Counter();
        final Counter javaLineCounter = new Counter();
        final Counter ktCounter = new Counter();
        final Counter javaCounter = new Counter();
        final FileUtil.MatchOption matchOption = new FileUtil.MatchOption(directory);
        matchOption.processor = new FileUtil.MatchOption.Processor() {
            public String onMatch(File file) {
                final String filename = file.getName();
                final String fileAbsolutePath = file.getAbsolutePath().replace("\\", "/");
                if (StringUtil.isMatchRegex(fileAbsolutePath, "src/[\\w\\W]+\\.kt$")) {
                    System.out.println(filename);
                    ktCounter.count++;
                    FileUtil.readFileContentIgnoreLine(fileAbsolutePath, Constants.Encoding.UTF8, new FileUtil.ReadFileContentProcessor() {
                        @Override
                        public boolean afterReadLine(String line) {
                            if (!line.isEmpty()) {
                                ktLineCounter.count++;
                            }
                            return true;
                        }
                    });
                } else if (StringUtil.isMatchRegex(fileAbsolutePath, "src/[\\w\\W]+\\.java$")) {
                    System.out.println(filename);
                    javaCounter.count++;
                    FileUtil.readFileContentIgnoreLine(fileAbsolutePath, Constants.Encoding.UTF8, new FileUtil.ReadFileContentProcessor() {
                        @Override
                        public boolean afterReadLine(String line) {
                            if (!line.isEmpty()) {
                                javaLineCounter.count++;
                            }
                            return true;
                        }
                    });
                }
                return file.getAbsolutePath();
            }
        };
        List<String> fileList = FileUtil.findMatchFile(matchOption);
        System.out.println(String.format("total:%s, kt:%s, java:%s, kt line:%s, java line:%s", fileList.size(), ktCounter.count, javaCounter.count, ktLineCounter.count, javaLineCounter.count));
    }
}
