package com.j.openproject.auth.code;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Joyuce
 * @Type AuthCode
 * @Desc 权限代码 不建议枚举类实现，关联上下级需要循环遍历，浪费性能
 * @date 2019年11月20日
 * @Version V1.0
 */
public class AuthCode {

    private static final Map<String, Body> map = new ConcurrentHashMap<>();

    /**
     * 保存权限枚举
     *
     * @param code       权限
     * @param detail     详情
     * @param fatherCode 父级权限
     */
    public static void saveCode(String code, String detail, String fatherCode) {
        map.put(code, new Body(detail, fatherCode));
    }

    /**
     * 获取权限描述
     *
     * @param code 子级code
     * @return 详情
     */
    public static String getCodeDetail(String code) {
        if (map.containsKey(code)) {
            return map.get(code).getDetail();
        } else {
            return null;
        }
    }

    /**
     * 获取父级code
     *
     * @param code 子级code
     * @return 父级权限
     */
    public static String getFatherCode(String code) {
        if (map.containsKey(code)) {
            return map.get(code).getFatherCode();
        } else {
            return null;
        }
    }

    static class Body {
        private String detail;
        private String fatherCode;

        public Body(String detail, String fatherCode) {
            this.detail = detail;
            this.fatherCode = fatherCode;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getFatherCode() {
            return fatherCode;
        }

        public void setFatherCode(String fatherCode) {
            this.fatherCode = fatherCode;
        }
    }

}
