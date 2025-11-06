package com.example.myapp.activites;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.Common.ApiService;
import com.example.myapp.Common.DbUtils;
import com.example.myapp.Common.GenrateQuestions;
import com.example.myapp.Common.MyUtill;
import com.example.myapp.MainActivity;
import com.example.myapp.Model.Question;
import com.example.myapp.Model.QuestionModel;
import com.example.myapp.PrefManager;
import com.example.myapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewQuestionsActivity extends AppCompatActivity {

    private LinearLayout container;
    private List<QuestionModel> questions;
    private int currentQuestionIndex = 0;

    private long unixTimestamp;
    private String locationCity = "Lahore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_questions);

        container = findViewById(R.id.container);

        questions = GenrateQuestions.getQuestionList();

        // Initialize the UI with the first question
        showQuestion(questions.get(currentQuestionIndex));

        Button nextButton = findViewById(R.id.btnNext);
        Button prevButton = findViewById(R.id.btnPrev);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex < questions.size() - 1) {
                    currentQuestionIndex++;
                    showQuestion(questions.get(currentQuestionIndex));
                }
                if(nextButton.getText().toString().toLowerCase().equals("submit")){
                    DbUtils.saveAnswers(questions); // save questions with answers to db
                    callWeatherAPI();
//                    startActivity(new Intent(NewQuestionsActivity.this, RecommendActivity.class));
//                    finish();
                }
                if(currentQuestionIndex == questions.size() - 1){
                    Drawable rightDrawable = getResources().getDrawable(R.drawable.check_24);
                    nextButton.setText("Submit");
                    nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--;
                    showQuestion(questions.get(currentQuestionIndex));
                }
                if(currentQuestionIndex != questions.size() - 1) {
                    Drawable rightDrawable = getResources().getDrawable(R.drawable.arrow_right_24);
                    nextButton.setText("Next");
                    nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);

                }
            }
        });
    }

    private void showQuestion(QuestionModel question) {
        container.removeAllViews();

        TextView questionText = new TextView(this);
        questionText.setText(question.getText());
        questionText.setTextSize(20);
        questionText.setPadding(8,8,8,8);
        container.addView(questionText);

        if ("radio".equals(question.getType())) {
            RadioGroup radioGroup = new RadioGroup(this);
            radioGroup.setPadding(16,8,8,8);

            for (QuestionModel.Option option : question.getOptions()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(option.getText());
                radioButton.setTextSize(16);

                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Check if the selected option has sub-options
                        int index = radioGroup.indexOfChild(v);
                        handleRadioOptionClick(question, index);
                    }
                });
                radioGroup.addView(radioButton);
            }

            container.addView(radioGroup);
        } else if ("dropdown".equals(question.getType())) {
            Spinner spinner = new Spinner(this);
            // Populate the spinner with options
            List<String> dropdownList = new ArrayList<>();
            for (QuestionModel.Option option : question.getOptions()) {
                dropdownList.add(option.getText());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>((Context) this, android.R.layout.simple_spinner_item, dropdownList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedItem = (String) parentView.getItemAtPosition(position);
                    // Do something with the selected item
                    question.setAnswer(selectedItem);
//                    handleItemSelected(selectedItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Handle the case where nothing is selected (if needed)
                }
            });
            container.addView(spinner);
        } else if ("date".equals(question.getType())) {
            EditText dateEditText = new EditText(this);
            dateEditText.setHint("Select Date");
            dateEditText.setFocusable(false);
            // Add a click listener to show a date picker dialog or launch a date picker activity
            dateEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show date picker dialog or launch date picker activity
                    showDatePickerDialog(question,dateEditText);
                }
            });
            container.addView(dateEditText);
        }
    }


    private void handleRadioOptionClick(QuestionModel question, int selectedIndex) {
        QuestionModel.Option option = question.getOptions().get(selectedIndex);
        List<String> subOptions = option.getSubOptions();

        if (!subOptions.isEmpty()) {
            // Show a dialog with sub-options
            showSubOptionsDialog(option,subOptions, question);
        }else {
            question.setAnswer(option.getText());
        }
    }

    private void showSubOptionsDialog(QuestionModel.Option option,List<String> subOptions, QuestionModel parentQuestion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(parentQuestion.getText());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subOptions);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the click on a sub-option
                String selectedSubOption = subOptions.get(which);
                String ans = option.getText()+"-"+selectedSubOption;
                parentQuestion.setAnswer(ans);

                // You can update the UI or store the answer as needed
                // For simplicity, let's just print it for now
                System.out.println("Selected Sub-Option: " + selectedSubOption);
            }
        });

        builder.create().show();
    }

    private void showDatePickerDialog(final QuestionModel question, final EditText dateEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String selectedDate = sdf.format(calendar.getTime());
                        dateEditText.setText(selectedDate);

                        // Save the selected date to the answer
                        question.setAnswer(selectedDate);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    void callWeatherAPI() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Weather data...");
        progressDialog.show();
        ApiService apiService = new ApiService(this);
        String locationName = "";
        String eventDate = "";

        for (int i = 0; i < questions.size(); i++) {
            QuestionModel questionModel= questions.get(i);
            Log.d("yy",i+" : "+questionModel.getAnswer());

            if(questionModel.getText().equalsIgnoreCase("Location?")){
                locationName= questionModel.getAnswer();
            }
            if(questionModel.getText().equalsIgnoreCase("When the event is?")){
                eventDate= questionModel.getAnswer();
            }

        }
//        DbUtils.setTemperature("27");
//        progressDialog.hide();
//
//        startActivity(new Intent(NewQuestionsActivity.this,RecommendActivity.class));
//        finish();
        Log.d("api call","loc:"+locationName+"dare:"+eventDate);
        if(!locationName.isEmpty() && !eventDate.isEmpty()){
            apiService.getTodayTemperature(locationName, new ApiService.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) throws JSONException {
                    JSONObject mainObject = response.getJSONObject("main");
                    double temperatureKelvin = mainObject.getDouble("temp");
                    double temperatureCelsius = temperatureKelvin - 273.15; // Convert to Celsius

                    DbUtils.setTemperature(""+temperatureCelsius);
                    progressDialog.hide();
                    MyUtill.toastMsg(NewQuestionsActivity.this,"TemperatureCelsius: "+(int) temperatureCelsius);

                    startActivity(new Intent(NewQuestionsActivity.this,RecommendActivity.class));
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    progressDialog.hide();
                    MyUtill.toastMsg(NewQuestionsActivity.this,""+errorMessage);
                }
            });

        }else {
            progressDialog.hide();
            MyUtill.toastMsg(NewQuestionsActivity.this,"Location or Date Not valid");
        }


    }

}
