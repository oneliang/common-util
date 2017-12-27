package com.oneliang.toolkit.seperator;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.oneliang.Constant;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.DefaultFileCopyProcessor;
import com.oneliang.util.file.FileUtil;
import com.oneliang.util.file.FileUtil.ReadFileContentProcessor;

public class FileContentSeperator extends DefaultFileCopyProcessor {

    private List<SeperatorConfig> seperatorConfigList = null;

    public FileContentSeperator(List<SeperatorConfig> seperatorConfigList) {
        this.seperatorConfigList = seperatorConfigList;
    }

    public boolean copyFileToFileProcess(String from, String to, boolean isFile) {
        File fromFile = new File(from);
        String filename = fromFile.getName();
        if (!isFile) {
            if (filename.equals(".git")) {
                return false;
            }
            return super.copyFileToFileProcess(from, to, isFile);
        }
        boolean isMatch = false;
        for (final SeperatorConfig seperatorConfig : seperatorConfigList) {
            if (StringUtil.isMatchPattern(filename, seperatorConfig.suffix)) {
                isMatch = true;
                final StringBuilder content = new StringBuilder();
                FileUtil.readFileContentIgnoreLine(from, Constant.Encoding.UTF8, new ReadFileContentProcessor() {
                    private boolean needToCollect = false;

                    public boolean afterReadLine(String line) {
                        if (line.startsWith(seperatorConfig.beginMark)) {
                            needToCollect = true;
                        } else if (line.startsWith(seperatorConfig.endMark)) {
                            needToCollect = false;
                        } else {
                            if (needToCollect) {
                                content.append(line);
                                content.append(StringUtil.CRLF_STRING);
                            }
                        }
                        return true;
                    }
                });
                if (content.toString().length() > 0) {
                    try {
                        FileUtil.writeFile(to, content.toString().getBytes(Constant.Encoding.UTF8));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    return super.copyFileToFileProcess(from, to, isFile);
                }
            }
        }
        if (!isMatch) {
            return super.copyFileToFileProcess(from, to, isFile);
        }
        return true;
    }
}
