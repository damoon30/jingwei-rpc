package com.jingwei.rpc.core.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author ipipman
 * @version V1.0
 * @date 2020/2/8
 * @date 2020/2/8 20:53
 * @Description 工具集合类
 */
@Slf4j
public class ToolUtils {

    public static final ThreadLocal<String> threadLocal = new ThreadLocal<>();
    /**
     * 获取随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = null;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }

        return sb.toString();
    }

    /**
     * md5Hex 加密
     */
    public static String md5Hex(String password, String salt) {
        return md5Hex(password + salt);
    }

    /**
     * Md5-Token加密算法
     */
    public static String md5Hex(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        byte[] bs = md5.digest(str.getBytes());
        StringBuilder md5StrBuff = new StringBuilder();

        for (byte b : bs) {
            if (Integer.toHexString(255 & b).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(255 & b));
            } else {
                md5StrBuff.append(Integer.toHexString(255 & b));
            }
        }
        return md5StrBuff.toString();
    }

    /**
     * 获取异常信息
     */
    public static String getExceptionMsg(Throwable e) throws IOException {
        StringWriter sw = new StringWriter();

        try {
            e.printStackTrace(new PrintWriter(sw));
        } finally {
            sw.close();
        }

        return sw.getBuffer().toString().replaceAll("\\$", "T");
    }

    /**
     * 获取当前IP
     *
     * @return
     */
    public static String getIp() {
        try {
            StringBuilder ifConfig = new StringBuilder();
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();

                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress == null) {
                        throw new NullPointerException("inetAddress is null");
                    }
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        ifConfig.append(inetAddress.getHostAddress().toString());
                        ifConfig.append("\n");
                    }
                }
            }

            return ifConfig.toString();
        } catch (SocketException var6) {
            log.error("{},{}", "SocketException=", var6);
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException var5) {
                log.error("{},{}", "UnknownHostException=", var5);
                return null;
            }
        }
    }


    /**
     * 判断win操作系统
     *
     * @return
     */
    public static Boolean isWinOs() {
        String os = System.getProperty("os.name");
        if (os == null) {
            throw new NullPointerException("os.name is null");
        }
        return os.toLowerCase().startsWith("win");
    }

    /**
     * 转Int类型
     *
     * @param val
     * @return
     */
    public static Integer toInt(Object val) {
        if (val instanceof Double) {
            BigDecimal bigDecimal = BigDecimal.valueOf((Double) val);
            return bigDecimal.intValue();
        } else {
            return Integer.valueOf(val.toString());
        }
    }

    /**
     * 是不是int类型
     *
     * @param obj
     * @return
     */
    public static boolean isNum(Object obj) {
        try {
            Integer.parseInt(obj.toString());
            return true;
        } catch (Exception var2) {
            return false;
        }
    }


    /**
     * 是不是json字符串
     *
     * @param obj
     * @return boolean
     */
    public static boolean isJson(Object obj) {
        try {
            JSONObject.parseObject(obj.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是不是decimal类型
     *
     * @param obj 检测对象
     * @return boolean
     */
    public static boolean isDecimal(Object obj) {
        try {
            new BigDecimal(obj.toString());
            return true;
        } catch (Exception var2) {
            return false;
        }
    }

    /**
     * 判断不为空
     *
     * @param o
     * @return
     */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     * 判断为空
     *
     * @param o
     * @return
     */
    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else {
            if (o instanceof String) {
                return "".equals(o.toString().trim());
            } else if (o instanceof List) {
                return ((List) o).size() == 0;
            } else if (o instanceof Map) {
                if (((Map) o).size() == 0) {
                    return true;
                }
            } else if (o instanceof Set) {
                if (((Set) o).size() == 0) {
                    return true;
                }
            } else if (o instanceof Object[]) {
                if ((((Object[]) o)).length == 0) {
                    return true;
                }
            } else if (o instanceof int[]) {
                if (((int[]) ((int[]) o)).length == 0) {
                    return true;
                }
            } else if (o instanceof long[] && ((long[]) ((long[]) o)).length == 0) {
                return true;
            }

            return false;
        }
    }

    /**
     * 判断是否存在空值
     *
     * @param os
     * @return
     */
    public static boolean isOneEmpty(Object... os) {
        int var2 = os.length;

        for (Object o : os) {
            if (isEmpty(o)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否全空
     *
     * @param os
     * @return
     */
    public static boolean isAllEmpty(Object... os) {
        int var2 = os.length;

        for (Object o : os) {
            if (!isEmpty(o)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断是否全不空
     *
     * @param os
     * @return
     */
    public static boolean allNotEmpty(Object... os) {
        int var2 = os.length;

        for (Object o : os) {
            if (isEmpty(o)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取文件后缀
     *
     * @param fileWholeName
     * @return
     */
    public static String getFileSuffix(String fileWholeName) {
        if (isEmpty(fileWholeName)) {
            return "none";
        } else {
            int lastIndexOf = fileWholeName.lastIndexOf(".");
            return fileWholeName.substring(lastIndexOf + 1);
        }
    }

    /**
     * 检查文件路径是否有效
     *
     * @param filePath 文件路径
     */
    public static boolean checkFileParentDir(String filePath) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            return parentFile.mkdirs();
        }
        return false;

    }

    /**
     * 面数转A，B，C
     *
     * @param pageNumber
     * @return
     */
    public static String numToLetter(Integer pageNumber) {
        StringBuilder input = new StringBuilder();
        for (int i = 1; i <= pageNumber; i++) {
            input.append(String.valueOf(i));
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : input.toString().getBytes()) {
            sb.append((char) (b + 48)).append(",");
        }
        return sb.toString().substring(0, sb.toString().length() - 1).toUpperCase();
    }


    /**
     * 对list中的object按某些字段进行排序
     *
     * @param list
     * @param sortNameArr
     * @param typeArr
     * @return void
     */
    public static <E> void sortList(List<E> list, final String[] sortNameArr, final boolean[] typeArr) {
        if (sortNameArr.length != typeArr.length) {
            throw new RuntimeException("属性数组元素个数和升降序数组元素个数不相等");
        }
        Collections.sort(list, new Comparator<E>() {
            @Override
            public int compare(E a, E b) {
                int ret = 0;
                try {
                    for (int i = 0; i < sortNameArr.length; i++) {
                        ret = compareObject(sortNameArr[i], typeArr[i], a, b);
                        if (0 != ret) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ret;
            }
        });
    }

    /**
     * 按照字段比较两个object
     *
     * @param sortName
     * @param isAsc
     * @param objA
     * @param objB
     * @return int
     */
    private static <E> int compareObject(final String sortName, final boolean isAsc, E objA, E objB) throws Exception {
        int ret;
        Object value1 = forceGetFieldValue(objA, sortName);
        Object value2 = forceGetFieldValue(objB, sortName);
        String str1 = value1.toString();
        String str2 = value2.toString();
        if (value1 instanceof Number && value2 instanceof Number) {
            int maxlen = Math.max(str1.length(), str2.length());
            str1 = addZero2Str((Number) value1, maxlen);
            str2 = addZero2Str((Number) value2, maxlen);
        } else if (value1 instanceof Date && value2 instanceof Date) {
            long time1 = ((Date) value1).getTime();
            long time2 = ((Date) value2).getTime();
            int maxlen = Long.toString(Math.max(time1, time2)).length();
            str1 = addZero2Str(time1, maxlen);
            str2 = addZero2Str(time2, maxlen);
        }
        if (isAsc) {
            ret = str1.compareTo(str2);
        } else {
            ret = str2.compareTo(str1);
        }
        return ret;
    }

    /**
     * 获得object指定字段的value
     *
     * @param obj
     * @param fieldName
     * @return java.lang.Object
     */
    public static Object forceGetFieldValue(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        Object object = null;
        boolean accessible = field.isAccessible();
        if (!accessible) {
            // 如果是private,protected修饰的属性，需要修改为可以访问的
            field.setAccessible(true);
            object = field.get(obj);
            // 还原private,protected属性的访问性质
            field.setAccessible(accessible);
            return object;
        }
        object = field.get(obj);
        return object;
    }


    /**
     * 将数字组成的字符串补齐0
     *
     * @param numObj
     * @param length
     * @return java.lang.String
     */
    public static String addZero2Str(Number numObj, int length) {
        NumberFormat nf = NumberFormat.getInstance();
        // 设置是否使用分组
        nf.setGroupingUsed(false);
        // 设置最大整数位数
        nf.setMaximumIntegerDigits(length);
        // 设置最小整数位数
        nf.setMinimumIntegerDigits(length);
        return nf.format(numObj);
    }


    /**
     * 获取上传文件扩展名
     *
     * @param file 文件
     */
    public static String getUploadFileExtensionName(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (null == fileName) {
            return null;
        }
        // 获取图片的扩展名
        String extensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (ToolUtils.isEmpty(extensionName)) {
            return null;
        }
        return extensionName;
    }

    /**
     * 检查文件大小
     *
     * @param len
     * @param size
     * @param unit
     * @return
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;

    }


    public static String https2Http(String str) {
        return str.replaceAll("https:/", "http:/");
    }


    /**
     * 字符串是否包含中文
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        return m.find();
    }


    /**
     * object转实体，属性相同
     */
    public static <T> T readValueMap(Object obj, Class<T> valueType) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String str = null;
        try {
            str = objectMapper.writeValueAsString(obj);
            return (T) objectMapper.readValue(str, valueType);
        } catch (IOException e) {
            throw new Exception("实体转换异常，请维护！");
        }
    }

    /**
     * object转list实体，属性相同
     */
    public static <T> List<T> objConvertList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }


    /**
     * 四舍五入
     *
     * @param bigDecimal 数字
     * @return 数字
     */
    public static BigDecimal halfDown(BigDecimal bigDecimal) {
        return bigDecimal == null ? BigDecimal.ZERO : bigDecimal.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 将字符 转换为 数字
     *
     * @return 返回十进制的数
     */
    private static int toNumByChar(char c) {
        if (c >= 'A' && c <= 'H') {
            return c - 55;
        } else if (c >= 'J' && c <= 'N') {
            return c - 56;
        } else if (c >= 'P' && c <= 'R') {
            return c - 57;
        } else if (c == 'T' || c == 'U') {
            return c - 58;
        } else if (c >= 'W' && c <= 'Y') {
            return c - 59;
        } else if (c == 'I' || c == 'O' || c == 'S' || c == 'V' || c == 'Z') {
            throw new RuntimeException("无法将" + c + "转成数字");
        } else {
            return c - 48;
        }
    }


    /**
     * 去掉字符串指定的前缀
     *
     * @param str 字符串名称
     * @param pf  前缀
     * @return
     */
    public static String removePrefix(String str, String pf) {
        if (Objects.isNull(str)) {
            return null;
        } else if (ToolUtils.isEmpty(str)) {
            return "";
        } else {
            if (null != pf) {
                if (str.toLowerCase().matches("^" + pf.toLowerCase() + ".*")) {
                    return str.substring(pf.length());//截取前缀后面的字符串
                }
            }

            return str;
        }
    }


    /**
     * 加密手机号
     *
     * @param phone phone
     * @return 加密以后的手机号
     */
    public static String encryptPhone(String phone) {
        if (isEmpty(phone) || phone.length() != 11) {
            return null;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7, 11);
    }

    /**
     * 加密邮箱
     *
     * @param email 邮箱
     * @return 加密以后的邮箱
     */
    public static String encryptEmail(String email) {
        if (isEmpty(email)) {
            return null;
        }
        return email.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }


    /**
     * 校验是否最多两位小数
     *
     * @param decimal 数字
     * @return 是否最多两位小数
     */
    public static boolean checkTwoDecimalPlaces(BigDecimal decimal) {
        Pattern pattern = Pattern.compile("(^-?[1-9](\\d+)?(\\.\\d{1,2})?$)|(^-?0$)|(^-?\\d\\.\\d{1,2}$)");
        Matcher matcher = pattern.matcher(decimal.toString());
        return matcher.matches();
    }

    /**
     * 获取文件后缀名
     * @param fileName
     * @return
     */
    public static String getLastName(String fileName) {
        String[] split = fileName.split("\\.");
        if (split.length > 1) {
            return split[split.length - 1];
        } else {
            return "";
        }
    }

    /**
     * 文件转二进制bytes
     * @param file  文件
     * @return bytes
     */
    public static byte[] convertFileToBytes(File file) {
        byte[] buffer = null;
        FileInputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            is = new FileInputStream(file);
            bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = is.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }
    /**
     * 检查文件路径是否有效
     */
    public static boolean checkAndCreateFileParentDir(final String filePath) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            return parentFile.mkdirs();
        }
        return true;
    }

    public static List<Integer> splitToIntList(String bv){
        Set<Integer> bvs = new TreeSet<>();
        if(bv !=null && !"".equals(bv)) {
            bv = bv.trim().replace("，", ",");
            String[] bvSetStr = bv.split(",");
            for (String btStr : bvSetStr) {
                try {
                    btStr = btStr.trim();
                    if(btStr !="") {
                        bvs.add(Integer.valueOf(btStr));
                    }
                } catch (NumberFormatException e) {
                    log.error("split 取值转换出错 bv:{},btStr:{}",bv,btStr);
                }
            }
        }
        return new ArrayList<Integer>(bvs);
    }
}
