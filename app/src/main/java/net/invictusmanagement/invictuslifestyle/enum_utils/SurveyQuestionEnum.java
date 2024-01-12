package net.invictusmanagement.invictuslifestyle.enum_utils;

public enum SurveyQuestionEnum {
    Switch("1"), SingleChoice("2"), MultipleChoice("3");

    String key;

    SurveyQuestionEnum(String str) {
        key = str;
    }

    public String value() {
        return this.key;
    }
}