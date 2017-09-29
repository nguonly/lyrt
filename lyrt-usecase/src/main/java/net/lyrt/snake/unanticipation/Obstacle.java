package net.lyrt.snake.unanticipation;

import net.lyrt.IRole;
import net.lyrt.snake.Board;
import net.lyrt.snake.Cell;

public class Obstacle implements IRole {
    public void defineObstacle(){
        Board board = (Board)this.getCore();

        for(int y=0; y<10; y++){
            board.setObstacle(13, y+10);
        }
    }

    public void clearAllObstacles(){
        Board board = (Board)getCore();
        Cell food = board.getFoodCell();
        for(int row=1;row<board.getRowCount()-1;row++){
            for(int col=1;col<board.getRowCount()-1;col++){
                board.setCellType(row, col, Cell.CELL_TYPE_EMPTY);
            }
        }

        //set food back
        board.setFoodCell(food.getRow(), food.getCol());
    }
}
