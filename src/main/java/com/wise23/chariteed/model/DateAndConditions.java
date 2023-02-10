package com.wise23.chariteed.model;

import java.util.Date;
import java.util.List;

public class DateAndConditions {
    private final Date date;
    private final List<String> conditions;

    public DateAndConditions(Date date, List<String> conditions) {
        this.date = date;
        this.conditions = conditions;
    }

    public Date getDate() {
        return date;
    }

    public List<String> getConditions() {
        return conditions;
    }
}