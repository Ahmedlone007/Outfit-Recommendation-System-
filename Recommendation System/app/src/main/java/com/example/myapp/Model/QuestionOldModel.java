package com.example.myapp.Model;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionOldModel {
    private String type;
    private String text;
    private List<String> options;
    private String answer;

    public QuestionOldModel(JSONObject questionObject) {
        try {
            type = questionObject.getString("type");
            text = questionObject.getString("text");
            options = getOptions(questionObject.getJSONArray("options"));
            answer = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> getOptions(JSONArray optionsArray) {
        List<String> optionsList = new ArrayList<>();
        try {
            for (int i = 0; i < optionsArray.length(); i++) {
                String option = optionsArray.getString(i);
                optionsList.add(option);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return optionsList;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
