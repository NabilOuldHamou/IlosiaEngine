package eu.ilosiaengine.core;

import eu.ilosiaengine.Main;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class Engine {

    public static final long NANOSECOND = 1000000000L;
    public static final float MAX_FRAMERATE = 500;

    private static int fps;
    private static float frametime = 1.0f / MAX_FRAMERATE;

    private boolean isRunning;

    private Window window;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;


    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err).set());

        window = Main.getWindow();
        gameLogic = Main.getGame();

        window.init();
        gameLogic.init();
    }

    public void start() throws Exception {

        init();

        if (isRunning) {
            return;
        }

        run();
    }

    public void run() {
        isRunning = true;

        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (isRunning) {

            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            input();

            while (unprocessedTime > frametime) {
                render = true;
                unprocessedTime -= frametime;

                if (window.windowShouldClose()) {
                    stop();
                }

                if (frameCounter >= NANOSECOND) {
                    fps = frames;

                    System.out.println(fps);

                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                update();
                render();

                frames++;
            }
        }

        cleanup();
    }

    private void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
    }

    private void input(){
        gameLogic.input();
    }

    private void render() {
        gameLogic.render();
        window.update();
    }

    private void update() {
        gameLogic.update();
    }

    private void cleanup() {
        window.cleanup();
        gameLogic.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int newFps) {
        fps = newFps;
    }

}
