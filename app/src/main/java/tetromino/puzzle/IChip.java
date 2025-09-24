package tetromino.puzzle;

import android.content.Context;
import android.widget.RelativeLayout;


/**
 * Created by Dario on 26/11/2016.
 */
public class IChip extends Chip{
    public int position = 0;

    public IChip(RelativeLayout layout, boolean[][] board , int x, int y, int brickSize, Context context, boolean next) {
        if (next){
            for (int i = 0; i < 4; i++) {
                new Brick(layout, R.drawable.cyan, x, y+i, brickSize, context);
            }
        }else{
            if((!board[x][y] && !board[x][y+1] && !board[x][y+2] && !board[x][y+3]) || next){
                for (int i = 0; i < 4; i++) {
                    new Brick(layout, R.drawable.cyan, x, y+i, brickSize, context);
                }
            }else{
                if(!board[x][y] && !board[x][y+1] && !board[x][y+2]){
                    for (int i = 0; i < 3; i++) {
                        new Brick(layout, R.drawable.cyan, x, y+i, brickSize, context);
                    }
                }else if(!board[x][y] && !board[x][y+1]){
                    for (int i = 0; i < 2; i++) {
                        new Brick(layout, R.drawable.cyan, x, y+i, brickSize, context);
                    }
                }else if(!board[x][y]){
                    new Brick(layout, R.drawable.cyan, x, y, brickSize, context);
                }
                DisplayGameActivity.gameOver();
            }
        }
    }

    public void rotate(boolean[][] board, RelativeLayout layout, int brickSize, Context context) {
        int children = layout.getChildCount();
        int brick = brickSize;
        int x = (int)layout.getChildAt(children-4).getX() / brick,
            y = (int)layout.getChildAt(children-4).getY() / brick;
        boolean overlapped = false;
        if (position==0){
            //si se sale de la pantalla por la derecha, se posiciona la ficha al final de la fila si no estan ocupadas las posiciones
            if(x + 2 > Constants.PLAY_AREA_WIDTH-1){
                for(int i=1;i<5;i++){
                    if(board[Constants.PLAY_AREA_WIDTH-i][y]){
                        overlapped=true;
                        break;
                    }
                }
                if(!overlapped){
                    for(int i=1;i<5;i++){
                        layout.getChildAt(children-i).setX((Constants.PLAY_AREA_WIDTH - 5 + i) * brick); layout.getChildAt(children-i).setY(y * brick);
                    }
                    position=1;
                }
                //si se sale de la pantalla por la izquierda, se posiciona la ficha al inicio de la fila si no estan ocupadas las posiciones
            }else if(x -1 < 0){
                for(int i=1;i<5;i++){
                    if(board[i-1][y]){
                        overlapped=true;
                        break;
                    }
                }
                if(!overlapped){
                    for(int i=1;i<5;i++){
                        layout.getChildAt(children-i).setX((i - 1) * brick); layout.getChildAt(children-i).setY(y * brick);
                    }
                    position=1;
                }
                //sino no se sale de los limites laterales de la pantalla
            }else{
                //si estan libres los laterales
                if(!board[x-1][y] && !board[x+1][y] && !board[x+2][y]){
                    for(int i=1;i<5;i++){
                        layout.getChildAt(children-i).setX((x-2+i)*brick); layout.getChildAt(children-i).setY(y*brick);
                    }
                    position=1;
                    //si esta ocupado un lado a la izquierda
                }else if(board[x-1][y]){
                    //si corriendo un lado a la derecha no me salgo por el lado derecho de la pantalla
                    if(x+3<=Constants.PLAY_AREA_WIDTH-1){
                        //si estan libres las casillas si corro un lugar a la derecha
                        if(!board[x+1][y] && !board[x+2][y] && !board[x+3][y]){
                            for(int i=1;i<5;i++){
                                layout.getChildAt(children-i).setX((x - 1 + i) * brick); layout.getChildAt(children-i).setY(y * brick);
                            }
                            position=1;
                        }
                    }

                }else{
                    //si esta ocupado dos lados a la derecha
                    if(board[x+2][y]){
                        //y esta ocupado uno a la derecha
                        if(board[x+1][y]){
                            //si corriendo dos lados a la izquierda no me salgo por el lado izquierdo de la pantalla
                            if(x-3>=0){
                                //si estan libres las casillas si corro dos lugares a la izquierda la ficha
                                if(!board[x-1][y] && !board[x-2][y] && !board[x-3][y]){
                                    for(int i=1;i<5;i++){
                                        layout.getChildAt(children-i).setX((x - 4 + i) * brick); layout.getChildAt(children-i).setY(y * brick);
                                    }
                                    position=1;
                                }
                            }
                            //si esta ocupado dos lados a la derecha pero libre uno a la derecha
                        }else{
                            //si corriendo un lado a la izquierda no me salgo por el lado izquierdo de la pantalla
                            if(x-2>=0){
                                //si estan libres las casillas si corro un lugar a la izquierda
                                if(!board[x-1][y] && !board[x-2][y]){
                                    for(int i=1;i<5;i++){
                                        layout.getChildAt(children-i).setX((x - 3 + i) * brick); layout.getChildAt(children-i).setY(y * brick);
                                    }
                                    position=1;
                                }
                            }
                        }
                    }else{//si no esta ocupado dos lados a la derecha
                        //y esta ocupado uno a la derecha
                        if(board[x+1][y]){
                            //si corriendo dos lados a la izquierda no me salgo por el lado izquierdo de la pantalla
                            if(x-3>=0){
                                //si estan libres las casillas si corro dos lugares a la izquierda la ficha
                                if(!board[x-1][y] && !board[x-2][y] && !board[x-3][y]){
                                    for(int i=1;i<5;i++){
                                        layout.getChildAt(children-i).setX((x - 4 + i) * brick); layout.getChildAt(children-i).setY(y * brick);
                                    }
                                    position=1;
                                }
                            }
                        }
                    }
                }
            }
        }else{
            if(y+3 <= Constants.PLAY_AREA_HEIGHT-1){
                if(x-3!=0 && x!=Constants.PLAY_AREA_WIDTH-1){// esto es para que si la ficha esta pegada al lado izquierdo, al girarla siga pegada
                    for(int i=1;i<4;i++){
                        if(board[x-2][y+i]){
                            overlapped=true;
                        }
                    }
                    if(!overlapped){
                        for(int i=1;i<5;i++){
                            layout.getChildAt(children-i).setX((x - 2) * brick); layout.getChildAt(children-i).setY((y + 4 - i) * brick);
                            position=0;
                        }
                    }
                }else if(x==Constants.PLAY_AREA_WIDTH-1){
                    for(int i=1;i<4;i++){
                        if(board[Constants.PLAY_AREA_WIDTH-1][y+i]){
                            overlapped=true;
                        }
                    }
                    if(!overlapped){
                        for(int i=1;i<5;i++){
                            layout.getChildAt(children-i).setX((Constants.PLAY_AREA_WIDTH - 1) * brick); layout.getChildAt(children-i).setY((y + 4 - i) * brick);
                            position=0;
                        }
                    }
                }else if(x-3==0){
                    for(int i=1;i<4;i++){
                        if(board[x-3][y+i]){
                            overlapped=true;
                        }
                    }
                    if(!overlapped){
                        for(int i=1;i<5;i++){
                            layout.getChildAt(children-i).setX((x-3)* brick); layout.getChildAt(children-i).setY((y+4-i)* brick);
                            position=0;
                        }
                    }
                }
            }
        }
    }
}

