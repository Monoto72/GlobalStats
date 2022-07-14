package me.monoto.statistics.stats;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerStatistics {

    private UUID uuid;
    private String name;

    private int fishedFish;
    private int minedBlocks;
    private int mobsKilled;
    private int placedBlocks;
    private int traversedBlocks;

    public void setPlayerUUID(UUID value) {
        this.uuid = value;
    }

    public UUID getPlayerUUID() {
        return this.uuid;
    }

    public void setPlayerName(String value) {
        this.name = value;
    }

    public String getPlayerName() {
        return this.name;
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
