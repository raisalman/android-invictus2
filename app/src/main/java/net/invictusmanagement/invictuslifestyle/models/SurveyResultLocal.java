package net.invictusmanagement.invictuslifestyle.models;

import java.util.ArrayList;

public class SurveyResultLocal extends ModelBase {
    public boolean isMultipleChoice = false;
    public boolean isAnswered = false;
    public String singleChoice;
    public String singleChoiceId;
    public String questionNumber;
    public int questionTypeId;
    public int surveyFormId;
    public ArrayList<String> multipleChoice;
    public ArrayList<String> multipleChoiceId;
}