package com.fenyx.utils;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public final class ResourceUtils {

    public static final String DIR_ROOT = new File(".").getAbsoluteFile().getParentFile().getAbsolutePath().concat("/");

    public static String dir_images = "res/gfx/";
    public static String dir_maps = "res/maps/";
    public static String dir_fonts = "res/fonts/";
    public static String dir_shaders = "res/shaders/";
    public static String dir_current = DIR_ROOT;

    private static HashMap<String, AWTImage> cached_images = new HashMap();
    public static final AWTImage NULL_IMAGE = createNullImage();

    private static boolean use_root = true;

    public static void useRootDirectory(boolean use) {
        use_root = use;
    }

    public static void setCurrentDir(String path) {
        if (use_root) path = DIR_ROOT.concat(path);

        dir_current = path;
    }

    public static boolean isFileExists(String path) {
        if (use_root) path = DIR_ROOT.concat(path);

        return new File(path).exists();
    }

    public static String[] listFiles(String path) {
        if (use_root) path = DIR_ROOT.concat(path);

        File folder = new File(path);
        File[] files = folder.listFiles();
        String[] names = new String[files.length];

        for (int i = 0; i < files.length; i++)
            names[i] = files[i].getName();

        return names;
    }

    public static void createDir(String path) {
        if (use_root) path = DIR_ROOT.concat(path);

        File file = new File(path);
        if (!file.exists())
            file.mkdir();
    }

    public static File createFile(String path) {
        if (use_root) path = DIR_ROOT.concat(path);

        File file = new File(path);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException localIOException) {}
        }
        return file;
    }

    public static byte[] loadRaw(String path) {
        byte[] bytes;
        InputStream is;

        if (use_root) path = DIR_ROOT.concat(path);

        try {
            is = new FileInputStream(path);
            bytes = new byte[is.available()];

            is.read(bytes);
            is.close();
        } catch (IOException e) {
            return null;
        }

        return bytes;
    }

    public static String loadFile(String path) {
        InputStream is;
        String tmp = "";
        int c;

        if (use_root) path = DIR_ROOT.concat(path);

        try {
            is = new FileInputStream(path);

            while ((c = is.read()) != -1)
                tmp = tmp + (char) c;

            is.close();
        } catch (IOException e) {
            return null;
        }

        return tmp;
    }

    public static String loadLines(String path) {
        String tmp = new String();

        if (use_root) path = DIR_ROOT.concat(path);

        try {
            try (InputStream is = new FileInputStream(path)) {
                Scanner scan = new Scanner(is);
                
                while (scan.hasNextLine())
                    tmp = tmp.concat(scan.nextLine()).concat("\n");
            }
        } catch (IOException localIOException) {
            return null;
        }

        return tmp;
    }

    public static String[] loadLinesArray(String path) {
        return StringUtils.splitString(loadLines(path), "\n");
    }

    private static AWTImage createNullImage() {
        BufferedImage img = new BufferedImage(32, 32, 1);
        Graphics2D g = img.createGraphics();

        g.setColor(java.awt.Color.BLACK);
        g.fillRect(0, 0, 32, 32);

        g.setColor(java.awt.Color.PINK);
        for (int y = 0; y < 32; y += 16) {
            for (int x = 0; x < 32; x += 16) {
                g.fillRect(x, y, 8, 8);
                g.fillRect(x + 8, y + 8, 8, 8);
            }
        }

        return new AWTImage("NULL_IMAGE", img);
    }

    public static AWTImage loadAWTImage(String path) {
        if (!cached_images.containsKey(path)) {
            try {
                File img = (use_root) ? new File(DIR_ROOT.concat(dir_images).concat(path)) : new File(path);
                BufferedImage source_image = javax.imageio.ImageIO.read(img);
                AWTImage out = new AWTImage(path, source_image);

                cached_images.put(path, out);
            } catch (IOException ex) {
                return NULL_IMAGE;
            }
        }

        return (AWTImage) cached_images.get(path);
    }

    public static Font loadTTF(String path, int size) {
        return loadTTF(path, size, 0);
    }

    public static Font loadTTF(String path, int size, int flags) {
        try {
            File ttf = (use_root) ? new File(DIR_ROOT.concat(dir_fonts).concat(path)) : new File(path);
            return Font.createFont(Font.TRUETYPE_FONT, ttf).deriveFont(flags, size);
        } catch (java.awt.FontFormatException | IOException ex) {
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            return gc.createCompatibleImage(1, 1, 3).createGraphics().getFont().deriveFont(flags, size);
        }
    }
}