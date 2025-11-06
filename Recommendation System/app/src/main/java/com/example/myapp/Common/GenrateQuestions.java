package com.example.myapp.Common;

import com.example.myapp.Model.Question;
import com.example.myapp.Model.QuestionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GenrateQuestions {
    static String jsonData = "{\n" +
            "  \"questions\": [\n" +
            "    {\n" +
            "      \"type\": \"radio\",\n" +
            "      \"text\": \"What is event?\",\n" +
            "      \"options\": [\n" +
            "        {\n" +
            "          \"type\": \"radio\",\n" +
            "          \"text\": \"Wedding\",\n" +
            "          \"options\": [\n" +
            "            \"Wleema\",\n" +
            "            \"Mehdi\",\n" +
            "            \"Baraat\"\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"radio\",\n" +
            "          \"text\": \"Eid\",\n" +
            "          \"options\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"radio\",\n" +
            "          \"text\": \"Party\",\n" +
            "          \"options\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"radio\",\n" +
            "          \"text\": \"Office/University\",\n" +
            "          \"options\": [\n" +
            "            \"Daily wear\",\n" +
            "            \"Presentation\",\n" +
            "            \"Meeting\"\n" +
            "          ]\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"radio\",\n" +
            "      \"text\": \"Time of day?\",\n" +
            "      \"options\": [\n" +
            "        {\n" +
            "          \"type\": \"radio\",\n" +
            "          \"text\": \"Night\",\n" +
            "          \"options\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"radio\",\n" +
            "          \"text\": \"Day\",\n" +
            "          \"options\": []\n" +
            "        },\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"dropdown\",\n" +
            "      \"text\": \"Location?\",\n" +
            "      \"options\": [\n" +
            "        {\n" +
            "          \"type\": \"dropdown\",\n" +
            "          \"text\": \"Lahore\",\n" +
            "          \"options\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"dropdown\",\n" +
            "          \"text\": \"Faisalabad\",\n" +
            "          \"options\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"dropdown\",\n" +
            "          \"text\": \"Islamabad\",\n" +
            "          \"options\": []\n" +
            "        },\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"date\",\n" +
            "      \"text\": \"When the event is?\",\n" +
            "      \"options\": []\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static List<Question> getQuestions(){
         List<Question> questions = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray questionsArray = jsonObject.getJSONArray("questions");

            // Populate the questions list
            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject questionObject = questionsArray.getJSONObject(i);
                String type = questionObject.getString("type");
                String text = questionObject.getString("text");
                JSONArray options = questionObject.optJSONArray("options");

                questions.add(new Question(type, text, options));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return questions;
    }
    public static List<QuestionModel> getQuestionList(){
        List<QuestionModel> questions = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray questionsArray = jsonObject.getJSONArray("questions");

            // Populate the questions list
            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject questionObject = questionsArray.getJSONObject(i);
                QuestionModel questionModel = new QuestionModel(questionObject);

                questions.add(questionModel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return questions;
    }
}
