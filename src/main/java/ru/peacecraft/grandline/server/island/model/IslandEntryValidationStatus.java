package ru.peacecraft.grandline.server.island.model;

public enum IslandEntryValidationStatus {
    ALLOWED,
    TARGET_ISLAND_NOT_FOUND,
    CURRENT_ISLAND_NOT_FOUND,
    ALREADY_ON_TARGET_ISLAND,
    TARGET_NOT_DISCOVERED,
    TARGET_NOT_UNLOCKED
}