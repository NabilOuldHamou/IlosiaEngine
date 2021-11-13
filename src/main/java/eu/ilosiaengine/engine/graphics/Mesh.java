package eu.ilosiaengine.engine.graphics;

import org.joml.Vector3f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Mesh {

    private static final Vector3f DEFAULT_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);

    private final int vaoId;
    private final List<Integer> vboIdList;
    private final int vertexCount;
    private Texture texture;
    private Vector3f color;

    public Mesh(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        FloatBuffer verticesBuffer = null;
        FloatBuffer textureCoordsBuffer = null;
        FloatBuffer normalsBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            color = DEFAULT_COLOR;
            vertexCount = indices.length;
            vboIdList = new ArrayList<>();

            vaoId = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoId);

            // Vertices
            int vboId = GL30.glGenBuffers();
            verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();
            GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL30.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
            GL30.glEnableVertexAttribArray(0);
            GL30.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = GL30.glGenBuffers();
            vboIdList.add(vboId);
            textureCoordsBuffer = MemoryUtil.memAllocFloat(textureCoords.length);
            textureCoordsBuffer.put(textureCoords).flip();
            GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL30.glBufferData(GL15.GL_ARRAY_BUFFER, textureCoordsBuffer, GL15.GL_STATIC_DRAW);
            GL30.glEnableVertexAttribArray(1);
            GL30.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = GL30.glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
            GL30.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);


            GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);

        } finally {
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer);
            }
            if (textureCoordsBuffer != null) {
                MemoryUtil.memFree(textureCoordsBuffer);
            }
            if (normalsBuffer != null) {
                MemoryUtil.memFree(normalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }

    }

    public void render() {

       if (texture != null) {
           GL30.glActiveTexture(GL13.GL_TEXTURE0);

           GL30.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
       }

        GL30.glBindVertexArray(vaoId);

        GL30.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);
        GL30.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        GL20.glDisableVertexAttribArray(0);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            GL30.glDeleteBuffers(vboId);
        }

        if (texture != null) {
            texture.cleanup();
        }

        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
    }

    public int getVaoId() {
        return this.vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }
}
