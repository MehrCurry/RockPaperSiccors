package de.gzockoll.rps.boundary;

import de.gzockoll.rps.control.GameController;
import de.gzockoll.rps.domain.Game;
import de.gzockoll.rps.domain.GameType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by guido on 21.06.16.
 */
@RestController
@RequestMapping("/game")
public class GameResource {
    private final GameController gameController;

    @Autowired
    public GameResource(GameController gameController) {
        this.gameController = gameController;
    }

    @ApiOperation(value = "creates a new game",
            notes = "Allowed types are: STANDARD and EXTENDED. The id from the returned game hast to be added to each match",
            response = Game.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully create game", response = Game.class),
            @ApiResponse(code = 422, message = "You provided an invalid game tyoe"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public Game createGame(@RequestBody(required = false) GameRequestTO request) {
        checkArgument((request == null || request.getType() == null || Arrays.stream(GameType.values())
                .map(GameType::name)
                .anyMatch(n -> n.equalsIgnoreCase(request.getType()))), "Unknown game type. Allowed types are " + GameType.allowedTypes());
        Optional<GameType> opt = Optional.ofNullable(request).map(r -> r.getType()).map(t -> GameType.valueOf(t.toUpperCase()));
        GameType type = opt.orElse(GameType.STANDARD);
        return gameController.createGame(type);
    }

    void checkArgument(boolean condition, String message) {
        if (!condition) {
            throw new GameType.UnknownGameTypeException(message);
        }
    }
}
