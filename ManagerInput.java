package com.ancientlore.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.SensorEvent;
import android.view.MotionEvent;

class ManagerInput {
    private static volatile ManagerInput instance=null;
    private GameButton menuButton;
    private GameButton pauseButton;
    private float accXPrevPos=0;
    private float accYPrevPos=0;
    private final int lag=5;
    private int counter=0;

    private ManagerInput(){}
    static ManagerInput getInstance(){
        if (instance==null) {
            synchronized (ManagerInput.class) {
                if (instance == null)
                    instance = new ManagerInput();
            }
        }
        return instance;
    }

    void initialise(Context context, final int maxX, final int maxY){
        final int padding=maxY/10;
        Bitmap menuBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.button_menu);
        int ratio = menuBitmap.getWidth()/menuBitmap.getHeight();
        menuBitmap=Bitmap.createScaledBitmap(menuBitmap,maxX/10*ratio,
                (maxY-2*padding),false);
        menuButton=new GameButton("Menu",new Rect(maxX-menuBitmap.getWidth()-padding,
                padding, maxX-padding,menuBitmap.getHeight()+padding),menuBitmap);
        Bitmap pauseBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.button_pause);
        pauseBitmap=Bitmap.createScaledBitmap(pauseBitmap,menuBitmap.getWidth(),
                menuBitmap.getHeight(),false);
        pauseButton=new GameButton("Pause",new Rect(
                menuButton.getRect().left-pauseBitmap.getWidth()-padding,
                padding, menuButton.getRect().left-padding,menuBitmap.getHeight()+padding),pauseBitmap);
    }

    void handleInput(MotionEvent me, ManagerGame gm, ManagerLevel lm){
        int x = (int) me.getX();
        int y = (int) me.getY();
        switch (me.getAction() & MotionEvent.ACTION_MASK) {
            default:break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                GODirection direction=getTouchSector(lm.getGameField(),new Point(x,y));
                if (direction == GODirection.DOWN || direction == GODirection.UP){
                    if (lm.getDirection()==GODirection.LEFT || lm.getDirection()==GODirection.RIGHT){
                        lm.setDirection(direction);}}
                else{
                    if (lm.getDirection()==GODirection.DOWN || lm.getDirection()==GODirection.UP){
                        lm.setDirection(direction);}}
                break;
        }
    }
    void handleAccelerometerInput(SensorEvent se, ManagerLevel lm){
        if(counter==lag){
            counter=0;
            if (Math.abs(se.values[0]-accXPrevPos) >= Math.abs(se.values[1]-accYPrevPos) &&
                    (lm.getDirection()==GODirection.DOWN || lm.getDirection()==GODirection.UP)) {
                    if (se.values[0] > accXPrevPos)
                        lm.setDirection(GODirection.LEFT);
                    else
                        lm.setDirection(GODirection.RIGHT);
                    accXPrevPos = se.values[0];
            }
            else if((lm.getDirection()==GODirection.LEFT || lm.getDirection()==GODirection.RIGHT)){
                    if (se.values[1] > accYPrevPos)
                        lm.setDirection(GODirection.DOWN);
                    else
                        lm.setDirection(GODirection.UP);
                    accYPrevPos = se.values[1];
            }
        }
        counter++;
    }

    private boolean isInTriangle(final Point P1,final Point P2,final Point P3,
                                 final Point PTest) {
        int a = (P1.x - PTest.x) * (P2.y - P1.y) - (P2.x - P1.x) * (P1.y - PTest.y);
        int b = (P2.x - PTest.x) * (P3.y - P2.y) - (P3.x - P2.x) * (P2.y - PTest.y);
        int c = (P3.x - PTest.x) * (P1.y - P3.y) - (P1.x - P3.x) * (P3.y - PTest.y);

        if ((a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0))
            return true;
        else
            return false;
    }

    private GODirection getTouchSector(Rect rect,Point point){
        boolean prodiag = pointIsOnTop(new Point(rect.left,rect.top),
                new Point(rect.right,rect.bottom),point);
        boolean contradiag = pointIsOnTop(new Point(rect.left,rect.bottom),
                new Point(rect.right,rect.top),point);
        if (prodiag) {
            if (contradiag) return GODirection.UP;
            else return GODirection.RIGHT;
        }
        else {
            if (contradiag) return GODirection.LEFT;
            else return GODirection.DOWN;
        }
    }

    private boolean pointIsOnTop(Point p1, Point p2, Point p){
        return p.x*(p1.y-p2.y)+p.y*(p2.x-p1.x)+(p1.x*p2.y-p2.x*p1.y) <= 0;
    }

    public GameButton getMenuButton() {
        return menuButton;
    }
    public void updateCounter(){counter++;}
    public GameButton getPauseButton() {
        return pauseButton;
    }
}
