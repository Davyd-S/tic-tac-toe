package ua.university.tictactoe;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class GameController {
    private GameModel model;

    @PostMapping("/start")
    public Map<String, Object> startGame(@RequestParam String mode,
                                         @RequestParam boolean vsAi,
                                         @RequestParam int aiSide) {
        GameModel.GameMode m = mode.equals("TIME_ATTACK") ?
                GameModel.GameMode.TIME_ATTACK : GameModel.GameMode.FIRST_LINE;

        // Створюємо нову модель кожного разу при старті
        model = new GameModel(m, 60, vsAi, aiSide);

        // Якщо ШІ має ходити першим (грає за X)
        if (model.isAiTurn()) {
            Point p = model.getBestMove();
            model.makeMove(p.x, p.y);
        }
        return getGameState();
    }

    @PostMapping("/move")
    public Map<String, Object> move(@RequestParam int x, @RequestParam int y) {
        if (model == null) return Map.of("error", "Game's begone!");

        if (model.makeMove(x, y)) {
            if (model.isAiTurn()) {
                Point p = model.getBestMove();
                model.makeMove(p.x, p.y);
            }
        }
        return getGameState();
    }

    private Map<String, Object> getGameState() {
        return Map.of(
                "board", model.getBoard(),
                "status", model.getStatusMessage(),
                "scoreX", model.getScoreX(),
                "scoreO", model.getScoreO(),
                "gameOver", model.isGameOver()
        );
    }
}