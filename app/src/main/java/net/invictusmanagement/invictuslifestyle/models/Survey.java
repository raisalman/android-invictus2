package net.invictusmanagement.invictuslifestyle.models;

import java.util.ArrayList;

public class Survey extends ModelBase {
    public int surveyFormId = 0;
    public int questionTypeId = 0;
    public String questionName = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.";
    public ArrayList<SurveyAnswer> answerList;
    public boolean isAnswered = false;
    public int checkboxCount = 0;
    public ArrayList<String> multiChoiceAns = new ArrayList<>();
    public ArrayList<String> multiChoiceAnsId = new ArrayList<>();
}