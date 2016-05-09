package com.ancientlore.snake;

class ManagerGame {
    private static volatile ManagerGame instance=null;
    private boolean playing = false;
    private boolean debug = false;
    private boolean lose = false;
    private boolean showGrid = false;
    private boolean showSectors = true;
    private boolean accelerometer = false;
    private int score = 0;
    private int bestScore = 0;
    private int max_lives;
    private int lives;
    private int touchX;
    private int touchY;

    private ManagerGame(){}
    static ManagerGame getInstance(){
        if (instance==null) {
            synchronized(ManagerGame.class) {
                if (instance == null)
                    instance = new ManagerGame();
            }
        }
        return instance;
    }

    void initialise(int newMaxLives){
        max_lives=newMaxLives;
        reset();
    }
    void reset(){
        playing=true;
        debug=false;
        lose =false;
        score=0;
        lives=max_lives;
        touchX=0;
        touchY=0;
    }

    public boolean isPlaying() {return playing;}
    boolean isDebug(){return debug;}
    boolean isLose(){return lose;}
    public boolean isShowGrid() {return showGrid;}
    public boolean isShowSectors() {return showSectors;}
    public boolean isAccelerometer() {return accelerometer;}
    int getScore(){return score;}
    public int getBestScore() {return bestScore;}
    int getLives(){return lives;}
    int getTouchX(){return touchX;}
    int getTouchY(){return touchY;}


    public void setPlaying(boolean playing) {this.playing = playing;}
    public void switchPlaying(){playing = !playing;}
    void setDebug(boolean value){debug=value;}
    void setLose(boolean value){lose =value;}
    public void setShowGrid(boolean showGrid) {this.showGrid = showGrid;}
    public void setShowSectors(boolean showSectors) {this.showSectors = showSectors;}
    public void setAccelerometer(boolean accelerometer) {this.accelerometer = accelerometer;}
    void updateScore(int value){score+=value;}
    public void setBestScore(int bestScore) {this.bestScore = bestScore;}
    void takeLive(){lives--;}
    void setTouch(int x, int y){touchX=x;touchY=y;}


}
