package tetro.puzzle;

import android.content.Context;
import android.widget.RelativeLayout;


/**
 * Created by Dario on 26/11/2016.
 */
public class SquareChip extends Chip {

    public SquareChip(RelativeLayout layout, boolean[][] board, int x, int y, int brickSize, Context context, boolean next, GameOverListener gameOver){
        if(next){
            new Brick(layout, R.drawable.yellow,x,y, brickSize,context);
            new Brick(layout, R.drawable.yellow, x+1, y, brickSize,context);
            new Brick(layout, R.drawable.yellow, x, y+1, brickSize,context);
            new Brick(layout, R.drawable.yellow, x+1, y+1, brickSize,context);
        }else{
            if((!board[x][y] && !board[x+1][y] && !board[x][y+1] && !board[x+1][y+1]) || next){
                new Brick(layout, R.drawable.yellow,x,y, brickSize,context);
                new Brick(layout, R.drawable.yellow, x+1, y, brickSize,context);
                new Brick(layout, R.drawable.yellow, x, y+1, brickSize,context);
                new Brick(layout, R.drawable.yellow, x+1, y+1, brickSize,context);
            }else{
                if(!board[x][y] && !board[x+1][y]){
                    new Brick(layout, R.drawable.yellow, x, y, brickSize,context);
                    new Brick(layout, R.drawable.yellow, x+1, y, brickSize,context);
                }
                gameOver.callGameOver();
            }
        }

    }

    @Override
    public void rotate(boolean[][] board, RelativeLayout layout, int brickSize, Context context){}
}
