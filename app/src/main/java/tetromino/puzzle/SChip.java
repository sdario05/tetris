package tetromino.puzzle;

import android.content.Context;
import android.widget.RelativeLayout;


/**
 * Created by Dario on 26/11/2016.
 */
public class SChip extends Chip{
    private final int offset = -1;
    public int position = 0;

    public SChip(RelativeLayout layout, boolean[][] board, int x, int y, int brickSize, Context context, boolean next){
        if(next){
            new Brick(layout, R.drawable.pink, x+1+offset, y, brickSize, context);
            new Brick(layout, R.drawable.pink, x+2+offset, y, brickSize, context);
            new Brick(layout, R.drawable.pink, x+offset, y+1, brickSize, context);
            new Brick(layout, R.drawable.pink, x+1+offset, y+1, brickSize, context);
        }else{
            if((!board[x+1+offset][y] && !board[x+2+offset][y] && !board[x+offset][y+1] && !board[x+1+offset][y+1]) || next){
                new Brick(layout, R.drawable.pink, x+1+offset, y, brickSize, context);
                new Brick(layout, R.drawable.pink, x+2+offset, y, brickSize, context);
                new Brick(layout, R.drawable.pink, x+offset, y+1, brickSize, context);
                new Brick(layout, R.drawable.pink, x+1+offset, y+1, brickSize, context);
            }else{
                if(!board[x+offset][y] && !board[x+1+offset][y]){
                    new Brick(layout, R.drawable.pink, x+offset, y, brickSize, context);
                    new Brick(layout, R.drawable.pink, x+1+offset, y, brickSize, context);
                }
                DisplayGameActivity.gameOver();
            }
        }

    }

    public void rotate(boolean[][] board, RelativeLayout layout, int brickSize, Context context) {
        int children = layout.getChildCount();
        int brick = brickSize;
        int x4 = (int)layout.getChildAt(children-4).getX() / brick,
            y4 = (int)layout.getChildAt(children-4).getY() / brick,
            x3 = (int)layout.getChildAt(children-3).getX() / brick,
            y3 = (int)layout.getChildAt(children-3).getY() / brick,
            x2 = (int)layout.getChildAt(children-2).getX() / brick,
            y2 = (int)layout.getChildAt(children-2).getY() / brick,
            x1 = (int)layout.getChildAt(children-1).getX() / brick,
            y1 = (int)layout.getChildAt(children-1).getY() / brick;
        switch(position){
            case 0: //en este if se verifica que la nueva posicion no se salga de la pantalla
                if(y3 + 2 < Constants.PLAY_AREA_HEIGHT){
                    if(x2!=0){ // esto es para que si la ficha esta pegada al lado izquierdo, al girarla siga pegada
                        //en este if se verifica que la nueva posicion no se solape con otra ficha
                        if(!board[x4+1][y4+1] && !board[x3][y3+2]){
                            layout.getChildAt(children-4).setX((x4 + 1) * brick);layout.getChildAt(children-4).setY((y4 + 1) * brick);
                            layout.getChildAt(children-3).setX(x3*brick);layout.getChildAt(children-3).setY((y3 + 2) * brick);
                            layout.getChildAt(children-2).setX((x2+1)*brick);layout.getChildAt(children-2).setY((y2 - 1) * brick);
                            position=1;
                        }
                    }else{
                        //en este if se verifica que la nueva posicion no se solape con otra ficha
                        if(!board[x3-1][y3+2] && !board[x2][y2-1]){
                            layout.getChildAt(children-4).setX(x4 * brick);layout.getChildAt(children-4).setY((y4 + 1) * brick);
                            layout.getChildAt(children-3).setX((x3-1)*brick);layout.getChildAt(children-3).setY((y3 + 2) * brick);
                            layout.getChildAt(children-2).setX(x2*brick);layout.getChildAt(children-2).setY((y2 - 1) * brick);
                            layout.getChildAt(children-1).setX((x1-1)*brick);layout.getChildAt(children-1).setY(y1 * brick);
                            position=1;
                        }
                    }
                }
                break;
            case 1: //si se sale de la pantalla por la izquierda, se posiciona la ficha al inicio de la fila si no estan ocupadas las posiciones
                if(x2 - 1 < 0){
                    if(!board[x4][y4-1] && !board[x3 + 1][y3 -2]){
                        layout.getChildAt(children-4).setX(x4 * brick);layout.getChildAt(children-4).setY((y4 - 1) * brick);
                        layout.getChildAt(children-3).setX((x3+1)*brick);layout.getChildAt(children-3).setY((y3 - 2) * brick);
                        layout.getChildAt(children-2).setX(x2*brick);layout.getChildAt(children-2).setY((y2 + 1) * brick);
                        layout.getChildAt(children-1).setX((x1+1)*brick);layout.getChildAt(children-1).setY(y1 * brick);
                        position=0;
                    }
                    //entonces no sale de la pantalla
                }else{
                    //si al girarla no estan ocupadas las posiciones se ejecuta el giro
                    if(!board[x3][y3-2] && !board[x2-1][y2 + 1]){
                        layout.getChildAt(children-4).setX((x4 - 1) * brick);layout.getChildAt(children-4).setY((y4 - 1) * brick);
                        layout.getChildAt(children-3).setX(x3*brick);layout.getChildAt(children-3).setY((y3 - 2) * brick);
                        layout.getChildAt(children-2).setX((x2-1)*brick);layout.getChildAt(children-2).setY((y2 + 1) * brick);
                        position=0;
                        //sino si no esta libre la posicion que ocupa el lado izquierdo de la ficha
                    }else if(board[x2 - 1][y2+1]){
                        //si corriendo un lado a la derecha no me salgo de la pantalla
                        if(x3 + 1 <= Constants.PLAY_AREA_WIDTH - 1){
                            //si corriendo un lado a la derecha el extremo derecho esta libre
                            if(!board[x3+1][y3-2]){
                                layout.getChildAt(children-4).setX(x4*brick);layout.getChildAt(children-4).setY((y4-1)*brick);
                                layout.getChildAt(children-3).setX((x3+1)*brick);layout.getChildAt(children-3).setY((y3 - 2) * brick);
                                layout.getChildAt(children-2).setX(x2*brick);layout.getChildAt(children-2).setY((y2+1) * brick);
                                layout.getChildAt(children-1).setX((x1+1)*brick);layout.getChildAt(children-1).setY(y1* brick);
                                position=0;
                            }
                        }
                    }
                }
                break;
        }
    }
}

