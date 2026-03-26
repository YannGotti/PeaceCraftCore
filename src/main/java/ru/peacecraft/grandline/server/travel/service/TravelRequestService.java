package ru.peacecraft.grandline.server.travel.service;

import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.island.model.IslandEntryValidationResult;
import ru.peacecraft.grandline.server.island.service.IslandEntryValidationService;
import ru.peacecraft.grandline.server.logpose.service.LogPoseTargetService;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;
import ru.peacecraft.grandline.server.travel.model.TravelRequestOperationResult;
import ru.peacecraft.grandline.server.travel.model.TravelRequestOperationStatus;
import ru.peacecraft.grandline.server.travel.model.TravelRequestStatus;

public final class TravelRequestService {
    private static final TravelRequestService INSTANCE = new TravelRequestService();

    private TravelRequestService() {
    }

    public static TravelRequestService getInstance() {
        return INSTANCE;
    }

    public boolean hasActiveRequest(PlayerProfile profile) {
        return profile.getTravelRequestStatus() == TravelRequestStatus.REQUESTED
                && profile.getRequestedTravelTargetId() != null
                && !profile.getRequestedTravelTargetId().isBlank();
    }

    public TravelRequestOperationResult createRequestFromActiveLogPose(PlayerProfile profile) {
        String rawActiveTargetId = profile.getActiveLogPoseTargetId();
        if (rawActiveTargetId == null || rawActiveTargetId.isBlank()) {
            return fail(
                    TravelRequestOperationStatus.NO_ACTIVE_LOG_POSE_TARGET,
                    null,
                    null,
                    "Active Log Pose target is not set."
            );
        }

        String validatedActiveTargetId = LogPoseTargetService.getInstance()
                .getValidatedActiveTargetIdOrNull(profile);

        if (validatedActiveTargetId == null) {
            return fail(
                    TravelRequestOperationStatus.ACTIVE_LOG_POSE_TARGET_INVALID,
                    rawActiveTargetId,
                    null,
                    "Active Log Pose target is invalid: " + rawActiveTargetId
            );
        }

        IslandEntryValidationResult entryResult = IslandEntryValidationService.getInstance()
                .validate(profile, validatedActiveTargetId);

        if (!entryResult.allowed()) {
            return fail(
                    TravelRequestOperationStatus.ENTRY_NOT_ALLOWED,
                    validatedActiveTargetId,
                    entryResult.status(),
                    "Cannot create travel request. " + entryResult.message()
            );
        }

        if (hasActiveRequest(profile)
                && validatedActiveTargetId.equals(profile.getRequestedTravelTargetId())) {
            return fail(
                    TravelRequestOperationStatus.REQUEST_ALREADY_EXISTS,
                    validatedActiveTargetId,
                    entryResult.status(),
                    "Travel request already exists for target: " + validatedActiveTargetId
            );
        }

        boolean replacingExisting = hasActiveRequest(profile);

        profile.setRequestedTravelTargetId(validatedActiveTargetId);
        profile.setTravelRequestStatus(TravelRequestStatus.REQUESTED);
        profile.setTravelRequestCreatedAtEpochMs(System.currentTimeMillis());

        if (replacingExisting) {
            ModLog.LOGGER.info("Travel request replaced. target={}", validatedActiveTargetId);
            return new TravelRequestOperationResult(
                    true,
                    true,
                    TravelRequestOperationStatus.REQUEST_REPLACED,
                    validatedActiveTargetId,
                    entryResult.status(),
                    "Travel request replaced. New target: " + validatedActiveTargetId
            );
        }

        ModLog.LOGGER.info("Travel request created. target={}", validatedActiveTargetId);
        return new TravelRequestOperationResult(
                true,
                true,
                TravelRequestOperationStatus.REQUEST_CREATED,
                validatedActiveTargetId,
                entryResult.status(),
                "Travel request created for target: " + validatedActiveTargetId
        );
    }

    public TravelRequestOperationResult cancelRequest(PlayerProfile profile) {
        if (!hasActiveRequest(profile)) {
            return fail(
                    TravelRequestOperationStatus.NO_REQUEST_TO_CANCEL,
                    null,
                    null,
                    "There is no active travel request to cancel."
            );
        }

        String previousTargetId = profile.getRequestedTravelTargetId();
        clearRequest(profile);

        ModLog.LOGGER.info("Travel request cancelled. target={}", previousTargetId);
        return new TravelRequestOperationResult(
                true,
                true,
                TravelRequestOperationStatus.REQUEST_CANCELLED,
                previousTargetId,
                null,
                "Travel request cancelled for target: " + previousTargetId
        );
    }

    public void clearRequest(PlayerProfile profile) {
        profile.setRequestedTravelTargetId(null);
        profile.setTravelRequestStatus(TravelRequestStatus.NONE);
        profile.setTravelRequestCreatedAtEpochMs(0L);
    }

    private TravelRequestOperationResult fail(
            TravelRequestOperationStatus status,
            String targetIslandId,
            ru.peacecraft.grandline.server.island.model.IslandEntryValidationStatus entryValidationStatus,
            String message
    ) {
        return new TravelRequestOperationResult(
                false,
                false,
                status,
                targetIslandId,
                entryValidationStatus,
                message
        );
    }
}