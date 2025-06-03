package de.rayzs.wearanything.api;

public enum ArmorEnum {

    BOOT(36), LEGGINGS(37), CHESTPLATE(38), HELMET(39);

    private final int slot;
    ArmorEnum(int slot) {
        this.slot = slot;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public int get() {
        return slot;
    }
}