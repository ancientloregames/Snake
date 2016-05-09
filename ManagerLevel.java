package com.ancientlore.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

class ManagerLevel {
    private static volatile ManagerLevel instance=null;
    private Rect gameField;
    private final int gridWidth=17;
    private int gridHeight;
    private int tileSize;
    private GameSprite[] sprites;
    private ArrayList<GOFood> food;
    private ArrayList<GOSegment> snake;
    private int speed;

    public GODirection getDirection() {
        return direction;
    }

    private GODirection direction;

    private ManagerLevel(){}
    static ManagerLevel getInstance(){
        if (instance==null) {
            synchronized (ManagerLevel.class) {
                if (instance == null)
                    instance = new ManagerLevel();
            }
        }
        return instance;
    }

    void initialize(final Context context, final int displayX, final int displayY,final int upperPanelY){
        /*gameField=newGameField;

        if (gameField.right < 720) tileSize=32;
        else if (gameField.right == 720) tileSize=48;
        else if (gameField.right > 720) tileSize=64;
        gridWidth=gameField.width()/tileSize;
        gridHeight=(newGameField.height())/tileSize;*/
        tileSize=displayX/gridWidth;
        gridHeight=(displayY-upperPanelY)/tileSize;
        final int paddingX=(displayX-(gridWidth*tileSize))/2;
        final int paddingY=(displayY-upperPanelY-(gridHeight*tileSize))/2;
        gameField = new Rect(paddingX,upperPanelY+paddingY,
                paddingX+tileSize*gridWidth,upperPanelY+paddingY+gridHeight*tileSize);
        speed=tileSize;

        sprites = loadBitmaps(R.drawable.class,context);
        for (GameSprite sprite:sprites)
            sprite.bitmap = Bitmap.createScaledBitmap(sprite.bitmap, tileSize,tileSize, false);

        food=new ArrayList<>();
        generateSnake();
        food.add(generateFood());
        food.add(generateFood());
        food.add(generateFood());
        direction=GODirection.UP;
    }

    public void reset(){
        food.clear();
        snake.clear();
        direction=GODirection.UP;
        generateSnake();
        food.add(generateFood());
        food.add(generateFood());
        food.add(generateFood());
    }

    private void generateSnake(){
        snake=new ArrayList<>();
        Rect tmp=new Rect(gridWidth/2*tileSize,gameField.top+gridHeight/2*tileSize,
                gridWidth/2*tileSize+tileSize,gameField.top+gridHeight/2*tileSize+tileSize);
        snake.add(new GOSegment(bitmapIndex("head"),"head",gridWidth/2,gridHeight/2,
                tmp));
        tmp=new Rect(tmp.left,tmp.top+tileSize,tmp.right,tmp.bottom+tileSize);
        snake.add(new GOSegment(bitmapIndex("segment"),"segment",gridWidth/2,gridHeight/2+1,
                tmp));
        tmp=new Rect(tmp.left,tmp.top+tileSize,tmp.right,tmp.bottom+tileSize);
        snake.add(new GOSegment(bitmapIndex("tail"),"tail",gridWidth/2,gridHeight/2+2,
                tmp));
    }

    private GOFood generateFood(){
        String name=getFoodType();
        Rect rect=getFoodPosition();
        return new GOFood(bitmapIndex(name),name,1,1,rect);
    }
    private Rect getFoodPosition(){
        Random rand=new Random();
        int x,y;
        unique:do {
            x = rand.nextInt(gridWidth - 1) * tileSize;
            y = rand.nextInt(gridHeight - 1) * tileSize;
            for (GOSegment obj:snake)
                if (obj.getRect().contains(x,y))
                    break unique;
            for (GOFood obj : food)
                if (obj.getRect().contains(x, y))
                    break unique;
        }while (false);
        return new Rect(x, y+gameField.top, x + tileSize, y + tileSize+gameField.top);
    }
    private String getFoodType(){
        Random rand=new Random();
        switch (rand.nextInt(2)){
            default:return "none";
            case 0:return "apple";
            case 1:return "egg";
        }
    }

    private GameSprite[] loadBitmaps(Class<?> aClass, Context context) throws IllegalArgumentException{
        Field[] fields = aClass.getFields();

        ArrayList<GameSprite> res = new ArrayList<>();
        try {
            for(Field field:fields){
                if (field.getName().contains("snake_")) {
                    res.add(new GameSprite(
                            BitmapFactory.decodeResource(context.getResources(), field.getInt(null)),
                            field.getName()));
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException();/* Exception will only occur on bad class submitted. */
        }
        return res.toArray(new GameSprite[res.size()]);
    }
    int bitmapIndex(String name){
        name = name.replace(' ','_');
        for (int i=0;i<sprites.length;i++)
            if (sprites[i].name.contains(name))
                return i;
        return -1;
    }

    void update(ManagerGame gm, ManagerSound sm){
        if (gm.isPlaying()) {
            Rect tmp;
            if (direction != GODirection.NONE) {
                for (int i = snake.size() - 1; i > 0; i--)
                    snake.get(i).setRect(snake.get(i - 1).getRect());
                tmp = snake.get(0).getRect();
                if (tmp.right <= gameField.left) {
                    tmp.left = gameField.right - speed;
                    tmp.right = gameField.right;
                } else if (tmp.left >= gameField.right) {
                    tmp.left = 0;
                    tmp.right = speed;
                } else if (tmp.top <= gameField.top) {
                    tmp.top = gameField.bottom - speed;
                    tmp.bottom = gameField.bottom;
                } else if (tmp.top >= gameField.bottom) {
                    tmp.top = gameField.top+1;
                    tmp.bottom = gameField.top + speed+1;
                } else {
                    switch (direction) {
                        case LEFT:
                            snake.get(0).moveRect(-speed, 0);
                            break;
                        case RIGHT:
                            snake.get(0).moveRect(speed, 0);
                            break;
                        case UP:
                            snake.get(0).moveRect(0, -speed);
                            break;
                        case DOWN:
                            snake.get(0).moveRect(0, speed);
                            break;
                    }
                }
            }
            for (int i=4;i<snake.size()-1;i++)
                if (snake.get(0).getRect().intersect(snake.get(i).getRect())) {
                    gm.takeLive();
                }

            for (GOFood obj : food) {
                if (snake.get(0).getRect().intersect(obj.getRect())) {
                    snake.add(0, new GOSegment(bitmapIndex("segment"), "segment", gridWidth / 2, gridHeight / 2 + 1,
                            new Rect(obj.getRect())));
                    sm.playSound(obj.getName());
                    gm.updateScore(obj.getValue());
                    food.add(generateFood());
                    food.remove(obj);
                    break;
                }
            }
            if (gm.getScore()>gm.getBestScore()){
                gm.setBestScore(gm.getScore());
                ActivityMain.editor.putInt("HiScore",gm.getScore());
                ActivityMain.editor.commit();
            }
            if (gm.getLives() <= 0) {
                gm.setLose(true);
                gm.setPlaying(false);
            }
            if (gm.isLose())
                sm.playSound("lose");
        }
    }




    public GameSprite[] getSprites() {return sprites;}
    public ArrayList<GOSegment> getSnake() {return snake;}
    public ArrayList<GOFood> getFood() {return food;}
    public Rect getGameField() {return gameField;}
    public void setDirection(GODirection direction) {this.direction = direction;}
    public int getGridWidth() {return gridWidth;}
    public int getGridHeight() {return gridHeight;}
    public int getTileSize() {return tileSize;}

}
