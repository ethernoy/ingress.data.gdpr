/*
 * Copyright (C) 2014-2018 SgrAlpha
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ingress.data.gdpr.models.reports;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ingress.data.gdpr.models.records.DeviceRecord;
import ingress.data.gdpr.models.utils.JsonUtil;

import java.time.Clock;
import java.util.Collections;
import java.util.List;

/**
 * @author SgrAlpha
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummarizedReport {

    private final long generatedTimeInMs;

    private List<DeviceRecord> usedDevices;
    private DiscoveryReport discovery;
    private HealthReport health;
    private BuildingReport building;
    private CombatReport combat;
    private DefenseReport defense;
    private ResourceGatheringReport resourceGathering;
    private MentoringReport mentoring;
    private EventsReport events;

    public SummarizedReport() {
        generatedTimeInMs = Clock.systemUTC().millis();
    }

    @JsonProperty("generatedTimeInMs")
    public long getGeneratedTimeInMs() {
        return generatedTimeInMs;
    }

    @JsonProperty("devices")
    public List<DeviceRecord> getUsedDevices() {
        return usedDevices;
    }

    public void setUsedDevices(final List<DeviceRecord> usedDevices) {
        this.usedDevices = Collections.unmodifiableList(usedDevices);
    }

    @JsonProperty("discovery")
    public DiscoveryReport getDiscovery() {
        return discovery;
    }

    public void setDiscovery(final DiscoveryReport discovery) {
        this.discovery = discovery;
    }

    @JsonProperty("health")
    public HealthReport getHealth() {
        return health;
    }

    public void setHealth(final HealthReport health) {
        this.health = health;
    }

    @JsonProperty("building")
    public BuildingReport getBuilding() {
        return building;
    }

    public void setBuilding(final BuildingReport building) {
        this.building = building;
    }

    @JsonProperty("combat")
    public CombatReport getCombat() {
        return combat;
    }

    public void setCombat(final CombatReport combat) {
        this.combat = combat;
    }

    @JsonProperty("defense")
    public DefenseReport getDefense() {
        return defense;
    }

    public void setDefense(final DefenseReport defense) {
        this.defense = defense;
    }

    @JsonProperty("resourceGathering")
    public ResourceGatheringReport getResourceGathering() {
        return resourceGathering;
    }

    public void setResourceGathering(final ResourceGatheringReport resourceGathering) {
        this.resourceGathering = resourceGathering;
    }

    @JsonProperty("mentoring")
    public MentoringReport getMentoring() {
        return mentoring;
    }

    public void setMentoring(final MentoringReport mentoring) {
        this.mentoring = mentoring;
    }

    @JsonProperty("events")
    public EventsReport getEvents() {
        return events;
    }

    public void setEvents(final EventsReport events) {
        this.events = events;
    }

    @Override public String toString() {
        return JsonUtil.toJson(this);
    }

}
