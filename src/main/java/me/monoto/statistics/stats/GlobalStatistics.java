package me.monoto.statistics.stats;

public class GlobalStatistics {
    private int fishedFish;
    private int minedBlocks;
    private int mobsKilled;
    private int placedBlocks;
    private int traversedBlocks;

    public GlobalStatistics() {
        this.fishedFish = 0;
        this.minedBlocks = 0;
        this.mobsKilled = 0;
        this.placedBlocks = 0;
        this.traversedBlocks = 0;
    }


    public void setFishedFish(int value) {
        this.fishedFish = value;
    }

    public int getFishedFish() {
        return this.fishedFish;
    }

    public void setMinedBlocks(int value) {
        this.minedBlocks = value;
    }

    public int getMinedBlocks() {
        return this.minedBlocks;
    }

    public void setMobsKilled(int value) {
        this.mobsKilled = value;
    }

    public int getMobsKilled() {
        return this.mobsKilled;
    }

    public void setPlacedBlocks(int value) {
        this.placedBlocks = value;
    }

    public int getPlacedBlocks() {
        return this.placedBlocks;
    }

    public void setTraversedBlocks(int value) {
        this.traversedBlocks = value;
    }

    public int getTraversedBlocks() {
        return this.traversedBlocks;
    }
}
