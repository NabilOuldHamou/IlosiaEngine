package eu.ilosiaengine.engine.graphics;

import eu.ilosiaengine.engine.GameObject;
import eu.ilosiaengine.engine.hud.FontTexture;
import eu.ilosiaengine.engine.lighting.Material;
import eu.ilosiaengine.engine.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TextObject extends GameObject {

    private static final float ZPOS = 0.0f;
    private static final int VERTICES_PER_QUAD = 4;

    private final FontTexture fontTexture;

    private String text;

    public TextObject(String text, FontTexture fontTexture) throws Exception {
        super();
        this.text = text;
        this.fontTexture = fontTexture;
        super.setMesh(this.buildMesh());
    }

    private Mesh buildMesh() {
        List<Float> positions = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();

        float[] normals = new float[0];
        List<Integer> indices = new ArrayList<>();
        char[] characters = text.toCharArray();
        int numChars = characters.length;

        float startX = 0;
        for (int i = 0; i < numChars; i++) {
            FontTexture.CharInfo charInfo = fontTexture.getCharInfo(characters[i]);

            // TOP LEFT VERTEX
            positions.add(startX);
            positions.add(0.0f);
            positions.add(ZPOS);
            textureCoords.add( (float) charInfo.getStartX() / (float) fontTexture.getWidth() );
            textureCoords.add(0.0f);
            indices.add(i*VERTICES_PER_QUAD);

            // BOTTOM LEFT VERTEX
            positions.add(startX);
            positions.add((float) fontTexture.getHeight());
            positions.add(ZPOS);
            textureCoords.add( (float) charInfo.getStartX() / (float) fontTexture.getWidth() );
            textureCoords.add(1.0f);
            textureCoords.add(1.0f);
            indices.add(i*VERTICES_PER_QUAD + 1);

            // BOTTOM RIGHT VERTEX
            positions.add(startX + charInfo.getWidth());
            positions.add((float) fontTexture.getHeight());
            positions.add(ZPOS);
            textureCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) /
                    (float) fontTexture.getWidth());
            textureCoords.add(1.0f);
            indices.add(i*VERTICES_PER_QUAD + 2);

            // TOP RIGHT VERTEX
            positions.add(startX + charInfo.getWidth());
            positions.add(0.0f);
            positions.add(ZPOS);
            textureCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) /
                    (float)fontTexture.getWidth());
            textureCoords.add(0.0f);
            indices.add(i*VERTICES_PER_QUAD + 3);

            // INDICES FOR TOP LEFT AND BOTTOM RIGHT VERTICES
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);

            startX += charInfo.getWidth();
        }

        float[] posArr = Utils.listToArray(positions);
        float[] textureCoordsArray = Utils.listToArray(textureCoords);
        int[] indicesArr = indices.stream().mapToInt(i->i).toArray();

        Mesh mesh = new Mesh(posArr, textureCoordsArray, normals, indicesArr);
        mesh.setMaterial(new Material(fontTexture.getTexture()));

        return mesh;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.getMesh().deleteBuffers();
        this.setMesh(buildMesh());
    }
}
