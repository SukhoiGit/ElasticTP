package com.artemis.elastictp.config.common.starter.configuration;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.boot.info.BuildProperties;

public class ElasticTpBannerHandler implements InitializingBean {

    private static final String DYNAMIC_THREAD_POOL = " :: Dynamic ThreadPool :: ";
    private static final String ONE_THREAD_DASHBOARD = "Git:    https://github.com/SukhoiGit/ElasticTP";
    private static final String ONE_THREAD_SITE = "Site:   https://github.com/SukhoiGit/ElasticTP";
    private static final int STRAP_LINE_SIZE = 50;
    private final String version;

    public ElasticTpBannerHandler(BuildProperties buildProperties) {
        this.version = buildProperties != null ? buildProperties.getVersion() : "";
    }

    @Override
    public void afterPropertiesSet() {
        String banner = """
                       _           _   _   _______   \s
                      | |         | | (_) |__   __|  \s
                   ___| | __ _ ___| |_ _  ___| |_ __ \s
                  / _ \\ |/ _` / __| __| |/ __| | '_ \\\s
                 |  __/ | (_| \\__ \\ |_| | (__| | |_) |
                  \\___|_|\\__,_|___/\\__|_|\\___|_| .__/\s
                                               | |   \s
                                               |_|   \s
                """;
        String bannerVersion = StrUtil.isNotEmpty(version) ? " (v" + version + ")" : "no version.";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE - (bannerVersion.length() + DYNAMIC_THREAD_POOL.length())) {
            padding.append(" ");
        }
        System.out.println(AnsiOutput.toString(banner, AnsiColor.GREEN, DYNAMIC_THREAD_POOL, AnsiColor.DEFAULT,
                padding.toString(), AnsiStyle.FAINT, bannerVersion, "\n\n", ONE_THREAD_DASHBOARD, "\n", ONE_THREAD_SITE, "\n"));
    }
}