package ancientlore.snake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;

public class ActivityGame extends Activity implements SensorEventListener {
    GameView gameView;
    ManagerGame _gm;
    ManagerInput _im;
    ManagerLevel _lm;
    private Sensor sensor;
    private SensorManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        _gm = ManagerGame.getInstance();
        _lm = ManagerLevel.getInstance();
        _im = ManagerInput.getInstance();

        sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        gameView=new GameView(this,displaySize.x,displaySize.y);
        setContentView(gameView);
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameView.resume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        int x = (int) me.getX();
        int y = (int) me.getY();
        switch (me.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (_im.getPauseButton().getRect().contains(x,y)) {
                    _gm.switchPlaying();
                    return true;
                }
                else if (_im.getMenuButton().getRect().contains(x,y)){
                    Intent i = new Intent(this,ActivityMenu.class);
                    startActivity(i);
                }
        }
        if(_gm.isPlaying() && !_gm.isAccelerometer())_im.handleInput(me,_gm,_lm);
        else{
            if (_gm.isLose()){
                _gm.reset();
                _lm.reset();
            }
            _gm.setPlaying(true);
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent se) {
        if (_gm.isPlaying() && _gm.isAccelerometer())
            _im.handleAccelerometerInput(se,_lm);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
