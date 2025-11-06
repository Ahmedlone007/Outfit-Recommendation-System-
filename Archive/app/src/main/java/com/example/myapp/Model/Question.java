package com.example.myapp.Model;

import org.json.JSONArray;

// Question.java
public class Question {

    private String type;
    private String text;
    private JSONArray options;

    public Question(String type, String text, JSONArray options) {
        this.type = type;
        this.text = text;
        this.options = options;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public JSONArray getOptions() {
        return options;
    }
}
