package com.example.crimemap_up920915;

import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends FragmentActivity {

    public static final String EXTRA_MESSAGE_MONTH = "com.example.properattempt.MESSAGEMonth";
    private Button changeToMapActivity;
    private TextView chooseDate;
    private DatePickerDialog.OnDateSetListener setDateListener;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatePicker();
        OutputDate();
        ChangeActivityButton();

    }


    private void DatePicker(){
        chooseDate = findViewById(R.id.enterDate);
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateDialog = new DatePickerDialog(MainActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        setDateListener,year,month,day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();

            }
        });

    }

    private void OutputDate(){
        setDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                switch (month){
                    case 1: {
                        date = "January" + "/" + Integer.toString(year);
                        break;

                    }
                    case 2: {
                        date = "February" + "/" + Integer.toString(year);
                        break;

                    }
                    case 3: {
                        date = "March" + "/" + Integer.toString(year);
                        break;

                    }
                    case 4: {
                        date = "April" + "/" + Integer.toString(year);
                        break;

                    }
                    case 5: {
                        date = "May" + "/" + Integer.toString(year);
                        break;

                    }
                    case 6: {
                        date = "June" + "/" + Integer.toString(year);
                        break;

                    }
                    case 7: {
                        date = "July" + "/" + Integer.toString(year);
                        break;

                    }
                    case 8: {
                        date = "August" + "/" + Integer.toString(year);
                        break;

                    }
                    case 9: {
                        date = "September" + "/" + Integer.toString(year);
                        break;

                    }
                    case 10: {
                        date = "October" + "/" + Integer.toString(year);
                        break;

                    }
                    case 11: {
                        date = "November" + "/" + Integer.toString(year);
                        break;

                    }

                    case 12: {
                        date = "December" + "/" + Integer.toString(year);
                        break;

                    }

                }
                return;
            }
        };

    }

    public void ChangeActivityButton(){
        changeToMapActivity = findViewById(R.id.changeActivity);
        changeToMapActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date != null){
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra(EXTRA_MESSAGE_MONTH,date);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(MainActivity.this, "Please Choose a Date", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}