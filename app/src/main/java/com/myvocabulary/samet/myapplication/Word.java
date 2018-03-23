package com.myvocabulary.samet.myapplication;
public class Word {

    private String name;
    private String equivalent;
    private String type;
    private String example;

    public Word(){}

    public Word(String name,  String equivalent, String type,String example) {
        this.equivalent = equivalent;
        this.type = type;
        this.name = name;
        this.example = example;
    }

    public String getName() {
        return name;
    }

    public String getEquivalent() {
        return equivalent;
    }

    public String getType() {
        return type;
    }

    public String getExample() {
        return example;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEquivalent(String equivalent) {
        this.equivalent = equivalent;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
