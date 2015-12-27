
import java.util.ArrayList;

/**
 * by Maruf Hamidi
 *
 */
public class Board {
    private int boardSize;
    private ArrayList<Board> neighborBoards;
    private int hammingValue;
    private int manhattanValue;
    private int[][] boardBlocks;    
    
    public Board(int[][] blocks) {
        this.boardSize = blocks.length;
        this.boardBlocks = new int[this.boardSize][this.boardSize];
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                this.boardBlocks[i][j] = blocks[i][j];
            }
        }

        this.hammingValue = -1;
        this.manhattanValue = -1;       
    }

    public int size() {
        return boardSize;
    }

    public int hamming() {
        int k = 1;
        int hV = 0;
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                if (this.boardBlocks[i][j] != 0 && this.boardBlocks[i][j] != k) {
                    hV++;
                }
                k++;
            }
        }
        this.hammingValue = hV;
        return this.hammingValue;
    }

    /*
     [i] row, [j] column
        
     manhattan distance is summation of absolute differences of the goal
     placement and the current placement.
    
     row = (i-1) / N
     column = (i-1) % N
     */
    public int manhattan() {
        int k = 1;
        int mV = 0;
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                if (this.boardBlocks[i][j] != 0 && this.boardBlocks[i][j] != k) {
                    mV = mV
                            + Math.abs(((this.boardBlocks[i][j] - 1) / this.boardSize) - i)
                            + Math.abs(((this.boardBlocks[i][j] - 1) % this.boardSize) - j);
                }
                k++;
            }
        }
        this.manhattanValue = mV;
        return this.manhattanValue;
    }

    public boolean isGoal() {
        int k = 1;
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                if (this.boardBlocks[i][j] != k) {
                    return false;
                }

                if (i == this.boardSize - 1 && j == this.boardSize - 2) {
                    k = 0;
                } else {
                    k++;
                }
            }
        }
        return true;
    }

    public boolean isSolvable() {
        // odd board size
        int inversion = 0;
        int blank = 0;
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                if (this.boardBlocks[i][j] != 0) {
                    // if the considered block is not the last element of the row 
                    // then check the rest blocks of the same row
                    if (j < this.boardSize - 1) {
                        for (int x = j + 1; x < this.boardSize; x++) {
                            if (this.boardBlocks[i][x] != 0 && this.boardBlocks[i][x] < this.boardBlocks[i][j]) {
                                inversion++;
                            }
                        }
                    }
                    // if the considered block is not in the last row
                    // then check the rest blocks in the next rows
                    if (i < this.boardSize - 1) {
                        for (int y = i + 1; y < this.boardSize; y++) {
                            for (int z = 0; z < this.boardSize; z++) {
                                if (this.boardBlocks[y][z] != 0 && this.boardBlocks[y][z] < this.boardBlocks[i][j]) {
                                    inversion++;
                                }
                            }
                        }
                    }
                } // storing the row location of zero block
                else {
                    blank = i;
                }
            }
        }

        if (this.boardSize % 2 != 0) {
            if (inversion % 2 != 0) {
                return false;
            }
        } else {
            if ((inversion + blank) % 2 == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object y) {
        Board temp = (Board) y;

        if (this.boardSize != temp.boardSize) {
            return false;
        }

        int k = 1;
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                if (this.boardBlocks[i][j] != temp.boardBlocks[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public Iterable<Board> neighbors() {
        this.neighborBoards = new ArrayList<Board>(); 
        this.discoverNeighbors();
        return this.neighborBoards;
    }

    public String toString() {
        String temp = new String();

        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                temp = temp.concat(Integer.toString(this.boardBlocks[i][j])).concat(" ");
            }
            if (i < this.boardSize - 1) {
                temp = temp.concat("\n");
            }
        }
        return temp;
    }

    // get the neighbor boards
    private void discoverNeighbors() {
        int blank_row = 0;
        int blank_col = 0;
        boolean found = false;
        int[][] temp = new int[this.boardSize][this.boardSize];
        Board temp_board;

        // locate the row and column of the blank block
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                if (this.boardBlocks[i][j] == 0) {
                    blank_row = i;
                    blank_col = j;
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }

        // whether permissible to move down
        if ((blank_row + 1) < this.boardSize) {
            temp = this.replicate();
            temp[blank_row][blank_col] = temp[blank_row + 1][blank_col];
            temp[blank_row + 1][blank_col] = 0;
            temp_board = new Board(temp);
            this.neighborBoards.add(temp_board);
        }

        // whether permissible to move up
        if ((blank_row - 1) >= 0) {
            temp = this.replicate();
            temp[blank_row][blank_col] = temp[blank_row - 1][blank_col];
            temp[blank_row - 1][blank_col] = 0;
            temp_board = new Board(temp);
            this.neighborBoards.add(temp_board);
        }

        // whether permissible to move right
        if ((blank_col + 1) < this.boardSize) {
            temp = this.replicate();
            temp[blank_row][blank_col] = temp[blank_row][blank_col + 1];
            temp[blank_row][blank_col + 1] = 0;
            temp_board = new Board(temp);
            this.neighborBoards.add(temp_board);
        }

        // whether permissible to move left
        if ((blank_col - 1) >= 0) {
            temp = this.replicate();
            temp[blank_row][blank_col] = temp[blank_row][blank_col - 1];
            temp[blank_row][blank_col - 1] = 0;
            temp_board = new Board(temp);
            this.neighborBoards.add(temp_board);
        }
    }

    // replicating the board
    private int[][] replicate() {
        int[][] temp = new int[this.boardSize][this.boardSize];
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                temp[i][j] = this.boardBlocks[i][j];
            }
        }
        return temp;
    }

    public static void main(String[] args) {

    }
}
