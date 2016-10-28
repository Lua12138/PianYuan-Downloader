package com.github.gam2046.pianyuan.downloader.bin;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by forDream on 2016/10/25.
 */
public class MainClass {
    private final String cookie;
    private final String url;
    private final String format;
    private boolean btFlag;

    private MainClass(String url, boolean btFlag, String cookie, String format) {
        this.url = url;
        this.cookie = cookie;
        this.format = format;
        this.btFlag = btFlag;
    }

    private void run() throws IOException, ExecutionException, InterruptedException {
        List<String> list = new DownloadListSpy(this.url, this.cookie).getIt(btFlag ? DownloadSpy.Type.btLink : DownloadSpy.Type.magnetLink);

        System.out.println();

        if (btFlag) {
            ForkJoinPool pool = new ForkJoinPool();
            BtDownloader downloader = new BtDownloader(list.toArray(new String[list.size()]));
            pool.submit(downloader);
            downloader.get();
            pool.shutdown();
        } else
            for (String str : list) {
                System.out.printf(this.format, str);
            }
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        boolean btBlog;
        System.out.print("Do you want to magnet?(blank to magnet, unless BT)");
        btBlog = scanner.nextLine().length() > 0;

        System.out.print("Input the url, and press Enter to finish:");
        String url = scanner.nextLine();

        System.out.print("Input the cookie of web site, and press the Enter to finish:");
        String cookie = scanner.nextLine();

        System.out.print("Input the format of output, and press the Enter to finish.");
        String format = scanner.nextLine();

        if (format == null || format.length() < 3) format = "%s%n";

        new MainClass(url, btBlog, cookie, format).run();
    }
}
