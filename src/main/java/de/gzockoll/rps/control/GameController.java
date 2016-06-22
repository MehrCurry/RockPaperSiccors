package de.gzockoll.rps.control;

import de.gzockoll.rps.boundary.ResultTO;
import de.gzockoll.rps.domain.Choice;
import de.gzockoll.rps.domain.DumpRobot;
import de.gzockoll.rps.domain.Game;
import de.gzockoll.rps.domain.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@Service
public class GameController {
    private GameRepository gameRepository;

    @Autowired
    public GameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createStandardGame() {
        Game game = Game.createStandardGame();
        gameRepository.save(game);
        return game;
    }

    public ResultTO makeMatch(String gameId, String aChoicesName) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new UnknownGameException("The specified game does not exist."));
        Choice choice = game.getChoiceByName(aChoicesName)
                .orElseThrow(() -> new IllegalChoiceException("Your choice is invalid in this game. Choose one of: "
                        + game.getChoices().stream().map(c -> c.getName()).collect(Collectors.joining(", "))));
        Choice opponentsChoice = DumpRobot.makeYourChoice(game);
        return ResultTO.builder()
                .gameId(game.getId())
                .yourChoice(choice.getName())
                .opponentsChoice(opponentsChoice.getName())
                .result(game.match(choice, opponentsChoice))
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UnknownGameException extends RuntimeException {
        public UnknownGameException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public static class IllegalChoiceException extends RuntimeException {
        public IllegalChoiceException(String message) {
            super(message);
        }
    }
}
