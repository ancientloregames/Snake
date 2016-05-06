package ancientlore.snake;

import android.graphics.Bitmap;

class GameSprite {
    Bitmap bitmap;
    String name;

    GameSprite(){
        bitmap=null;
        name="none";
    }
    GameSprite(Bitmap Bitmap, String Name){
        bitmap=Bitmap;
        name=Name;
    }
}
