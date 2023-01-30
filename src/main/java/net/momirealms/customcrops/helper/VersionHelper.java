package net.momirealms.customcrops.helper;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class VersionHelper {

    private boolean isNewerThan1_19_R2;
    private String version;

    public boolean isVersionNewerThan1_19_R2() {
        if (version == null) {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            String[] split = version.split("_");
            int main_ver = Integer.parseInt(split[1]);
            if (main_ver >= 20) isNewerThan1_19_R2 = true;
            else if (main_ver == 19) isNewerThan1_19_R2 = Integer.parseInt(split[2].substring(1)) >= 2;
            else isNewerThan1_19_R2 = false;
        }
        return isNewerThan1_19_R2;
    }
}
