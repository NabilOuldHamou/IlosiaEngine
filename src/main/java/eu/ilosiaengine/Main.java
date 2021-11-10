package eu.ilosiaengine;

import eu.ilosiaengine.engine.Engine;
import eu.ilosiaengine.engine.IGameLogic;

public class Main {

    public static void main(String[] args) {

        try {
            IGameLogic gameLogic = new DummyGame();
            Engine engine = new Engine("GAME",
                    1280, 720, true, gameLogic);
            engine.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
