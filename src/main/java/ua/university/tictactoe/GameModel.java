package ua.university.tictactoe;

import java.util.*;

public class GameModel {
    public enum GameMode { FIRST_LINE, TIME_ATTACK }

    private final Map<Point, Integer> board = new HashMap<>();
    private final Set<Point> usedForScoring = new HashSet<>();
    private final GameMode mode;
    private boolean isXTurn = true;
    private final boolean vsAI;
    private final int aiPlayerSide;
    private int scoreX = 0;
    private int scoreO = 0;
    private int timeLeft;
    private boolean gameOver = false;
    private String statusMessage = "";

    public GameModel(GameMode mode, int timeLimitSeconds, boolean vsAI, int aiPlayerSide) {
        this.mode = mode;
        this.timeLeft = timeLimitSeconds;
        this.vsAI = vsAI;
        this.aiPlayerSide = aiPlayerSide;
        this.statusMessage = "Game's begone! Make your move.";
    }

    public boolean makeMove(int x, int y) {
        if (gameOver) return false;
        Point p = new Point(x, y);
        if (board.containsKey(p) || !isWithinDistance(p)) return false;

        board.put(p, isXTurn ? 1 : 2);

        if (mode == GameMode.TIME_ATTACK) {
            checkAndScoreLines(x, y);
        } else {
            int winner = checkWin(x, y);
            if (winner != 0) {
                gameOver = true;
                statusMessage = (winner == 1 ? "X's the winner!" : "O's the winner!");
            }
        }

        if (!gameOver) {
            isXTurn = !isXTurn;
            statusMessage = "Turn: " + getCurrentPlayerName();
        }
        return true;
    }

    private void checkAndScoreLines(int x, int y) {
        int player = board.get(new Point(x, y));
        int[][] dirs = {{1,0}, {0,1}, {1,1}, {1,-1}};
        for (int[] d : dirs) {
            List<Point> line = new ArrayList<>();
            line.add(new Point(x, y));
            collectPoints(x, y, d[0], d[1], player, line);
            collectPoints(x, y, -d[0], -d[1], player, line);
            line.sort((p1, p2) -> p1.x != p2.x ? p1.x - p2.x : p1.y - p2.y);

            if (line.size() >= 5) {
                for (int i = 0; i <= line.size() - 5; i++) {
                    List<Point> segment = line.subList(i, i + 5);
                    if (segment.stream().noneMatch(usedForScoring::contains)) {
                        if (player == 1) scoreX++; else scoreO++;
                        usedForScoring.addAll(segment);
                        break;
                    }
                }
            }
        }
    }

    private void collectPoints(int x, int y, int dx, int dy, int p, List<Point> list) {
        int c = 1;
        while (board.getOrDefault(new Point(x + dx * c, y + dy * c), 0) == p) {
            list.add(new Point(x + dx * c, y + dy * c));
            c++;
        }
    }

    public int checkWin(int x, int y) {
        if (mode == GameMode.TIME_ATTACK) return 0;
        Integer player = board.get(new Point(x, y));
        int[][] dirs = {{1,0}, {0,1}, {1,1}, {1,-1}};
        for (int[] d : dirs) {
            int count = 1 + countInDir(x, y, d[0], d[1], player) + countInDir(x, y, -d[0], -d[1], player);
            if (count >= 5) return player;
        }
        return 0;
    }

    private int countInDir(int x, int y, int dx, int dy, int p) {
        int c = 0;
        while (board.getOrDefault(new Point(x + dx*(c+1), y + dy*(c+1)), 0) == p) c++;
        return c;
    }

    public Point getBestMove() {
        Point best = null;
        int max = -1;
        // ШІ аналізує тільки клітинки навколо вже поставлених фігур
        for (Point p : board.keySet()) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    Point c = new Point(p.x + dx, p.y + dy);
                    if (!board.containsKey(c)) {
                        int s = eval(c, aiPlayerSide) + (int)(eval(c, (aiPlayerSide==1?2:1)) * 1.5);
                        if (s > max) { max = s; best = c; }
                    }
                }
            }
        }
        return best == null ? new Point(0,0) : best;
    }

    private int eval(Point p, int pl) {
        int t = 0;
        int[][] dirs = {{1,0}, {0,1}, {1,1}, {1,-1}};
        for (int[] d : dirs) {
            int l = 1 + countInDir(p.x, p.y, d[0], d[1], pl) + countInDir(p.x, p.y, -d[0], -d[1], pl);
            if (l >= 5) t += 1000; else if (l == 4) t += 100; else if (l == 3) t += 10;
        }
        return t;
    }

    private boolean isWithinDistance(Point p) {
        if (board.isEmpty()) return true;
        for (Point bp : board.keySet()) {
            if (Math.abs(bp.x - p.x) <= 5 && Math.abs(bp.y - p.y) <= 5) return true;
        }
        return false;
    }

    public boolean isAiTurn() { return !gameOver && vsAI && ((isXTurn && aiPlayerSide == 1) || (!isXTurn && aiPlayerSide == 2)); }
    public Map<Point, Integer> getBoard() { return board; }
    public int getScoreX() { return scoreX; }
    public int getScoreO() { return scoreO; }
    public int getTimeLeft() { return timeLeft; }
    public String getStatusMessage() { return statusMessage; }
    public boolean isGameOver() { return gameOver; }
    public String getCurrentPlayerName() { return isXTurn ? "X" : "O"; }
}