package eu.ilosiaengine.engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture {

    private final int id;

    private final int width;
    private final int height;

    public Texture(String fileName) throws Exception {
        ByteBuffer buffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buffer = STBImage.stbi_load(fileName, w, h, channels, 4);
            if (buffer == null) {
                throw new Exception("Image file (" + fileName + ") not loaded:" + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        this.id = createTexture(buffer);
        STBImage.stbi_image_free(buffer);
    }

    public Texture(ByteBuffer byteBuffer) throws Exception {
        ByteBuffer buffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buffer = STBImage.stbi_load_from_memory(byteBuffer, w, h, channels, 4);
            if (buffer == null) {
                throw new Exception("Image file (ByteBuffer) not loaded:" + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        this.id = createTexture(buffer);
        STBImage.stbi_image_free(buffer);
    }

    private int createTexture(ByteBuffer buffer) {
        int textureId = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        return textureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    public void cleanup() {
        GL30.glDeleteTextures(id);
    }
}
