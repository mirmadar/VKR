package com.example.vkr.DTO;

import java.util.List;

public class ChartData {
    private List<String> labels;
    private List<Double> values;
    private String title;
    private String datasetLabel;

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }
    public List<Double> getValues() { return values; }
    public void setValues(List<Double> values) { this.values = values; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDatasetLabel() { return datasetLabel; }
    public void setDatasetLabel(String datasetLabel) { this.datasetLabel = datasetLabel; }
}
