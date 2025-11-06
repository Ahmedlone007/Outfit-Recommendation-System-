package com.example.myapp.Common;

import com.example.myapp.Model.QuestionModel;
import com.example.myapp.Model.User;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class DbUtils {
    private static final String KEY_USER= "USER";
    private static final String KEY_ANSWERS = "ANSWERS";
    private static final String KEY_HISTORY = "HISTORY";

    public static void saveCurrentUser(User user) {
        Paper.book().write(KEY_USER,user);
    }
    public static User getCurrentUser() {
        return Paper.book().read(KEY_USER,null);
    }
    public static void saveAnswers(List<QuestionModel> questions) {
        Paper.book().write(KEY_ANSWERS,questions);
    }
    public static List<QuestionModel> getAnswers() {
        return Paper.book().read(KEY_ANSWERS,new ArrayList<>());
    }

    public static QuestionModel getQuestionByIndex(int index) {
        // index always starts at 0;
        List<QuestionModel> questions = Paper.book().read(KEY_ANSWERS,new ArrayList<>());

        return questions.get(index);
    }
    public static void setTemperature(String temp) {
        // index always starts at 0;
        Paper.book().write("Temp",temp);
    }
    public static String getTemperature() {

        return Paper.book().read("Temp","");
    }

    public static void saveHistory(JSONArray result) {
        List<JSONArray> history = getHistory();
        history.add(result);
        Paper.book().write(KEY_HISTORY,history);
    }
    public static List<JSONArray> getHistory() {

        return Paper.book().read(KEY_HISTORY,new ArrayList<>());
    }

}
