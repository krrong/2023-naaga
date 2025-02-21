package com.now.naaga.gameresult.repository;

import com.now.naaga.common.builder.GameBuilder;
import com.now.naaga.common.builder.GameResultBuilder;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.game.domain.Game;
import com.now.naaga.gameresult.domain.GameResult;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.gameresult.domain.ResultType.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
@SpringBootTest
class GameResultRepositoryTest {

    @Autowired
    private GameResultRepository gameResultRepository;

    @Autowired
    private PlayerBuilder playerBuilder;
    @Autowired
    private PlaceBuilder placeBuilder;
    @Autowired
    private GameResultBuilder gameResultBuilder;
    @Autowired
    private GameBuilder gameBuilder;

    @Test
    void 맴버아이디로_게임결과를_조회했을때_게임결과에_모든정보가_조회된다() {
        //given
        final Player player = playerBuilder.init()
                .build();

        final Place place = placeBuilder.init()
                .position(잠실역_교보문고_좌표)
                .registeredPlayer(player)
                .build();

        final Game game1 = gameBuilder.init()
                .startPosition(잠실_루터회관_정문_좌표)
                .player(player)
                .place(place)
                .gameStatus(DONE)
                .build();

        final Game game2 = gameBuilder.init()
                .startPosition(잠실_루터회관_정문_좌표)
                .player(player)
                .place(place)
                .gameStatus(DONE)
                .build();

        final GameResult gameResult1 = gameResultBuilder.init()
                .resultType(SUCCESS)
                .game(game1)
                .build();
        final GameResult gameResult2 = gameResultBuilder.init()
                .resultType(SUCCESS)
                .game(game2)
                .build();

        // when
        List<GameResult> gameResults = gameResultRepository.findByPlayerId(player.getId());

        // then
        assertThat(gameResults).containsExactlyInAnyOrder(gameResult1, gameResult2);

    }

}
