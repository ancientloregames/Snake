package com.ancientlore.snake;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class ActivitySettings extends Activity implements View.OnClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button applyButton = (Button)findViewById(R.id.buttonSettingsApply);
        applyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSettingsApply:
                ManagerGame _gm= ManagerGame.getInstance();
                _gm.setShowGrid(((CheckBox)findViewById(R.id.checkBoxShowGrid)).isChecked());
                _gm.setShowSectors(((CheckBox)findViewById(R.id.checkBoxShowPanels)).isChecked());
                _gm.setAccelerometer(((RadioButton)findViewById(R.id.radioButtonAccelerometer)).isChecked());
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
    }
}
