import java.util.Arrays;

public class TicTacToeMatch {
    private static final char X = 'X';
    private static final char O = 'O';
    private static final char EMPTY = '-';
    private static int nextId = -1;
    private final int[] rowsSums = new int[3];
    private final int[] columnsSums = new int[3];
    private final int[] diagonalsSums = new int[2];
    private final char[] board = new char[9];
    private final int id;
    private int numberOfMoves;
    private boolean finished;
    private boolean playerXTurn;
    private char winner;

    public TicTacToeMatch() {
        this.id = generateId();
        this.playerXTurn = true;
        this.finished = false;
        this.numberOfMoves = 0;
        this.winner = EMPTY;
        Arrays.fill(board, EMPTY);
    }

    private static int generateId() {
        return nextId++;
    }

    public boolean performMove(int index) {
        if (index < 0 || index >= board.length || finished) {
            return false;
        }
        if (board[index] != EMPTY) {
            return false;
        }
        if (playerXTurn) {
            board[index] = X;
        } else {
            board[index] = O;
        }
        numberOfMoves++;
        determineState(index);
        playerXTurn = !playerXTurn;
        return true;
    }

    private void determineState(int index) {
        int column = index % 3;
        int row = index / 3;

        char shape = playerXTurn ? X : O;

        columnsSums[column] += shape;
        rowsSums[row] += shape;

        if (index % 2 == 0) {
            if (index == 4) {
                diagonalsSums[0] += shape;
                diagonalsSums[1] += shape;
            } else {
                diagonalsSums[(index % 4) / 2] += shape;
            }
        }

        // No need for calculations
        if (numberOfMoves < 5) {
            return;
        }

        for (int columnSum : columnsSums) {
            if (isSatisfied(columnSum)) {
                return;
            }
        }

        for (int rowSum : rowsSums) {
            if (isSatisfied(rowSum)) {
                return;
            }
        }

        for (int diagonalSum : diagonalsSums) {
            if (isSatisfied(diagonalSum)) {
                return;
            }
        }

        if (numberOfMoves >= board.length) {
            declareDraw();
        }
    }

    private boolean isSatisfied(int sum) {
        if (sum == 3 * O) {
            setPlayerOAsWinner();
            return true;
        } else if (sum == 3 * X) {
            setPlayerXAsWinner();
            return true;
        }
        return false;
    }

    private void setPlayerOAsWinner() {
        this.winner = O;
        this.finished = true;
    }

    private void setPlayerXAsWinner() {
        this.winner = X;
        this.finished = true;
    }

    private void declareDraw() {
        this.winner = EMPTY;
        this.finished = true;
    }

    public boolean isPlayerXTurn() {
        return playerXTurn;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public char getWinner() {
        return winner;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof TicTacToeMatch) {
            return this.id == ((TicTacToeMatch) object).id;
        }
        return false;
    }
}
