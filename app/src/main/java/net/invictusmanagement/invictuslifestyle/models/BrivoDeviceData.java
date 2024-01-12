package net.invictusmanagement.invictuslifestyle.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class BrivoDeviceData implements Serializable {
    private Integer id;
    private String type;
    private String name;
    private List<Integer> property = new ArrayList<Integer>();
    private String state;
    private Object temperature;
    private Object temperatureUpdatedAt;
    private Object humidity;
    private Object humidityState;
    private Boolean dry;
    private Double batteryLevel;
    private Object lastBatteryChanged;
    private Object lastBatteryReported;
    private Settings settings;
    private Boolean isActive;
    private List<Object> alarms = new ArrayList<Object>();
    private Object floorplan;
    private Object co;
    private Object smoke;
    private List<Object> alerts = new ArrayList<Object>();
    private String index;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getProperty() {
        return property;
    }

    public void setProperty(List<Integer> property) {
        this.property = property;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getTemperature() {
        return temperature;
    }

    public void setTemperature(Object temperature) {
        this.temperature = temperature;
    }

    public Object getTemperatureUpdatedAt() {
        return temperatureUpdatedAt;
    }

    public void setTemperatureUpdatedAt(Object temperatureUpdatedAt) {
        this.temperatureUpdatedAt = temperatureUpdatedAt;
    }

    public Object getHumidity() {
        return humidity;
    }

    public void setHumidity(Object humidity) {
        this.humidity = humidity;
    }

    public Object getHumidityState() {
        return humidityState;
    }

    public void setHumidityState(Object humidityState) {
        this.humidityState = humidityState;
    }

    public Boolean getDry() {
        return dry;
    }

    public void setDry(Boolean dry) {
        this.dry = dry;
    }

    public Double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Object getLastBatteryChanged() {
        return lastBatteryChanged;
    }

    public void setLastBatteryChanged(Object lastBatteryChanged) {
        this.lastBatteryChanged = lastBatteryChanged;
    }

    public Object getLastBatteryReported() {
        return lastBatteryReported;
    }

    public void setLastBatteryReported(Object lastBatteryReported) {
        this.lastBatteryReported = lastBatteryReported;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<Object> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<Object> alarms) {
        this.alarms = alarms;
    }

    public Object getFloorplan() {
        return floorplan;
    }

    public void setFloorplan(Object floorplan) {
        this.floorplan = floorplan;
    }

    public Object getCo() {
        return co;
    }

    public void setCo(Object co) {
        this.co = co;
    }

    public Object getSmoke() {
        return smoke;
    }

    public void setSmoke(Object smoke) {
        this.smoke = smoke;
    }

    public List<Object> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Object> alerts) {
        this.alerts = alerts;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}

