package com.brokepal.constant;

import java.io.File;

/**
 * Created by Administrator on 2016/11/10.
 */
public final class Const {
    /**
     * 支持的语言有：CHINESE（中文）、ENGLISH（英文）
     */
    public static final class Language{
        public static final String CHINESE = "zh";
        public static final String ENGLISH = "en";
        public static final String stringXmlFilePath;
        static {
            String root = Language.class.getResource("/").getFile().toString();
            File file = new File(root);
            String targetaPath = file.getParent();
            stringXmlFilePath = targetaPath + "/classes/string/";
        }
    }

    public static final class EmailConfig{
        public static final String emailXmlFilePath;
        static {
            String root = EmailConfig.class.getResource("/").getFile().toString();
            File file = new File(root);
            String targetaPath = file.getParent();
            emailXmlFilePath = targetaPath + "/classes/smtp-config.xml";
        }
    }
}
