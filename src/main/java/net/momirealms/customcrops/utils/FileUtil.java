/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

public class FileUtil {

    /**
     * 备份全部文件
     */
    public static void backUpData(){

        List<String> files = Arrays.asList("crop","sprinkler","pot","season");

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        files.forEach(fileName -> {
            File data = new File(CustomCrops.plugin.getDataFolder(), "data"+ File.separatorChar + fileName + ".yml");
            File backUp = new File(CustomCrops.plugin.getDataFolder(), "backups"+ File.separatorChar + format.format(date) + File.separatorChar + fileName + ".yml");
            try {
                FileUtil.backUp(data, backUp);
            } catch (IOException e) {
                e.printStackTrace();
                CustomCrops.plugin.getLogger().warning(fileName + ".yml备份出错!");
            }
        });
    }

    /**
     * 复制某个文件
     * @param file_from 源文件
     * @param file_to 目标文件
     * @throws IOException IO异常
     */
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
