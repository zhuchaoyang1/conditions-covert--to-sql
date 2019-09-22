package com.demo.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: ${朱朝阳}
 * @Date: 2019/9/21 21:39
 */
@Component
public class DateUtil {

    /**
     * 根据format验证日期
     *
     * @param dateStr
     * @param format
     * @return
     */
    public Object valiateDate(String dateStr, String format, boolean isFirst) {
        if (dateStr.length() <= 10) {
            if (isFirst) {
                dateStr += " 00:00:00";
            } else {
                dateStr += " 23:59:59";
            }
        }

        try {
            SimpleDateFormat fmt = new SimpleDateFormat(format);
            Date dd = fmt.parse(dateStr);
            if (dateStr.equals(fmt.format(dd))) {
                return dateStr;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 处理日期
     * 去空格
     * 补时间
     *
     * @param dateStr
     * @param isFirst
     * @return
     */
    public Object handleDateStr(String dateStr, boolean isFirst) {
        if (StringUtils.isEmpty(dateStr)) {
            return false;
        }

        dateStr = dateStr.replaceAll(" ", "");

        if (dateStr.startsWith("\'")) {
            dateStr = dateStr.substring(1, dateStr.length());
        }
        if (dateStr.endsWith("\'")) {
            dateStr = dateStr.substring(0, dateStr.length() - 1);
        }

        if (dateStr.length() > 10) {
            int last = dateStr.lastIndexOf('-') + 3;
            StringBuffer stringBuffer = new StringBuffer(dateStr);
            stringBuffer.insert(last, " ");
            dateStr = stringBuffer.toString();
        }

        return valiateDate(dateStr, "yyyy-MM-dd HH:mm:ss", isFirst);
    }

}
