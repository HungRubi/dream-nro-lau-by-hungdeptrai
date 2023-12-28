package com.girlkun.tool;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import server.FileIO;

public class Test {

    public static void main(String[] args) throws Exception {
        DataInputStream dis = new DataInputStream(new FileInputStream(new File("C:\\Users\\adm\\.microemulator\\filesystem\\e\\0")));
        System.out.println(dis.readShort());
        int a = dis.readInt();
        byte[] ba1 = new byte[a];
        dis.read(ba1);
        int b = dis.readInt();
        byte[] ba2 = new byte[b];
        dis.read(ba2);

        ByteArrayInputStream bis = new ByteArrayInputStream(ba2);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "jpg", new File("C:\\Users\\adm\\Desktop\\testimage.png"));
        System.out.println("image created");
    }

}
