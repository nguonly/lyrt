package net.lyrt.snake.unanticipation;

import net.lyrt.IRole;
import net.lyrt.snake.Board;
import net.lyrt.snake.Cell;
import net.lyrt.snake.Router;

public class PassingWall implements IRole {
    public Cell getNextCell(Cell currentPosition) {
        //Router router = (Router)getRootPlayer();
        //Router router = (Router) Registry.getRegistry().getRootPlayer(null, this);
        Router router = (Router)this.getCore();
        Board board = router.getBoard();

//        System.out.println("In Passing Wall role");

        int row = currentPosition.getRow();
        int col = currentPosition.getCol();

        if (router.getDirection() == Router.DIRECTION_RIGHT) {
            col++;
            if(col==board.getColCount()-1) col=1;
        } else if (router.getDirection() == Router.DIRECTION_LEFT) {
            col--;
            if(col == 0) col = board.getColCount() - 2;
        } else if (router.getDirection() == Router.DIRECTION_UP) {
            row--;
            if(row == 0) row = board.getRowCount() - 2;
        } else if (router.getDirection() == Router.DIRECTION_DOWN) {
            row++;
            if(row == board.getRowCount()-1) row = 1;
        }

        return  router.getBoard().getCells()[row][col];
    }
}
