package com.gamesbykevin.createscreenshots.formatter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Formatter {

    //different sizes
    public static final int[] WIDTH = {800, 400, 200, 100, 114, 600, 1080, 512, 180, 1024, 1280};
    public static final int[] HEIGHT = {480, 240, 120, 60, 114, 600, 1080, 512, 120, 500, 720};

    //where to read and write our files
    private final String pathRead, pathWrite;

    //the portion of the image we want to format
    private final Rectangle screen;

    //list of dimensions we will create our images in
    private List<DesiredSizes> desiredSizes;

    //color for our background when ignoring transparency
    private final Color color;

    //image format we want to write
    private String format;

    public Formatter(String pathRead, String pathWrite, String format, int srcX, int srcY, int srcW, int srcH, int backR, int backG, int backB) {

        this.pathRead = pathRead;
        this.pathWrite = pathWrite;
        this.screen = new Rectangle(srcX, srcY, srcW, srcH);
        this.color = new Color(backR, backG, backB);

        //we will create images in every format possible
        for (String formatName : ImageIO.getWriterFormatNames()) {

            //set one by default
            if (getFormat() == null)
                setFormat(formatName);

            //if we have a match, then our format is supported
            if (format.equalsIgnoreCase(formatName)) {
                setFormat(formatName);
                break;
            }
        }
    }

    public List<DesiredSizes> getDesiredSizes() {

        if (WIDTH.length != HEIGHT.length)
            throw new RuntimeException("Lengths have to match");

        if (this.desiredSizes == null) {
            this.desiredSizes = new ArrayList<>();

            //add the different combinations
            for (int i = 0; i < WIDTH.length; i++) {
                if (WIDTH[i] == HEIGHT[i]) {
                    this.desiredSizes.add(new DesiredSizes(WIDTH[i], HEIGHT[i]));
                } else {
                    this.desiredSizes.add(new DesiredSizes(WIDTH[i], HEIGHT[i]));
                    this.desiredSizes.add(new DesiredSizes(HEIGHT[i], WIDTH[i]));
                }
            }
        }

        return desiredSizes;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public Color getColor() {
        return color;
    }

    public String getPathRead() {
        return pathRead;
    }

    public String getPathWrite() {
        return pathWrite;
    }

    public Rectangle getScreen() {
        return screen;
    }

    public void format() {

        long start = System.currentTimeMillis();

        try {

            //our directory to read files
            File directorySrc = new File(getPathRead());

            if (!directorySrc.exists())
                throw new RuntimeException("source directory doesn't exist " + directorySrc.getName());

            //our directory to write files
            File directoryDst = new File(getPathWrite());

            if (!directoryDst.exists())
                directoryDst.mkdirs();

            //how many files are we going to create?
            final int total = directorySrc.listFiles().length * getDesiredSizes().size() * 4;

            int count = 0;

            //check every image in the source directory
            for (File file : directorySrc.listFiles()) {

                //read entire image
                BufferedImage tmp = ImageIO.read(file);

                //source image is what we will use to format additional images
                BufferedImage source = new BufferedImage(getScreen().width, getScreen().height, BufferedImage.TYPE_INT_ARGB);

                //get graphics so we can update it
                Graphics graphics = source.getGraphics();

                //we now have our
                graphics.drawImage(tmp, 0, 0, 0 + getScreen().width, 0 + getScreen().height, getScreen().x, getScreen().y, getScreen().x + getScreen().width, getScreen().y + getScreen().height, null);

                //get the file name
                String fileName = file.getName().substring(file.getName().lastIndexOf("\\") + 1);

                //remove the extension from the filename
                fileName = fileName.substring(0, fileName.lastIndexOf("."));

                //the path to all files of this format
                final String pathFileFormat = getPathWrite() + "\\" + getFormat();

                //for every possible size
                for (DesiredSizes desiredSize : getDesiredSizes()) {

                    //each size will have its own path
                    final String pathFileSize = pathFileFormat + "\\" + desiredSize.width + "_" + desiredSize.height + "\\";

                    //create our files
                    formatFile(source, desiredSize, pathFileSize, fileName, true, true);
                    formatFile(source, desiredSize, pathFileSize, fileName, false, true);
                    formatFile(source, desiredSize, pathFileSize, fileName, true, false);
                    formatFile(source, desiredSize, pathFileSize, fileName,  false, false);

                    count += 4;
                    System.out.println("Progress: " + count + " of " + total);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println("Duration: " + ((end - start) / 1000) + " seconds.");
    }

    private void formatFile(BufferedImage source, DesiredSizes desiredSize, String path, String filename, boolean transparency, boolean keepRatio) {

        try {

            if (transparency) {
                path += "transparency\\";
            } else {
                path += "no-transparency\\";
            }

            if (keepRatio) {
                path += "ratio\\";
            } else {
                path += "no-ratio\\";
            }

            File directory = new File(path);

            //create the directory if it doesn't exist
            if (!directory.exists())
                directory.mkdirs();

            //create the path of the new file
            final String pathFinal = path + filename + "." + getFormat();

            System.out.println("Writing file: " + pathFinal);

            BufferedImage image = new BufferedImage(desiredSize.width, desiredSize.height, (transparency) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

            //get graphics so we can write our image
            Graphics graphics = image.getGraphics();

            //if no transparency give our image a background color
            if (!transparency) {
                graphics.setColor(getColor());
                graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
            }

            int sx = 0;
            int sy = 0;
            int sw = source.getWidth();
            int sh = source.getHeight();

            int dx = 0;
            int dy = 0;
            int dw = desiredSize.width;
            int dh = desiredSize.height;

            //do we want to maintain the size ratio
            if (keepRatio) {

                float ratio;

                //resize accordingly so we maintain aspect ratio
                if (source.getWidth() > source.getHeight()) {
                    dx = 0;
                    dw = desiredSize.width;
                    ratio = (float)source.getHeight() / (float)source.getWidth();
                    dh = (int)((float)desiredSize.width * ratio);
                    dy = (image.getHeight() / 2) - (dh / 2);
                } else {
                    dy = 0;
                    dh = desiredSize.height;
                    ratio = (float)source.getWidth() / (float)source.getHeight();
                    dw = (int)((float)desiredSize.height * ratio);
                    dx = (image.getWidth() / 2) - (dw / 2);
                }
            }

            //write the content to our image
            graphics.drawImage(source, dx, dy, dx + dw, dy + dh, sx, sy, sx + sw, sy + sh, null);

            //write the file
            ImageIO.write(image, getFormat(), new File(pathFinal));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DesiredSizes {

        private final int width, height;

        private DesiredSizes(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}