package eu.ilosiaengine;

import eu.ilosiaengine.core.Engine;
import eu.ilosiaengine.core.Window;

public class Main {

    private static Window window;
    private static TestGameLogic game;

    public static void main(String[] args) {
        window = new Window("Game", 1280, 720, false);
        game = new TestGameLogic();
        Engine engine = new Engine();

        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Window getWindow() {
        return window;
    }

    public static TestGameLogic getGame() {
        return game;
    }
}
