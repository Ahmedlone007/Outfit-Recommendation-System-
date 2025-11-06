package com.example.myapp.activites;

import static com.example.myapp.Common.ApiService.AI_URL_PREDICT;
import static com.example.myapp.Common.ApiService.AI_URL_PREDICT_AGAIN;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.Common.ApiService;
import com.example.myapp.Common.DbUtils;
import com.example.myapp.MainActivity;
import com.example.myapp.Model.QuestionModel;
import com.example.myapp.Model.User;
import com.example.myapp.PrefManager;
import com.example.myapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecommendActivity extends AppCompatActivity {
    LinearLayout container;
    User currentUser;
    ApiService apiService;
    JSONArray currentResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        setTitle("Recommendations");
        Button feedBack = findViewById(R.id.btn_feedback);
        container = findViewById(R.id.linearView);
        currentUser = new PrefManager(this).getUser();
        apiService = new ApiService(this);
        feedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackDialog();
            }
        });

        callAIGenerationAPI(AI_URL_PREDICT);
    }

    void callAIGenerationAPI(String url){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Results...");
        progressDialog.show();
        List<QuestionModel> questions = DbUtils.getAnswers();

        try {
            JSONObject requestBody = new JSONObject();
            for (int i = 0; i < questions.size(); i++) {
                QuestionModel question = questions.get(i);
                if(question.getText().equals("What is event?")){
                    requestBody.put("event", question.getAnswer());
                }
                if(question.getText().equals("Location?")){
                    requestBody.put("location", question.getAnswer());
                }
            }
            requestBody.put("gender", currentUser.getGender());
            requestBody.put("hue", currentUser.getSkinTone());
            requestBody.put("value", currentUser.getHairColor());
            requestBody.put("height", currentUser.getHeight());
            requestBody.put("physique", currentUser.getPhysique());
            requestBody.put("color_Season", "autumn");
            requestBody.put("forecast", DbUtils.getTemperature());


            apiService.postData(url, requestBody, new ApiService.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) throws JSONException {
                    // Handle the successful response
                    currentResults = response.getJSONArray("data");
                    container.removeAllViews();
                    for (int i = 0; i < currentResults.length(); i++) {
                        JSONObject questionObject = currentResults.getJSONObject(i);
                        String label = questionObject.getString("label");
                        String value = questionObject.getString("value");

                        generateRecommendResults(label,value);
                    }
                    progressDialog.hide();
                }

                @Override
                public void onError(String errorMessage) {
                    // Handle the error
                    progressDialog.hide();
                    Toast.makeText(RecommendActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                }
            });
        } catch (JSONException e) {
            progressDialog.hide();
            Toast.makeText(RecommendActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            throw new RuntimeException(e);
        }
    }
    private void showFeedbackDialog() {
        // Inflate custom layout for dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_feedback, null);

        // Find views in custom layout
        Button buttonYes = dialogView.findViewById(R.id.btn_yes);
        Button buttonNo = dialogView.findViewById(R.id.btn_no);

        // Create AlertDialog with custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Create and show AlertDialog
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Set click listeners for buttons
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Yes" button click
                DbUtils.saveHistory(currentResults);
                Toast.makeText(RecommendActivity.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

                finish();
                // Add your action here
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "No" button click
                Toast.makeText(RecommendActivity.this, "Reloading Recommendations", Toast.LENGTH_SHORT).show();
                callAIGenerationAPI(AI_URL_PREDICT_AGAIN);
                alertDialog.dismiss();
                // Add your action here
            }
        });
    }


    void generateRecommendResults(String label,String value){
        TextView labelTv = new TextView(this);
        labelTv.setTextSize(20);
        labelTv.setTextColor(getResources().getColor(R.color.black));
        labelTv.setPadding(10,10,5,18);
        labelTv.setText(label+": ");
        TextView valueTv = new TextView(this);
        valueTv.setPadding(5,10,10,18);
        valueTv.setTextSize(16);
        valueTv.setText(value);
        LinearLayout horizontalLinear= new LinearLayout(this);
        horizontalLinear.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLinear.addView(labelTv);
        horizontalLinear.addView(valueTv);
        container.addView(horizontalLinear);
    }

}