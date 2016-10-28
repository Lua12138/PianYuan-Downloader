package com.github.gam2046.pianyuan.downloader.bin;

import com.github.gam2046.pianyuan.downloader.helper.Spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.RecursiveTask;

/**
 * Created by forDream on 2016/11/1.
 */
public class BtDownloader extends RecursiveTask<Boolean> {
    private final String[] url;

    public BtDownloader(String[] url) {
        this.url = url;
    }

    @Override
    protected Boolean compute() {
        File locate = new File(System.getenv("TEMP") + "/PianYuanDownloader/");
        if (this.url.length > 1) {
            BtDownloader[] downloaders = new BtDownloader[this.url.length];
            for (int i = 0; i < this.url.length; i++) {
                downloaders[i] = new BtDownloader(new String[]{this.url[i]});
                downloaders[i].fork();
            }

            int fail = 0;
            for (int i = 0; i < downloaders.length; i++) {
                //if (downloaders[i] == null) continue;
                if (!downloaders[i].join()) {
                    fail++;
                    System.err.printf("Download err -> %s%n", this.url[i]);
                }
            }

            System.err.printf("Total/Success -> %d / %d, All finished in %s%n",
                    this.url.length, this.url.length - fail, locate);
        } else {
            try {
                if (!locate.exists()) locate.mkdirs();
                if (Spider.newHost(new URL(this.url[0]))
                        // 无需cookie
                        .get((responseCode, responseHeaders, responseStream) -> {
                            if (responseCode == 200) {
                                if (responseHeaders.get("Accept-Ranges").get(0).indexOf("bytes") != -1) {
                                    String filename = responseHeaders.get("Content-Disposition").get(0).split("; ")[1].split("=")[1].replace("\"", "");
                                    OutputStream os = new FileOutputStream(locate.getAbsolutePath() + "/" + filename);
                                    byte[] buffer = new byte[4096];
                                    int len;
                                    while (-1 != (len = responseStream.read(buffer, 0, buffer.length)))
                                        os.write(buffer, 0, len);

                                    os.close();
                                    System.err.printf("Download finish -> %s%n", filename);
                                } else {
                                    System.err.println("Response Header Err -> " + responseHeaders);
                                }
                            } else
                                System.err.println("Response Code Err -> " + responseCode);
                            return responseCode;
                        }).requestValue() == 200) return true;
            } catch (SocketTimeoutException e) {
                System.err.println("Request timed out");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
