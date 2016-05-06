package ancientlore.snake;

import android.graphics.Rect;

class GOSegment extends GameObject {
    private GODirection direction;

    GOSegment(int newIndex, String newName, int newX,
              int newY, Rect newRect){
        super(newIndex, newName, newX, newY, newRect);
        direction=GODirection.NONE;
    }

    public GODirection getDirection() {return direction;}

    public void setDirection(GODirection direction) {this.direction = direction;}
}
