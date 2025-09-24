package tetromino.puzzle;

import android.content.Context;
import android.widget.RelativeLayout;


/**
 * Created by Dario on 26/11/2016.
 */
public class TChip extends Chip {
    private final int offset = -1;
    private int position = 0;

    public TChip(RelativeLayout layout, boolean[][] board, int x, int y, int brickSize, Context context, boolean next) {
        if(next){
            new Brick(layout, R.drawable.red, x + 1 + offset, y, brickSize, context);
            for (int i = 0; i < 3; i++) {
                new Brick(layout, R.drawable.red, x + i + offset, y + 1, brickSize, context);
            }
        }else{
            if ((!board[x + 1 + offset][y] && !board[x + offset][y + 1] && !board[x + 1 + offset][y + 1] && !board[x + 2 + offset][y + 1]) || next) {
                new Brick(layout, R.drawable.red, x + 1 + offset, y, brickSize, context);

                for (int i = 0; i < 3; i++) {
                    new Brick(layout, R.drawable.red, x + i + offset, y + 1, brickSize, context);

                }
            } else {
                if (!board[x + offset][y] && !board[x + 1 + offset][y] && !board[x + 2 + offset][y]) {
                    for (int i = 0; i < 3; i++) {
                        new Brick(layout, R.drawable.red, x + i + offset, y, brickSize, context);
                    }
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
        switch (position) {
            case 0: //en este if se verifica que la nueva posicion no se salga de la pantalla
                if (y1 + 1 < Constants.PLAY_AREA_HEIGHT) {
                    if (x3 != 0) { // esto es para que si la ficha esta pegada al lado izquierdo, al girarla siga pegada
                        //en este if se verifica que la nueva posicion no se solape con otra ficha
                        if (!board[x1 - 1][y1 + 1]) {
                            layout.getChildAt(children-4).setX((x4 + 1) * brick); layout.getChildAt(children-4).setY((y4 + 1) * brick);
                            layout.getChildAt(children-3).setX((x3 + 1) * brick); layout.getChildAt(children-3).setY((y3 - 1) * brick);
                            layout.getChildAt(children-1).setX((x1 - 1) * brick); layout.getChildAt(children-1).setY((y1 + 1) * brick);
                            position = 1;
                        }
                    } else {
                        //en este if se verifica que la nueva posicion no se solape con otra ficha
                        if (!board[x3][y3 - 1] && !board[x1 - 2][y1 + 1]) {
                            layout.getChildAt(children-4).setX(x4*brick);layout.getChildAt(children-4).setY((y4+1)*brick);
                            layout.getChildAt(children-3).setX(x3*brick);layout.getChildAt(children-3).setY((y3 - 1) * brick);
                            layout.getChildAt(children-2).setX((x2-1)*brick);layout.getChildAt(children-2).setY(y2 * brick);
                            layout.getChildAt(children-1).setX((x1-2)*brick);layout.getChildAt(children-1).setY((y1 + 1) * brick);
                            position = 1;
                        }
                    }
                }
                break;
            case 1: //si se sale de la pantalla por la izquierda, se posiciona la ficha al inicio de la fila si no estan ocupadas las posiciones
                if (x1 - 1 < 0) {
                    if (!board[x3 + 2][y3] && !board[x2 + 1][y2 - 1]) {
                        layout.getChildAt(children-3).setX((x3 + 2) * brick);layout.getChildAt(children-3).setY(y3 * brick);
                        layout.getChildAt(children-2).setX((x2+1)*brick);layout.getChildAt(children-2).setY((y2 - 1) * brick);
                        layout.getChildAt(children-1).setX(x1*brick);layout.getChildAt(children-1).setY((y1 - 2) * brick);
                        position = 2;
                    }
                    //entonces no sale de la pantalla
                } else {
                    //si al girarla no estan ocupadas las posiciones se ejecuta el giro
                    if (!board[x3 + 1][y3] && !board[x1 - 1][y1 - 2]) {
                        layout.getChildAt(children-4).setX((x4 - 1) * brick);layout.getChildAt(children-4).setY(y4 * brick);
                        layout.getChildAt(children-3).setX((x3+1)*brick);layout.getChildAt(children-3).setY(y3 * brick);
                        layout.getChildAt(children-2).setX(x2*brick);layout.getChildAt(children-2).setY((y2 - 1) * brick);
                        layout.getChildAt(children-1).setX((x1-1)*brick);layout.getChildAt(children-1).setY((y1 - 2) * brick);
                        position = 2;
                        //sino si no estan libres la posicion que ocupa el lado izquierdo de la ficha
                    } else if (board[x1 - 1][y1 - 2]) {
                        //si corriendo un lado a la derecha no me salgo de la pantalla
                        if (x3 + 2 <= Constants.PLAY_AREA_WIDTH - 1) {
                            if (!board[x2 + 1][y2 - 1] && !board[x3 + 2][y3]) {
                                layout.getChildAt(children-3).setX((x3 + 2) * brick);layout.getChildAt(children-3).setY(y3 * brick);
                                layout.getChildAt(children-2).setX((x2+1)*brick);layout.getChildAt(children-2).setY((y2 - 1) * brick);
                                layout.getChildAt(children-1).setX(x1*brick);layout.getChildAt(children-1).setY((y1 - 2) * brick);
                                position = 2;
                            }
                        }
                    }
                }
                break;
            case 2: //en este if se verifica que la nueva posicion no se salga de la pantalla
                if (y3 + 2 < Constants.PLAY_AREA_HEIGHT) {
                    if (x1 != 0) { // esto es para que si la ficha esta pegada al lado izquierdo, al girarla siga pegada
                        //en este if se verifica que la nueva posicion no se solape con otra ficha
                        if (!board[x3][y3 + 2] && !board[x2 + 1][y2 + 1]) {
                            layout.getChildAt(children-3).setX(x3 * brick);layout.getChildAt(children-3).setY((y3 + 2) * brick);
                            layout.getChildAt(children-2).setX((x2+1)*brick);layout.getChildAt(children-2).setY((y2 + 1) * brick);
                            layout.getChildAt(children-1).setX((x1+2)*brick);layout.getChildAt(children-1).setY(y1 * brick);
                            position = 3;
                        }
                    } else {
                        if (!board[x4 - 1][y4] && !board[x3 - 1][y3 + 2]) {
                            layout.getChildAt(children-4).setX((x4 - 1) * brick);layout.getChildAt(children-4).setY(y4 * brick);
                            layout.getChildAt(children-3).setX((x3-1)*brick);layout.getChildAt(children-3).setY((y3 + 2) * brick);
                            layout.getChildAt(children-2).setX(x2*brick);layout.getChildAt(children-2).setY((y2 + 1) * brick);
                            layout.getChildAt(children-1).setX((x1+1)*brick);layout.getChildAt(children-1).setY(y1 * brick);
                            position = 3;
                        }
                    }

                }
                break;
            case 3: //si se sale de la pantalla por la izquierda, se posiciona la ficha al inicio de la fila si no estan ocupadas las posiciones
                if (x3 - 2 < 0) {
                    if (!board[x1 + 1][y1 + 1]) {
                        layout.getChildAt(children-4).setX((x4 + 1) * brick);layout.getChildAt(children-4).setY((y4 - 1) * brick);
                        layout.getChildAt(children-3).setX((x3-1)*brick);layout.getChildAt(children-3).setY((y3 - 1) * brick);
                        layout.getChildAt(children-1).setX((x1+1)*brick);layout.getChildAt(children-1).setY((y1 + 1) * brick);
                        position = 0;
                    }
                }
                //entonces no sale de la pantalla
                else {
                    //si al girarla no estan ocupadas las posiciones se ejecuta el giro
                    if (!board[x4][y4 - 1] && !board[x3 - 2][y3 - 1]) {
                        layout.getChildAt(children-4).setX(x4 * brick);layout.getChildAt(children-4).setY((y4 - 1) * brick);
                        layout.getChildAt(children-3).setX((x3-2)*brick);layout.getChildAt(children-3).setY((y3 - 1) * brick);
                        layout.getChildAt(children-2).setX((x2-1)*brick);layout.getChildAt(children-2).setY(y2 * brick);
                        layout.getChildAt(children-1).setX(x1*brick);layout.getChildAt(children-1).setY((y1 + 1) * brick);
                        position = 0;
                        //sino si no esta libre la posicion que ocupa el lado izquierdo de la ficha
                    } else if (board[x3 - 2][y3 - 1]) {
                        //si corriendo un lado a la derecha no me salgo de la pantalla
                        if (x1 + 1 <= Constants.PLAY_AREA_WIDTH - 1) {
                            if (!board[x1 +1][y1 + 1]) {
                                layout.getChildAt(children-4).setX((x4+1)*brick);layout.getChildAt(children-4).setY((y4-1)*brick);
                                layout.getChildAt(children-3).setX((x3-1)*brick);layout.getChildAt(children-3).setY((y3 - 1) * brick);
                                layout.getChildAt(children-1).setX((x1+1)*brick);layout.getChildAt(children-1).setY((y1 + 1) * brick);
                                position = 0;
                            }
                        }
                    }
                }
                break;
        }
    }
}

