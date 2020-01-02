package com.gamesbykevin.createscreenshots.camera;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Camera extends Thread {

    //our settings
    private final String directory;
    private final long frequency;
    private String fileFormat;

    private final Robot robot;

    public Camera(String directory, long frequency, String fileFormat) throws Exception {
        this.directory = directory;
        this.frequency = frequency;
        this.robot = new Robot();

        //make sure we can write to the specified format
        String names[] = ImageIO.getWriterFormatNames();

        //set a default format (for now)
        setFileFormat(names[0]);

        //check our list of valid formats
        for (String name : names) {

            //look for a match
            if (fileFormat.equalsIgnoreCase(name)) {
                setFileFormat(name);
                break;
            }
        }

        //create directory if it doesn't exist
        File directoryTmp = new File(getDirectory());
        if (!directoryTmp.exists())
            directoryTmp.mkdirs();
    }

    public String getDirectory() {
        return this.directory;
    }

    public long getFrequency() {
        return this.frequency;
    }

    public Robot getRobot() {
        return this.robot;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getFileFormat() {
        return this.fileFormat;
    }

    @Override
    public void run() {

        //obtain full screen coordinates once
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        System.out.println(getDirectory());
        System.out.println("Writing images in format: ." + getFileFormat());

        while (true) {

            try {

                long start = System.currentTimeMillis();

                String filename = "screenshot_" + System.nanoTime() + "." + getFileFormat();

                //update file name to include directory path
                String path;

                if (!getDirectory().endsWith("\\")) {
                    path = getDirectory() + "\\" + filename;
                } else {
                    path = getDirectory() + filename;
                }

                //create our image
                BufferedImage screenFullImage = getRobot().createScreenCapture(screenRect);

                //and then write that image to a file
                ImageIO.write(screenFullImage, getFileFormat(), new File(path));

                //notify user screenshot has been created
                System.out.println("created " + path);

                long end = System.currentTimeMillis();

                //how long to sleep for
                long sleep = getFrequency() - (end - start);

                if (sleep < 0)
                    sleep = 1;

                //sleep before our next screenshot
                Thread.sleep(sleep);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}