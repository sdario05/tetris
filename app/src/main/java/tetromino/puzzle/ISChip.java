package tetromino.puzzle;

import android.content.Context;
import android.widget.RelativeLayout;


/**
 * Created by Dario on 26/11/2016.
 */
public class ISChip extends Chip{
    private final int offset = -1;
    public int position = 0;

    public ISChip(RelativeLayout layout, boolean[][] board, int x, int y, int brickSize, Context context, boolean next, GameOverListener gameOver){
        if (next){
            new Brick(layout, R.drawable.green, x+offset, y, brickSize, context);
            new Brick(layout, R.drawable.green, x+1+offset, y, brickSize, context);
            new Brick(layout, R.drawable.green, x+1+offset, y+1, brickSize, context);
            new Brick(layout, R.drawable.green, x+2+offset, y+1, brickSize, context);
        }else{
            if(!board[x+offset][y] && !board[x+1+offset][y] && !board[x+1+offset][y+1] && !board[x+2+offset][y+1]){
                new Brick(layout, R.drawable.green, x+offset, y, brickSize, context);
                new Brick(layout, R.drawable.green, x+1+offset, y, brickSize, context);
                new Brick(layout, R.drawable.green, x+1+offset, y+1, brickSize, context);
                new Brick(layout, R.drawable.green, x+2+offset, y+1, brickSize, context);
            }else{
                if(!board[x+1+offset][y] && !board[x+2+offset][y]){
                    new Brick(layout, R.drawable.green, x+1+offset, y, brickSize, context);
                    new Brick(layout, R.drawable.green, x+2+offset, y, brickSize, context);
                }
                gameOver.callGameOver();
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
                if(y1 + 1 < Constants.PLAY_AREA_HEIGHT){
                    // esto es para que si la ficha esta pegada al lado izquierdo, al girarla siga pegada
                    if(x4!=0){
                        //en este if se verifica que la nueva posicion no se solape con otra ficha
                        if(!board[x4+2][y4] && !board[x1-1][y1+1]){
                            layout.getChildAt(children-4).setX((x4 + 2) * brick);layout.getChildAt(children-4).setY(y4 * brick);
                            layout.getChildAt(children-3).setX((x3+1)*brick);layout.getChildAt(children-3).setY((y3 + 1) * brick);
                            layout.getChildAt(children-1).setX((x1-1)*brick);layout.getChildAt(children-1).setY((y1 + 1) * brick);
                            position=1;
                        }
                    }else{
                        //en este if se verifica que la nueva posicion no se solape con otra ficha
                        if(!board[x2-1][y2] && !board[x1-2][y1+1]){
                            layout.getChildAt(children-4).setX((x4 + 1) * brick);layout.getChildAt(children-4).setY(y4 * brick);
                            layout.getChildAt(children-3).setX(x3*brick);layout.getChildAt(children-3).setY((y3 + 1) * brick);
                            layout.getChildAt(children-2).setX((x2-1)*brick);layout.getChildAt(children-2).setY(y2 * brick);
                            layout.getChildAt(children-1).setX((x1-2)*brick);layout.getChildAt(children-1).setY((y1 + 1) * brick);
                            position=1;
                        }
                    }
                }
                break;
            case 1: //si se sale de la pantalla por la izquierda, se posiciona la ficha al inicio de la fila si no estan ocupadas las posiciones
                if(x4 - 2 < 0){
                    if(!board[x4-1][y4] && !board[x1 + 2][y1 -1]){
                        layout.getChildAt(children-4).setX((x4 - 1) * brick);layout.getChildAt(children-4).setY(y4 * brick);
                        layout.getChildAt(children-3).setX(x3*brick);layout.getChildAt(children-3).setY((y3 - 1) * brick);
                        layout.getChildAt(children-2).setX((x2+1)*brick);layout.getChildAt(children-2).setY(y2 * brick);
                        layout.getChildAt(children-1).setX((x1+2)*brick);layout.getChildAt(children-1).setY((y1 - 1) * brick);
                        position=0;
                    }
                    //entonces no sale de la pantalla
                }else{
                    //si al girarla no estan ocupadas las posiciones se ejecuta el giro
                    if(!board[x4-2][y4] && !board[x3-1][y3-1]){
                        layout.getChildAt(children-4).setX((x4 - 2) * brick);layout.getChildAt(children-4).setY(y4 * brick);
                        layout.getChildAt(children-3).setX((x3-1)*brick);layout.getChildAt(children-3).setY((y3 - 1) * brick);
                        layout.getChildAt(children-1).setX((x1+1)*brick);layout.getChildAt(children-1).setY((y1 - 1) * brick);
                        position=0;
                        //sino si no esta libre la posicion que ocupa el lado izquierdo de la ficha
                    }else if(board[x4 - 2][y4]){
                        //si corriendo un lado a la derecha no me salgo de la pantalla
                        if(x1 + 2 <= Constants.PLAY_AREA_WIDTH - 1){
                            //si corriendo un lado a la derecha el extremo derecho esta libre
                            if(!board[x1+2][y1-1]){
                                layout.getChildAt(children-4).setX((x4-1)*brick);layout.getChildAt(children-4).setY(y4*brick);
                                layout.getChildAt(children-3).setX(x3*brick);layout.getChildAt(children-3).setY((y3 - 1) * brick);
                                layout.getChildAt(children-2).setX((x2+1)*brick);layout.getChildAt(children-2).setY(y2 * brick);
                                layout.getChildAt(children-1).setX((x1+2)*brick);layout.getChildAt(children-1).setY((y1 - 1) * brick);
                                position=0;
                            }
                        }
                    }
                }
                break;
        }
    }
}

