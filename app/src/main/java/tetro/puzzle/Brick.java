package tetro.puzzle;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Dario on 26/11/2016.
 */
public class Brick {
    public Brick(RelativeLayout layout, int color, int x, int y, int brickSize, Context context){
        ImageView brick = new ImageView(context);
        brick.setImageResource(color);
        brick.setLayoutParams(new RelativeLayout.LayoutParams(brickSize, brickSize));
        if(layout.getTag().equals("next")){
            if(color == R.drawable.yellow || color == R.drawable.orange || color == R.drawable.blue){
                brick.setX((x -0.5f) * brickSize);brick.setY((y - 0.5f) * brickSize);
            }else{
                brick.setX(x * brickSize);brick.setY(y * brickSize);
            }
        } else{
            brick.setX((x) * brickSize);brick.setY((y) * brickSize);
        }
        if(color == R.drawable.gray){
            brick.setTag("gray");
        }else{
            brick.setTag("notGray");
        }
        layout.addView(brick);
    }
}
