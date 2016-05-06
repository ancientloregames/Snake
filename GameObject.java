package ancientlore.snake;

import android.graphics.Rect;

abstract class GameObject {
    private int index;
    private String name;
    private int x;
    private int y;
    private int frame;
    private Rect rect;

    GameObject(int newIndex, String newName, int newX,
               int newY, Rect newRect){
        index=newIndex;
        name=newName;
        x=newX;
        y=newY;
        frame=0;
        rect=newRect;
    }

    public int getIndex() {return index;}
    public String getName() {return name;}
    public int getX() {return x;}
    public int getY() {return y;}
    public int getFrame() {return frame;}
    public Rect getRect() {return rect;}

    public void setIndex(int index) {this.index = index;}
    public void setName(String name) {this.name = name;}
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
    public void setFrame(int frame) {this.frame = frame;}
    public void setRect(Rect rect) {this.rect.set(rect);}
    public void moveRect(int horizontal, int vertical) {
        rect.left += horizontal;
        rect.right += horizontal;
        rect.top += vertical;
        rect.bottom +=vertical;
    }
}
