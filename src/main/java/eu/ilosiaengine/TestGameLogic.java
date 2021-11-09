package eu.ilosiaengine;

import eu.ilosiaengine.core.ILogic;
import eu.ilosiaengine.core.ObjectLoader;
import eu.ilosiaengine.core.Window;
import eu.ilosiaengine.core.render.Model;
import eu.ilosiaengine.core.render.Renderer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGameLogic implements ILogic {

    private int direction = 0;
    private float colour = 0.0f;

    private final Renderer renderer;
    private final ObjectLoader loader;
    private final Window window;

    private Model model;

    public TestGameLogic() {
        renderer = new Renderer();
        window = Main.getWindow();
        loader = new ObjectLoader();
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = {
            -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
            -0.5f, 0.5f, 0f
        };

        model = loader.loadModel(vertices);
    }

    @Override
    public void input() {
        if (window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            direction = 1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            direction -= 1;
        } else {
            direction = 0;
        }
    }

    @Override
    public void update() {
        colour += direction * 0.01f;

        if (colour > 1) {
            colour = 1.0f;
        } else if (colour <= 0) {
            colour = 0.0f;
        }
    }

    @Override
    public void render() {
        if (window.isResize()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColor(colour, colour, colour, 0);
        renderer.render(model);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
