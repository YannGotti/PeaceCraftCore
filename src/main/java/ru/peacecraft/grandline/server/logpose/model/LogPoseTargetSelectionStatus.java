package ru.peacecraft.grandline.server.logpose.model;

public enum LogPoseTargetSelectionStatus {
    SELECTED,
    TARGET_NOT_FOUND,
    CURRENT_ISLAND_NOT_FOUND,
    TARGET_NOT_DISCOVERED,
    TARGET_IS_CURRENT_ISLAND,
    TARGET_ALREADY_SELECTED
}