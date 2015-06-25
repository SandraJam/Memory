package com.example.sandra.apprendogles;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by sandra on 12/04/15.
 */
public class Triangle {
    private FloatBuffer vertexBuffer;
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = new float[9];
    float color[] = new float[4];
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private int mMVPMatrixHandle;


    public Triangle(float triangleCoords2[], float colors[]){
        for (int i=0; i < triangleCoords2.length; i++){
            triangleCoords[i] = triangleCoords2[i];
        }
        color = colors;
        vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length*4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void setColor(String couleur) {
        switch(couleur){
            case "black":
                color = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
                break;
        }
    }

    public float[] getTriangleCoords() {
        return triangleCoords;
    }

    public float[] getColor() {
        return color;
    }

    public boolean compareColor(Triangle triangle) {
        float [] couleur1 = triangle.getColor();
        for (int i=0; i < couleur1.length; i++){
            if (couleur1[i] != color[i]){
                return false;
            }
        }
        return true;
    }
}
