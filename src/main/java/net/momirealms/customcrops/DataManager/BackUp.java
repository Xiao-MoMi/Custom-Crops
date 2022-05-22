package net.momirealms.customcrops.DataManager;

import net.momirealms.customcrops.CustomCrops;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackUp {
    public static void backUpData(){

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        File crop_data = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        File cropBackUp = new File(CustomCrops.instance.getDataFolder(), "backups/"+ format.format(date) + "/" + "crop-data.yml");
        File sprinkler_data = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        File sprinklerBackUp = new File(CustomCrops.instance.getDataFolder(), "backups/"+ format.format(date) + "/" + "sprinkler-data.yml");

        try {
            BackUp.backUp(crop_data,cropBackUp);
            BackUp.backUp(sprinkler_data,sprinklerBackUp);
        } catch (IOException e) {
            e.printStackTrace();
            CustomCrops.instance.getLogger().warning("备份发生错误");
        }
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
