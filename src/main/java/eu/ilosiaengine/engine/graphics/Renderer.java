package eu.ilosiaengine.engine.graphics;

import eu.ilosiaengine.engine.Camera;
import eu.ilosiaengine.engine.GameObject;
import eu.ilosiaengine.engine.Window;
import eu.ilosiaengine.engine.lighting.DirectionalLight;
import eu.ilosiaengine.engine.lighting.PointLight;
import eu.ilosiaengine.engine.lighting.SpotLight;
import eu.ilosiaengine.engine.utils.Transformation;
import eu.ilosiaengine.engine.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000f;
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;

    private Transformation transformation;
    private ShaderProgram shaderProgram;

    private float specularPower;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {

        // Creating the shader program
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shaderProgram.link();

        // Creating the uniforms that will be used in the shaders
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");
        // MATERIAL
        shaderProgram.createMaterialUniform("material");
        // LIGHTING
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        shaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        shaderProgram.createDirectionalLightUniform("directionalLight");
    }

    public void render(Window window, Camera camera, GameObject[] gameObjects,
                       Vector3f ambientLight, PointLight[] pointLights,
                       SpotLight[] spotLights, DirectionalLight directionalLight) {

        clear();

        if (window.isResized()) {
            GL30.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // UPDATING THE PROJECTION MATRIX
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV,
                window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // UPDATING THE VIEW MATRIX
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        // UPDATING THE LIGHT UNIFORMS
        renderLights(viewMatrix, ambientLight, pointLights, spotLights, directionalLight);

        shaderProgram.setUniform("texture_sampler", 0);
        // RENDERING THE GAME OBJECTS
        for (GameObject gameObject : gameObjects) {

            Mesh mesh = gameObject.getMesh();
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameObject, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            shaderProgram.setUniform("material", mesh.getMaterial());

            mesh.render();
        }

        shaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, Vector3f ambientLight, PointLight[] pointLights,
                              SpotLight[] spotLights, DirectionalLight directionalLight) {

        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);

        // PROCESS POINT LIGHTS
        int numLights = pointLights != null ? pointLights.length : 0;

        for (int i = 0; i < numLights; i++) {
            PointLight currPointLight = new PointLight(pointLights[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            shaderProgram.setUniform("pointLights", currPointLight, i);
        }

        numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            SpotLight currSpotLight = new SpotLight(spotLights[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
            Vector3f lightPos = currSpotLight.getPointLight().getPosition();

            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            shaderProgram.setUniform("spotLights", currSpotLight, i);
        }

        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform("directionalLight", currDirLight);
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
