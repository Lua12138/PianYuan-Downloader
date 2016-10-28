package com.github.gam2046.pianyuan.downloader.bin;

import com.github.gam2046.pianyuan.downloader.helper.Spider;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by forDream on 2016/10/25.
 */
public class DownloadSpy {
    private final String cookie;

    public enum Type {
        btLink(Pattern.compile("/dlbt/[^\"]+")),
        magnetLink(Pattern.compile("magnet:\\?[^\"]+"));
        private Pattern pattern;

        Type(Pattern pattern) {
            this.pattern = pattern;
        }
    }

    private final List<String> list;
    private final static String host = "http://pianyuan.net/";

    public DownloadSpy(List<String> list, String cookie) {
        this.list = list;
        this.cookie = cookie;
    }

    public List<String> getIt(Type downloadType) throws IOException {
        List<String> result = Collections.synchronizedList(new ArrayList());
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Cookie", this.cookie);

        for (String link : this.list) {
            Spider.newHost(new URL(this.host + link))
                    .setRequestHeaders(requestHeaders)
                    .asyn()
                    .get((responseCode, responseHeaders, responseStream) -> {
                        if (responseCode == 200) {
                            String html = responseStream.toString();

                            Matcher matcher = downloadType.pattern.matcher(html);
                            while (matcher.find()) {
                                String lk = matcher.group();
                                //System.out.print("Find Link -> ");
                                //System.out.println(lk);
                                if (Type.btLink.equals(downloadType))
                                    result.add(DownloadSpy.this.host + lk);
                                else
                                    result.add(lk);
                            }
                        }
                        return responseCode;
                    });
        }

        // 等待网络请求全部完成
        while (result.size() != this.list.size())
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        return result;
    }
}
