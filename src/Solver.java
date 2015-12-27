
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * by Maruf Hamidi
 *
 */
public class Solver {

//    ArrayList<SearchNode> searchNodeList;    
    ArrayList<Board> solutionBoardList;
    private int minMoves;
    private Board initial;

    public Solver(Board initial) {
        this.minMoves = 0;
        this.initial = initial;
        this.solve();
    }

    public int moves() {
        return this.minMoves;
    }

    public Iterable<Board> solution() {
        Collections.reverse(this.solutionBoardList);
        return this.solutionBoardList;
    }

    // not provided by API - custom built
    // called by thr constructor
    // solves the puzzle before its needed
    private void solve() {
        PriorityQueue<SearchNode> pQ;
        SearchNode tempSN, newSN;
        Board tempBoard;
        ArrayList<Board> tempNeighbors;
        boolean boardMatched;

        Comparator<SearchNode> cmp = new Comparator<SearchNode>() {
            @Override
            public int compare(SearchNode o1, SearchNode o2) {
                return (o1.moves + o1.board.manhattan()) - (o2.moves + o2.board.manhattan());
            }
        };

        pQ = new PriorityQueue<SearchNode>(11, cmp);
        pQ.add(new SearchNode(initial, 0, null));

        while (true) {
            tempSN = pQ.poll();
            if (tempSN == null) {
                break;
            } // goal reached
            else if (tempSN.board.isGoal()) {
                this.solutionBoardList = new ArrayList<Board>();
                this.solutionBoardList.add(tempSN.board);
                while (true) {
                    tempSN = tempSN.prev;
                    if (tempSN == null) {
                        break;
                    }
                    this.solutionBoardList.add(tempSN.board);
                    this.minMoves++;
                }
                break;
            } // not the goal board, needs to go deeper
            else {
                tempNeighbors = (ArrayList<Board>) tempSN.board.neighbors();
                for (int i = 0; i < tempNeighbors.size(); i++) {
                    tempBoard = tempNeighbors.get(i);
                    boardMatched = false;
                    newSN = new SearchNode(tempBoard, (tempSN.moves + 1), tempSN);
                    pQ.add(newSN);
                }
            }
        }
    }

    // not provided by API - custom built
    class SearchNode {

        public Board board;
        public int moves;
        public SearchNode prev;

        public SearchNode(Board b, int m, SearchNode p) {
            this.board = b;
            this.moves = m;
            this.prev = p;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // create initial board from file
        File file = new File(args[0]);
        Scanner in = new Scanner(file);
        int N = in.nextInt();
        int[][] blocks = new int[N][N];
        int temp;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                blocks[i][j] = in.nextInt();
            }
        }

        // create the initial board
        Board initial = new Board(blocks);
        System.out.println(initial.toString());

        // check if puzzle is solvable; if so, solve it and output solution
        if (initial.isSolvable()) {
            Solver solver = new Solver(initial);
            System.out.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                System.out.println(board);
                System.out.println();
            }
        } // if not, report unsolvable
        else {
            System.out.println("Unsolvable puzzle");
        }
    }
}
