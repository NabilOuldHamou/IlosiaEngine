package eu.ilosiaengine.engine;

import eu.ilosiaengine.engine.io.MouseInput;

public class Engine implements Runnable {

    public static final int TICKS_PER_SECOND = 30;

    private final Window window;
    private final IGameLogic gameLogic;
    private final MouseInput mouseInput;

    public Engine(String title, int width, int height, boolean vSync, IGameLogic gameLogic) throws Exception {
        window = new Window(title, width, height, vSync);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        window.init();
        gameLogic.init(window);
        mouseInput.init(window);
    }

    private void gameLoop() {
        boolean running = true;

        int fps = 0;
        int ticks = 0;
        float tickRate = 1f / TICKS_PER_SECOND;
        float accumulator = 0;
        long currentTime;
        long lastUpdate = System.currentTimeMillis();
        long time = System.currentTimeMillis();

        while (running && !window.windowShouldClose()) {
            currentTime = System.currentTimeMillis();
            float lastRenderTimeInSeconds = (currentTime - lastUpdate) / 1000f;
            accumulator += lastRenderTimeInSeconds;
            lastUpdate = currentTime;

            input();

            while (accumulator > tickRate) {
                update(tickRate);
                ticks++;
                accumulator -= tickRate;
            }

            render();
            fps++;

            if (System.currentTimeMillis() + 1000 >= time) {
                System.out.println(fps + " " + ticks);
                time += 1000;
                ticks = 0;
                fps = 0;
            }
        }
    }

    protected void cleanup() {
        gameLogic.cleanup();
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    protected void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }

}
