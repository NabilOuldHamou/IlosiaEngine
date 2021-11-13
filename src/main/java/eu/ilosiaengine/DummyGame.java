package eu.ilosiaengine;

import eu.ilosiaengine.engine.Camera;
import eu.ilosiaengine.engine.GameObject;
import eu.ilosiaengine.engine.IGameLogic;
import eu.ilosiaengine.engine.Window;
import eu.ilosiaengine.engine.graphics.Mesh;
import eu.ilosiaengine.engine.graphics.Renderer;
import eu.ilosiaengine.engine.io.MouseInput;
import eu.ilosiaengine.engine.utils.OBJLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.8f;
    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;
    private Window window;
    private GameObject[] gameItems;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f();
    }

    @Override
    public void init(Window window) throws Exception {
        this.window = window;
        renderer.init(window);

        Mesh mesh = OBJLoader.loadMesh("/Killjoy.obj");
        GameObject gameObject = new GameObject(mesh);
        gameObject.setScale(1.5f);
        gameItems = new GameObject[] {gameObject};

        GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 1;
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        Vector2f rotVec = mouseInput.getDisplacementVec();
        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        GLFW.glfwSetCursorPos(window.getWindow(), window.getWidth() / 2, window.getHeight() / 2);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameObject gameItem : gameItems) {
            gameItem.getMesh().cleanup();
        }
    }

}
