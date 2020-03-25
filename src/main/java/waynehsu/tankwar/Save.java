package waynehsu.tankwar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor //發射json使用
// use public class, for JSON, it is a requirement
public class Save {

    private boolean gameContinued;

    private Position playerPosition;

    private List<Position> enemyPositions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Position { // for all the tank the position, the parameters are universal
        private int x, y;
        private Direction direction;
    }

}
