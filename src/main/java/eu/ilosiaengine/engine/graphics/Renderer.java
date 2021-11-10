package eu.ilosiaengine.engine.graphics;

import eu.ilosiaengine.engine.GameObject;
import eu.ilosiaengine.engine.Window;
import eu.ilosiaengine.engine.utils.Utils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000f;

    private ShaderProgram shaderProgram;
    private Transformation transformation;

    public Renderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {

        // Creating the shader program
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shaderProgram.link();

        // Creating the projection matrix
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("worldMatrix");
    }

    public void render(Window window, GameObject[] gameObjects) {
        clear();

        if (window.isResized()) {
            GL30.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV,
                window.getWidth(), window.getHeight()
                , Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        for (GameObject gameObject : gameObjects) {

            Matrix4f worldMatrix =
                    transformation.getWorldMatrix(
                            gameObject.getPosition(),
                            gameObject.getRotation(),
                            gameObject.getScale()
                    );
            shaderProgram.setUniform("worldMatrix", worldMatrix);

            gameObject.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }

}