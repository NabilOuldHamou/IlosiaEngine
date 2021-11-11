package eu.ilosiaengine.engine.io;

import eu.ilosiaengine.engine.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {

    private final Vector2d previousPos;
    private final Vector2d currentPos;
    private final Vector2f displacementVec;

    private boolean inWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;
    private boolean middleButtonPressed = false;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displacementVec = new Vector2f();
    }

    public void init(Window window) {
        GLFW.glfwSetCursorPosCallback(window.getWindow(), (windowHandle, xPos, yPos) -> {
           currentPos.x = xPos;
           currentPos.y = yPos;
        });

        GLFW.glfwSetCursorEnterCallback(window.getWindow(), (windowHandle, entered) -> {
           inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(window.getWindow(), (windowHandle, button, action, mode) ->{
           leftButtonPressed = (button == GLFW.GLFW_MOUSE_BUTTON_1) && (action == GLFW.GLFW_PRESS);
           rightButtonPressed = (button == GLFW.GLFW_MOUSE_BUTTON_2) && (action == GLFW.GLFW_PRESS);
           middleButtonPressed = (button == GLFW.GLFW_MOUSE_BUTTON_3) && (action == GLFW.GLFW_PRESS);
        });
    }

    public void input(Window window) {
        displacementVec.x = 0;
        displacementVec.y = 0;

        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltaX = currentPos.x - previousPos.x;
            double deltaY = currentPos.y - previousPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if (rotateX) {
                displacementVec.y = (float) deltaX;
            }
            if (rotateY) {
                displacementVec.x = (float) deltaY;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public Vector2f getDisplacementVec() {
        return displacementVec;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public boolean isMiddleButtonPressed() {
        return middleButtonPressed;
    }
}
