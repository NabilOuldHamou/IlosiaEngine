package eu.ilosiaengine.engine.hud;

import eu.ilosiaengine.engine.GameObject;

public interface IHud {

    GameObject[] getGameObjects();

    default void cleanup() {
        GameObject[] gameObjects = getGameObjects();
        for (GameObject gameObject : gameObjects) {
            gameObject.getMesh().cleanup();
        }
    }

}
