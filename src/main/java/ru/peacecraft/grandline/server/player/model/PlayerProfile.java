package ru.peacecraft.grandline.server.player.model;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import ru.peacecraft.grandline.server.travel.model.TravelRequestStatus;


public final class PlayerProfile {
    private int profileVersion;
    private UUID playerUuid;
    private String lastKnownName;
    private String currentIslandId;
    private Set<String> discoveredIslandIds;
    private Set<String> unlockedIslandIds;
    private String activeLogPoseTargetId;
    private String activeFruitId;
    private String crewId;
    private boolean starterLogPoseGranted;
    private long createdAtEpochMs;
    private long updatedAtEpochMs;

    private String requestedTravelTargetId;
    private TravelRequestStatus travelRequestStatus;
    private long travelRequestCreatedAtEpochMs;

    public PlayerProfile() {
        this.profileVersion = 1;
        this.discoveredIslandIds = new HashSet<>();
        this.unlockedIslandIds = new HashSet<>();
        this.starterLogPoseGranted = false;
        this.travelRequestStatus = TravelRequestStatus.NONE;
        this.travelRequestCreatedAtEpochMs = 0L;
    }

    public static PlayerProfile createDefault(UUID playerUuid, String lastKnownName) {
        PlayerProfile profile = new PlayerProfile();
        long now = System.currentTimeMillis();

        profile.profileVersion = 1;
        profile.playerUuid = playerUuid;
        profile.lastKnownName = lastKnownName;
        profile.currentIslandId = "starter_island";
        profile.activeLogPoseTargetId = null;
        profile.activeFruitId = null;
        profile.crewId = null;
        profile.starterLogPoseGranted = false;
        profile.createdAtEpochMs = now;
        profile.updatedAtEpochMs = now;

        profile.discoveredIslandIds.add("starter_island");
        profile.unlockedIslandIds.add("starter_island");
        profile.requestedTravelTargetId = null;
        profile.travelRequestStatus = TravelRequestStatus.NONE;
        profile.travelRequestCreatedAtEpochMs = 0L;

        return profile;
    }

    public int getProfileVersion() {
        return profileVersion;
    }

    public void setProfileVersion(int profileVersion) {
        this.profileVersion = profileVersion;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }

    public String getCurrentIslandId() {
        return currentIslandId;
    }

    public void setCurrentIslandId(String currentIslandId) {
        this.currentIslandId = currentIslandId;
    }

    public Set<String> getDiscoveredIslandIds() {
        return discoveredIslandIds;
    }

    public void setDiscoveredIslandIds(Set<String> discoveredIslandIds) {
        this.discoveredIslandIds = discoveredIslandIds;
    }

    public Set<String> getUnlockedIslandIds() {
        return unlockedIslandIds;
    }

    public void setUnlockedIslandIds(Set<String> unlockedIslandIds) {
        this.unlockedIslandIds = unlockedIslandIds;
    }

    public String getActiveLogPoseTargetId() {
        return activeLogPoseTargetId;
    }

    public void setActiveLogPoseTargetId(String activeLogPoseTargetId) {
        this.activeLogPoseTargetId = activeLogPoseTargetId;
    }

    public String getActiveFruitId() {
        return activeFruitId;
    }

    public void setActiveFruitId(String activeFruitId) {
        this.activeFruitId = activeFruitId;
    }

    public String getCrewId() {
        return crewId;
    }

    public void setCrewId(String crewId) {
        this.crewId = crewId;
    }

    public boolean isStarterLogPoseGranted() {
        return starterLogPoseGranted;
    }

    public void setStarterLogPoseGranted(boolean starterLogPoseGranted) {
        this.starterLogPoseGranted = starterLogPoseGranted;
    }

    public long getCreatedAtEpochMs() {
        return createdAtEpochMs;
    }

    public void setCreatedAtEpochMs(long createdAtEpochMs) {
        this.createdAtEpochMs = createdAtEpochMs;
    }

    public long getUpdatedAtEpochMs() {
        return updatedAtEpochMs;
    }

    public void setUpdatedAtEpochMs(long updatedAtEpochMs) {
        this.updatedAtEpochMs = updatedAtEpochMs;
    }

    public void touch() {
        this.updatedAtEpochMs = System.currentTimeMillis();
    }

    public String getRequestedTravelTargetId() {
        return requestedTravelTargetId;
    }

    public void setRequestedTravelTargetId(String requestedTravelTargetId) {
        this.requestedTravelTargetId = requestedTravelTargetId;
    }

    public TravelRequestStatus getTravelRequestStatus() {
        return travelRequestStatus;
    }

    public void setTravelRequestStatus(TravelRequestStatus travelRequestStatus) {
        this.travelRequestStatus = travelRequestStatus;
    }

    public long getTravelRequestCreatedAtEpochMs() {
        return travelRequestCreatedAtEpochMs;
    }

    public void setTravelRequestCreatedAtEpochMs(long travelRequestCreatedAtEpochMs) {
        this.travelRequestCreatedAtEpochMs = travelRequestCreatedAtEpochMs;
    }
}