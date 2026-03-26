package ru.peacecraft.grandline.server.travel.service;

import ru.peacecraft.grandline.server.island.model.IslandEntryValidationResult;
import ru.peacecraft.grandline.server.island.service.IslandEntryValidationService;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;
import ru.peacecraft.grandline.server.travel.model.TravelRequestStatus;

public final class TravelProfileSanitizer {
    private TravelProfileSanitizer() {
    }

    public static void sanitize(PlayerProfile profile) {
        if (profile.getTravelRequestStatus() == null) {
            profile.setTravelRequestStatus(TravelRequestStatus.NONE);
        }

        if (profile.getTravelRequestCreatedAtEpochMs() < 0L) {
            profile.setTravelRequestCreatedAtEpochMs(0L);
        }

        if (profile.getTravelRequestStatus() != TravelRequestStatus.REQUESTED) {
            clearResidualState(profile);
            return;
        }

        String requestedTargetId = profile.getRequestedTravelTargetId();
        if (requestedTargetId == null || requestedTargetId.isBlank()) {
            clearRequestState(profile);
            return;
        }

        IslandEntryValidationResult entryResult = IslandEntryValidationService.getInstance()
                .validate(profile, requestedTargetId);

        if (!entryResult.allowed()) {
            clearRequestState(profile);
        }
    }

    private static void clearResidualState(PlayerProfile profile) {
        if (profile.getRequestedTravelTargetId() != null || profile.getTravelRequestCreatedAtEpochMs() != 0L) {
            clearRequestState(profile);
        }
    }

    private static void clearRequestState(PlayerProfile profile) {
        profile.setRequestedTravelTargetId(null);
        profile.setTravelRequestStatus(TravelRequestStatus.NONE);
        profile.setTravelRequestCreatedAtEpochMs(0L);
    }
}