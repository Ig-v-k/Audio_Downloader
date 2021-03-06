package com.download.mp3.basic;

import com.github.kiulian.downloader.OnYoutubeDownloadListener;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioFormat;
import com.github.kiulian.downloader.model.quality.AudioQuality;
import com.github.kiulian.downloader.model.quality.VideoQuality;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.*;
import static java.lang.Thread.sleep;

public class MainApplication{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    static Scanner scanner = new Scanner(in);
    static StringBuilder stringBuilder = new StringBuilder();
    static List<String> list_data = new ArrayList<>();
    static boolean flag = true;
    static byte i = 1, ii = 0;
    static YoutubeDownloader downloader = new YoutubeDownloader();
    static YoutubeVideo video;
    static VideoDetails details;
    static File outputDirectoryAudioFile = new File("audio_downloaded");

    public static void main(String... args) throws IOException, YoutubeException, InterruptedException {

        /* start app */
        out.println("\nlaunch...");
        sleep(2000);

        new CustomRun1("thread1").start();
    }

    public static class CustomRun2 extends Thread {

        public CustomRun2(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                informationData();
            } catch (IOException | YoutubeException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        }

        public static void informationData() throws IOException, YoutubeException, InterruptedException {
            out.println(ANSI_WHITE + "\ndownload(d)?\ninformation(i)?");
            out.print(">> ");switch(scanner.nextLine()) {
                case "d":
                    getDownloadAudioFromInternet(list_data);
                    break;
                case "i":
                    getInformation(list_data);
                    break;
                default:
            }
        }

        public static void getInformation(List<String> listdata) throws IOException, YoutubeException, InterruptedException {
            for (String data : listdata) {
                video = downloader.getVideo(data);
                details = video.details();
                out.println("\nTitle : " + details.title());
                out.println("Description : " + details.description());
                List<AudioFormat> audioFormats = video.findAudioWithQuality(AudioQuality.low);
                audioFormats.forEach(it -> {
                    out.println("Audio: " + it.audioQuality() + ":" + it.url());
                });
                System.exit(0);
            }
        }

        public static void getDownloadAudioFromInternet(List<String> listdata) throws IOException, YoutubeException, InterruptedException {
            for (String data : listdata) {
                video = downloader.getVideo(data);
                details = video.details();
                List<AudioFormat> audioFormats = video.findAudioWithQuality(AudioQuality.low);
                video.downloadAsync(audioFormats.get(ii++), outputDirectoryAudioFile, new OnYoutubeDownloadListener() {
                    @Override
                    public void onDownloading(int iii) {
                        out.printf("\b\b\b\b\b%d%%", iii);
                    }

                    @Override
                    public void onFinished(File file) {
                        out.printf("\nFinish audio downloaded: %s", file);
                        System.exit(0);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        err.println("\nError: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    public static class CustomRun1 extends Thread {
        public CustomRun1(String name) {
            super(name);
        }

        @Override
        public void run() {
            inputClient();
            new CustomRun2("thread2").start();
            interrupt();
        }

        public static void inputClient() {
            out.println("insert the link: (s - stop, q - exit)");
            while(flag) {
                out.print(ANSI_GREEN + i++ + ". ");
                inputData(scanner.nextLine());
            }
        }

        public static void inputData(String s) {
            if("q".equals(s))
                System.exit(0);
            if("s".equals(s))
                flag = false;
            list_data.add(s);
        }

    }
}