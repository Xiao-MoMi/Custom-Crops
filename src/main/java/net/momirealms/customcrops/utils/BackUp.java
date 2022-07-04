package net.momirealms.customcrops.utils;

import net.momirealms.customcrops.CustomCrops;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BackUp {

    public static void backUpData(){

        List<String> files = Arrays.asList("crop","sprinkler","pot","season");

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        files.forEach(fileName -> {
            File data = new File(CustomCrops.instance.getDataFolder(), "data"+ File.separatorChar + fileName + ".yml");
            File backUp = new File(CustomCrops.instance.getDataFolder(), "backups"+ File.separatorChar + format.format(date) + File.separatorChar + fileName + ".yml");
            try {
                BackUp.backUp(data, backUp);
            } catch (IOException e) {
                e.printStackTrace();
                CustomCrops.instance.getLogger().warning(fileName + ".yml备份出错!");
            }
        });
    }

    private static void backUp(File file_from, File file_to) throws IOException {
        if(!file_to.exists()){
            file_to.getParentFile().mkdirs();
        }
        FileInputStream fis = new FileInputStream(file_from);
        if(!file_to.exists()){
            file_to.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file_to);
        byte[] b = new byte[1024];
        int len;
        while ((len = fis.read(b))!= -1){
            fos.write(b,0,len);
        }
        fos.close();
        fis.close();
    }
}
