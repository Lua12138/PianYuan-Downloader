package com.github.gam2046.pianyuan.downloader.bin;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by forDream on 2016/10/25.
 */
public class MainClass {
    private final String cookie;
    private final String url;
    private final String format;

    private MainClass(String url, String cookie, String format) {
        this.url = url;
        this.cookie = cookie;
        this.format = format;
    }

    private void run() throws IOException {
        List<String> list = new DownloadListSpy(this.url, this.cookie).getIt(DownloadSpy.Type.magnetLink);

        System.out.println();
        for (String str : list) {
            System.out.printf(this.format, str);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input the url, and press Enter to finish:");
        String url = scanner.nextLine();

        System.out.print("Input the cookie of web site, and press the Enter to finish:");
        String cookie = scanner.nextLine();

        System.out.print("Input the format of output, and press the Enter to finish.");
        String format = scanner.nextLine();
        if (format == null || format.length() < 3) format = "%s%n";

        new MainClass(url, cookie, format).run();
    }
}
