package eu.ilosiaengine.core.render;

public class Model {

    private int id;
    private int vertexCount;

    public Model(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
    }

    public int getId() {
        return this.id;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
