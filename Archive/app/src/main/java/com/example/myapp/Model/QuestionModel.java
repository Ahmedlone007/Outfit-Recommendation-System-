package com.example.myapp.Model;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionModel {
    private String type;
    private String text;
    private List<Option> options;
    private String answer;

    public QuestionModel(JSONObject questionObject) {
        try {
            type = questionObject.getString("type");
            text = questionObject.getString("text");
            options = getOptions(questionObject.getJSONArray("options"));
            answer = "";
            if(questionObject.getString("answer")!=null){
                answer=questionObject.getString("answer");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public QuestionModel(Context context, JSONObject questionAnswerObject) {
        try {
            type = questionAnswerObject.getString("type");
            text = questionAnswerObject.getString("text");
            options = getOptions(questionAnswerObject.getJSONArray("options"));
            answer=questionAnswerObject.getString("answer");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<Option> getOptions(JSONArray optionsArray) {
        List<Option> optionsList = new ArrayList<>();
        try {
            for (int i = 0; i < optionsArray.length(); i++) {
                JSONObject optionObject = optionsArray.getJSONObject(i);
                Option option = new Option(optionObject);
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

    public List<Option> getOptions() {
        return options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public static class Option {
        private String type;
        private String text;
        private List<String> subOptions;

        public Option(JSONObject optionObject) {
            try {
                type = optionObject.getString("type");
                text = optionObject.getString("text");
                subOptions = getSubOptions(optionObject.getJSONArray("options"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private List<String> getSubOptions(JSONArray subOptionsArray) {
            List<String> subOptionsList = new ArrayList<>();
            try {
                for (int i = 0; i < subOptionsArray.length(); i++) {
                    String subOption = subOptionsArray.getString(i);
                    subOptionsList.add(subOption);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return subOptionsList;
        }

        public String getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        public List<String> getSubOptions() {
            return subOptions;
        }
    }
}
