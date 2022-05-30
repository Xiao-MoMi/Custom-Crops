package net.momirealms.customcrops.utils;

public class Crop {

    private final String key;
    private final double chance;
    private double giantChance;
    private String[] seasons;
    private boolean willReturn;
    private String returnStage;
    private boolean willGiant;
    private String giant;

    public Crop(String key, double chance){
        this.key = key;
        this.chance = chance;
    }

    public String getReturnStage(){
        return this.returnStage;
    }
    public String getGiant(){
        return this.giant;
    }
    public boolean getWillReturn(){
        return this.willReturn;
    }
    public boolean getWillGiant(){
        return this.willGiant;
    }
    public String[] getSeasons() {return this.seasons;}
    public double getChance() {return this.chance;}
    public double getGiantChance() { return this.giantChance; }

    public void setWillReturn(boolean b){
        this.willReturn = b;
    }
    public void setReturnStage(String stage){
        this.returnStage = stage;
    }
    public void setSeasons(String[] seasons) { this.seasons = seasons; }
    public void setWillGiant(boolean b) { this.willGiant = b; }
    public void setGiant(String giant) {this.giant = giant; }
    public void setGiantChance(double giantChance) { this.giantChance = giantChance; }
}
