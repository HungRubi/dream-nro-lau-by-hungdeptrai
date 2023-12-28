package server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIO {

    public static byte[] readFile(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            byte[] ab = new byte[fis.available()];
            fis.read(ab, 0, ab.length);
            fis.close();
            return ab;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFile(String url, byte[] ab) {
        try {
            File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(ab);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("data/map/temp/48"));
            dis.readByte();
            int tmw, tmh;
            System.out.println("tmw: " + (tmw = dis.readByte()));
            System.out.println("tmh: " + (tmh = dis.readByte()));
            for(int i = 0; i < tmh; i++){
                for(int j = 0; j < tmw; j++){
                    String text = dis.readByte() + "";
                    System.out.print(text + (text.length() == 1? "  " : " "));
                }
                    System.out.println();
            }
        } catch (Exception e) {
        }
    }
}
