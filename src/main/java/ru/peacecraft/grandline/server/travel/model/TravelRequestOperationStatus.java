package ru.peacecraft.grandline.server.travel.model;

public enum TravelRequestOperationStatus {
    REQUEST_CREATED,
    REQUEST_REPLACED,
    REQUEST_ALREADY_EXISTS,
    NO_ACTIVE_LOG_POSE_TARGET,
    ACTIVE_LOG_POSE_TARGET_INVALID,
    ENTRY_NOT_ALLOWED,
    REQUEST_CANCELLED,
    NO_REQUEST_TO_CANCEL
}