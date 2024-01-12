package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;

public class SurveyList extends ModelBase {
    public int surveyId;
    public int locationId;
    public int lpLevel1;
    public int lpLevel2;
    public int lpLevel1Point;
    public int lpLevel2Point;
    public int lpLevel3Point;
    public boolean byAdmin;
    public boolean deleted;
    public boolean isActive;
    public Date fromDate;
    public Date toDate;
    public String location;
    public String name;
    public String description;
    public String surveyName;

}