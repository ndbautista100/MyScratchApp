package com.example.mortgagev0;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import java.text.DecimalFormat;


public class DataActivity extends AppCompatActivity {
        public void onCreate( Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_data );
        }
    public void updateView( ) {
        Mortgage mortgage = MainActivity.mortgage;
        if( mortgage.getYears( ) == 10 ) {
            RadioButton rb10 = ( RadioButton ) findViewById( R.id.ten );
            rb10.setChecked( true );
        } else if(mortgage.getYears()== 15)
        {
            RadioButton rb10 = ( RadioButton ) findViewById( R.id.fifteen );
            rb10.setChecked( true );
    } // else do nothing (default is 30)
    EditText amountET = ( EditText ) findViewById( R.id.data_amount );
    amountET.setText( "" + mortgage.getAmount( ) );


}



    public void updateMortgageObject( ) {

        RadioButton rb10 = ( RadioButton ) findViewById( R.id.ten );
        RadioButton rb15 = ( RadioButton ) findViewById( R.id.fifteen );
        int years = 30;
        if( rb10.isChecked( ) )
            years = 10;
        else if(rb15.isChecked())
        {
            years = 15;
        }
        MainActivity.mortgage.setYears( years );
        EditText amountET = ( EditText ) findViewById( R.id.data_amount );
        String amountString = amountET.getText( ).toString( );
        EditText rateET = (EditText) findViewById(R.id.data_rate);
        String rateString = rateET.getText().toString();

        try {
            float amount = Float.parseFloat( amountString );
            MainActivity.mortgage.setAmount( amount );
            float rate = Float.parseFloat(rateString);
            MainActivity.mortgage.setRate(rate);

        } catch( NumberFormatException nfe ) {
            MainActivity.mortgage.setAmount( 100000.0f );
            MainActivity.mortgage.setRate( .035f );
        }
    }

    public void goBack( View v )
    {
        updateMortgageObject();
        this.finish( );
    }
}
