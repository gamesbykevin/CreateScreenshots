package com.gamesbykevin.createscreenshots;

import com.gamesbykevin.createscreenshots.camera.Camera;
import com.gamesbykevin.createscreenshots.formatter.Formatter;

import java.util.Date;

public class Main extends Thread {

    public static void main(String[] args) {

        try {

            if (args.length == 3) {

                String path = args[0];
                long delay = Long.parseLong(args[1]);
                String format = args[2];

                Camera camera = new Camera(path, delay, format);
                camera.start();

            } else if (args.length == 10) {

                String pathRead = args[0];
                String pathWrite = args[1];
                String format = args[2];
                int srcX = Integer.parseInt(args[3]);
                int srcY = Integer.parseInt(args[4]);
                int srcW = Integer.parseInt(args[5]);
                int srcH = Integer.parseInt(args[6]);
                int backR = Integer.parseInt(args[7]);
                int backG = Integer.parseInt(args[8]);
                int backB = Integer.parseInt(args[9]);

                Formatter formatter = new Formatter(pathRead, pathWrite, format, srcX, srcY, srcW, srcH, backR, backG, backB);
                formatter.format();

            } else {

                //prompt the user what to do
                promptUser();
            }

        } catch (Exception e) {
            promptUser();
            e.printStackTrace();
        }

        System.out.println("Done " + new Date().toString());
    }

    private static void promptUser() {
        System.out.println("You need to provide the correct parameters, examples below");
        System.out.println("java -jar CreateScreenshots-1.0-SNAPSHOT.jar \"C:\\screenshots\" 1000 \"png\"");
        System.out.println("java -jar CreateScreenshots-1.0-SNAPSHOT.jar \"C:\\source_folder\" \"C:\\destination_folder\" \"png\" 0 0 800 800 255 255 255");
    }
}