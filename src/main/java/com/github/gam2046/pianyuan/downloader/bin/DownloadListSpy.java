package com.github.gam2046.pianyuan.downloader.bin;

import com.github.gam2046.pianyuan.downloader.helper.Spider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by forDream on 2016/10/25.
 */
public class DownloadListSpy {
    private final String url;
    private final String cookie;
    private Pattern pattern;

    public DownloadListSpy(String url, String cookie) {
        if (!url.startsWith("http://pianyuan.net/m"))
            throw new RuntimeException(url + " -> The Url Is Not Supported.");
        this.url = url;
        this.cookie = cookie;
        this.pattern = Pattern.compile("/r_.+\\.html");
    }

    public List<String> getIt(DownloadSpy.Type downloadType) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Cookie", this.cookie);
        List<String> downloadLink = new ArrayList();
        Spider.newHost(new URL(this.url))
                .setRequestHeaders(requestHeaders)
                .get((responseCode, responseHeaders, responseStream) -> {
                    if (responseCode == 200) {
                        String html = responseStream.toString();
                        Matcher matcher = DownloadListSpy.this.pattern.matcher(html);
                        while (matcher.find()) {
                            downloadLink.add(matcher.group());
                        }
                    }
                    return responseCode;
                });

        return new DownloadSpy(downloadLink, this.cookie).getIt(downloadType);
    }
}
