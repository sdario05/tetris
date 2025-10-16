package tetro.puzzle;

import android.content.Context;
import android.widget.RelativeLayout;

/**
 * Created by Dario on 26/11/2016.
 */
public abstract class Chip {
    public void move(String buttonTouched, RelativeLayout layout, int brickSize, int x, int y, String version){
        int children = layout.getChildCount();
        for(int i=4;i>0;i--){
            switch(buttonTouched) {
                case "LEFT":
                     layout.getChildAt(children-i).setX(layout.getChildAt(children-i).getX()-1*brickSize);
                break;
                case "RIGHT":
                     layout.getChildAt(children-i).setX(layout.getChildAt(children - i).getX() + 1 * brickSize);
                break;
                case "DOWN":
                     layout.getChildAt(children-i).setY(layout.getChildAt(children-i).getY()+1*brickSize);
                break;
            }
        }
    }

    public int isCollidedFrom(boolean[][] board, RelativeLayout layout, int brickSize){
        boolean leftCollision = false, rightCollision = false, bottomCollision = false;
        int children = layout.getChildCount();
        for(int i=4;i>0;i--){
                //si hay colision por abajo
                if(layout.getChildAt(children-i).getY() ==
                        Constants.PLAY_AREA_HEIGHT*brickSize - brickSize ||
                        board[(int)(layout.getChildAt(children-i).getX()/brickSize)]
                                [(int)(layout.getChildAt(children-i).getY()/brickSize+1)]){
                    bottomCollision= true;
                }//si hay colision por la derecha
                if(layout.getChildAt(children-i).getX() ==
                        Constants.PLAY_AREA_WIDTH*brickSize - brickSize ||
                        board[((int)(layout.getChildAt(children-i).getX()/brickSize)+1)]
                                [(int)(layout.getChildAt(children-i).getY()/brickSize)]){
                    rightCollision= true;
                }//si hay colision por la izquierda
                if(layout.getChildAt(children-i).getX() == 0 ||
                        board[(int)(layout.getChildAt(children-i).getX()/brickSize-1)]
                                [(int)(layout.getChildAt(children-i).getY()/brickSize)]){
                    leftCollision= true;
                }
            }

        if(bottomCollision){
            if(leftCollision){
                if(rightCollision){
                    return Constants.BOTTOM_LEFT_RIGHT_BORDER;
                }else{
                    return Constants.BOTTOM_LEFT_BORDER;
                }
            }else if(rightCollision){
                return Constants.BOTTOM_RIGHT_BORDER;
            }else{
                return Constants.BOTTOM_BORDER;
            }
        }else{
            if(leftCollision){
                if(rightCollision){
                    return Constants.LEFT_RIGHT_BORDER;
                }else{
                    return Constants.LEFT_BORDER;
                }
            }else if(rightCollision){
                return Constants.RIGHT_BORDER;
            }else{
                return Constants.NO_COLLIDED;
            }
        }
    }

    public abstract void rotate(boolean[][] board, RelativeLayout layout, int bricksize, Context context);
}
