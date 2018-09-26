package com.oneliang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneliang.util.common.MathUtil;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.common.TimeUtil;
import com.oneliang.util.file.FileUtil;
import com.oneliang.util.json.JsonObject;

public class Test {

    private static String toKvStatJson(long currentServerTime, int[] kvReportCounter) {
        String json = null;
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("time", currentServerTime);
            jsonObject.put("count", kvReportCounter);
            json = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static class Bean {
        int id = 1;
        String msg = "";
    }

    public static void main(String[] args) throws Exception {
        System.out.println(TimeUtil.timeMillisToDate(1537238066000l));
        System.out.println(0x21000011);
        System.exit(0);
        System.out.println(0 | 1 | 2 | 4);
        System.out.println(0x21000011);
        System.out.println(MathUtil.intToUnsignedInt(-485159075));
        TreeSet<Bean> treeSet = new TreeSet<Bean>(new Comparator<Bean>() {

            public int compare(Bean o1, Bean o2) {
                return o1.id - o2.id;
            }
        });
        Bean beanA = new Bean();
        beanA.msg = "a";
        treeSet.add(beanA);

        Bean beanB = new Bean();
        beanB.id = 1;
        beanB.msg = "b";
        treeSet.add(beanB);

        System.out.println(treeSet.remove(beanA));
        System.out.println(treeSet.size());
        System.exit(0);
        int argb = -16736417;
        System.out.println(Integer.toHexString(argb));
        // if ((0xFF000000 & argb) >= 0) {
        // System.out.println(String.format("#%08X", 0x00FFFFFF & argb));
        // } else {
        System.out.println(argb);
        System.out.println(String.format("#%06X", 0xFFFFFF & argb));
        // }
        System.exit(0);
        System.out.println(TimeUtil.timeMillisToDate(1533279433000l));
        JsonFactory jsonFactory = new JsonFactory();
        // ByteArrayOutputStream byteArrayOutputStream = new
        // ByteArrayOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper = new ObjectMapper();
        try {
            JsonGenerator jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);
            TestBean testBean = new TestBean();
            testBean.setId("id");
            testBean.setName("name");
            jsonGenerator.writeObject(testBean);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // System.out.println(byteArrayOutputStream.size());
        System.exit(0);
        // hexStringToByteArray();
        // System.out.println(Math.ceil((double) 64 / 64));
        // System.out.println(Integer.MIN_VALUE);
        // System.out.println((long) Math.abs(Integer.MIN_VALUE) * -1 + (long)
        // Math.abs(Integer.MIN_VALUE) * -1 - 744086640);
        // 2390040527
        // -1904926769
        // 242556880 1403397009
        byte[] byteArray = MathUtil.intToByteArray(-1904926769);
        byte[] longByteArray = new byte[8];
        System.arraycopy(byteArray, 0, longByteArray, 4, 4);
        System.out.println(MathUtil.byteArrayToLong(longByteArray));
        byteArray = MathUtil.intToByteArray(-721329523);
        longByteArray = new byte[8];
        System.arraycopy(byteArray, 0, longByteArray, 4, 4);
        System.out.println(MathUtil.byteArrayToLong(longByteArray));
        byteArray = MathUtil.intToByteArray(-699328304);
        longByteArray = new byte[8];
        System.arraycopy(byteArray, 0, longByteArray, 4, 4);
        System.out.println(MathUtil.byteArrayToLong(longByteArray));
        long data = 4294967295l;
        longByteArray = MathUtil.longToByteArray(data);
        byteArray = new byte[4];
        System.arraycopy(longByteArray, 4, byteArray, 0, 4);
        for (int i = 0; i < byteArray.length; i++) {
            System.out.println(byteArray[i]);
        }
        System.out.println(MathUtil.byteArrayToInt(byteArray));
        System.exit(0);
        List<String> a = new ArrayList<>();
        // System.out.println(MathUtil.scaleStringToInt("26051086", 16));
        // System.out.println(MathUtil.intToScaleString(16842904, 16));
        System.out.println("svr time:" + TimeUtil.timeMillisToDate(1508136732519l) + ",msg svr time:" + TimeUtil.timeMillisToDate(1500937610000l));
        System.exit(0);
        String json = toKvStatJson(10000000l, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 });
        System.out.println(json);
        JsonObject jsonObject = new JsonObject(json);
        System.out.println(jsonObject.getLong("time"));
        System.out.println(jsonObject.getJsonArray("count").length());

        List<TestObject> testObjectList = new CopyOnWriteArrayList<>();
        TestObject testObject = new TestObject();
        testObject.field = "fieldA";
        testObjectList.add(testObject);

        testObject = new TestObject();
        testObject.field = "fieldB";
        testObjectList.add(testObject);

        testObject = new TestObject();
        testObject.field = "fieldC";
        testObjectList.add(testObject);

        testObject = new TestObject();
        testObject.field = "fieldD";
        testObjectList.add(testObject);
        for (TestObject object : testObjectList) {
            if (object.field.equals("fieldC")) {
                testObjectList.remove(object);
            }
        }
        System.out.println(testObjectList.size());
    }

    private static void hexStringToByteArray() {
        String hexStringFile = "C:/Users/oneliang/Desktop/hex.txt";
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileUtil.readFileContentIgnoreLine(hexStringFile, Constants.Encoding.UTF8, new FileUtil.ReadFileContentProcessor() {
            public boolean afterReadLine(String line) {
                String[] stringArray = line.trim().split(StringUtil.SPACE);
                if (stringArray == null) {
                    return false;
                }
                for (String string : stringArray) {
                    byte[] byteArray = StringUtil.hexStringToByteArray(string);
                    if (byteArray == null || byteArray.length == 0) {
                        System.out.println("error:" + line);
                        continue;
                    }
                    byteArrayOutputStream.write(byteArray[0]);
                }

                return true;
            }
        });
        FileUtil.writeFile("C:/Users/oneliang/Desktop/data", byteArrayOutputStream.toByteArray());
    }

    static class TestObject {
        public String field = null;
    }
}
