package com.j.openproject.utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import lombok.extern.slf4j.Slf4j;

/**
 * @author shenxiaodong
 * @Type JsoupUtil
 * @Desc
 * @date 2019年08月27日
 * @Version V1.0
 */
@Slf4j
public class JsoupUtil {

    /**
     * 使用自带的basicWithImages 白名单
     * 允许的便签有a,b,blockquote,br,cite,code,dd,dl,dt,em,i,li,ol,p,pre,q,small,span,
     * strike,strong,sub,sup,u,ul,img
     * 以及a标签的href,img标签的src,align,alt,height,width,title属性
     */
    private static final Whitelist whitelist = Whitelist.basicWithImages();
    /**
     * 配置过滤化参数,不对代码进行格式化
     */
    private static final Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);

    static {
        // 富文本编辑时一些样式是使用style来进行实现的
        // 比如红色字体 style="color:red;"
        // 所以需要给所有标签添加style属性
        whitelist.addAttributes(":all", "style");
    }

    public static String clean(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        content = content.trim();
        content = Jsoup.clean(content, "", whitelist, outputSettings);
        if (!isSqlValid(content)) {
            content = null;
        }
        return content;
    }

    private static final String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
            + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

    /**
     * 表示忽略大小写
     */
    private static final Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);

    /**
     * sql注入检查
     *
     * @param str
     * @return
     */
    public static boolean isSqlValid(String str) {
        Matcher matcher = sqlPattern.matcher(str);
        if (matcher.find()) {
            log.error("存在sql注入, 字符串已清空，请确认：" + str);
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        String text = "   <a href=\"http://www.baidu.com/a\" οnclick=\"alert(1);\">sss</a><script>alert(0);</script>sss   ";
        System.out.println(clean(text));
    }

}
