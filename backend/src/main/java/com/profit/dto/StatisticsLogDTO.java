package com.profit.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StatisticsLogDTO {
    private List<Double> stock;
    private List<Double> liability;
    private List<Double> profit;
    private List<Double> ready;

    public StatisticsLogDTO() {
        stock = new ArrayList<>();
        liability = new ArrayList<>();
        profit = new ArrayList<>();
        ready = new ArrayList<>();
    }

}
