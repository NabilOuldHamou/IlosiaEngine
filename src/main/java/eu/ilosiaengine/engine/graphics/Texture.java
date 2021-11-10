package eu.ilosiaengine.engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture {

    private final int id;

    public Texture(String fileName) throws Exception {
        this(loadTexture(fileName));
    }

    public Texture(int id) {
        this.id = id;
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    private static int loadTexture(String fileName) throws Exception {
        int width;
        int height;
        ByteBuffer buffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buffer = STBImage.stbi_load(fileName, w, h, channels, 4);

            if (buffer == null) {
                throw new Exception("Image file (" + fileName + ") not loaded: " + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        int textureId = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        STBImage.stbi_image_free(buffer);

        return textureId;
    }

    public void cleanup() {
        GL11.glDeleteTextures(id);
    }

    public int getId() {
        return id;
    }
}
