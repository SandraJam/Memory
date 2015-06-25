package com.example.sandra.apprendogles;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.DisplayMetrics;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by sandra on 12/04/15.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer{
    // Taille doit etre pair et < 25
    private int cptFinDeJeu =0;
    private int la = 4;
    private int lo = 6;
    private int taille = la*lo;
    private int change = -1;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mAngle =  new float[taille];
    private float delta = 0.05f;
    private Triangle [] boite = new Triangle[taille];
    private Square [] cartes = new Square[taille];
    private float[][] couleurs = new float[taille][4];
    private final float[] mRotationMatrix = new float[16];
    private DisplayMetrics metrics;
    private int other = -1;
    public GL10 g;
    public EGLConfig configu;

    public MyGLRenderer(DisplayMetrics met){
        metrics = met;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        g = gl;
        configu = config;
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        usineACouleur();
        usineATriangle();
    }

    // Crée les triangles et les cartes
    public void usineATriangle(){
        float z = 0.0f;
        float x1 = 0.450f;
        float y1 = 0.95f;
        float x2 = 0.325f;
        float y2 = 0.65f;
        float x3 = 0.575f;
        float y3 = 0.65f;

        for (int i=0; i < taille; i++){
            float tab[] = {x1, y1, z, x2, y2, z, x3, y3, z};
            boite[i] = new Triangle(tab, couleurs[i]);
            float tab2[] = {x3 , y1, z, x3, y3, z, x2, y2, z, x2, y1, z};
            cartes[i] = new Square(tab2);
            x1 = x1 - 0.25f - delta;
            if (x1 < -0.5f){
                x1 = 0.450f;
                x2 = 0.325f;
                x3 = 0.575f;
                y1 = y1 - 0.32f;
                y2 = y2 - 0.32f;
                y3 = y3 - 0.32f;
            }else{
                x2 = x2 - 0.25f- delta;
                x3 = x3 - 0.25f- delta;
            }
        }
    }

    // Crée le tableau de couleur etr le mélange
    public void usineACouleur(){
        float r;
        float g;
        float b;
        float a=0.0f;
        float[] tab;
        for (int i=0; i < taille; i = i+2){
            r = (float) Math.random();
            g = (float) Math.random();
            b = (float) Math.random();
            tab = new float[]{r, g, b, a};
            while(inCouleur(tab, i)){
                r = (float) Math.random();
                g = (float) Math.random();
                b = (float) Math.random();
                tab = new float[]{r, g, b, a};
            }
            couleurs[i] = new float[]{r, g, b, a};
            couleurs[i+1] = new float[]{r, g, b, a};
        }
        shuffleArray();
    }

    // Evite les doublons de couleurs
    public boolean inCouleur(float[] tab, int a){
        for (int i = 0; i < a; i++){
            if (couleurs[i].equals(tab)){
                return true;
            }
        }
        return false;
    }

    public void shuffleArray()
    {
        Random rnd = new Random();
        for (int i = couleurs.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            float[] a = couleurs[index];
            couleurs[index] = couleurs[i];
            couleurs[i] = a;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float[] scratch = new float[16];
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        for (int i=0; i < boite.length; i++){
            boite[i].draw(mMVPMatrix);
            Matrix.setRotateM(mRotationMatrix, (int)boite[i].getTriangleCoords()[0], mAngle[i],  0,  1.0f, 0);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            cartes[i].draw(scratch);
        }

        if (change != -1){

            if (other != -1) {

                if (boite[change].compareColor(boite[other]) && other != change) {
                    cptFinDeJeu += 2;
                    boite[change].setColor("black");
                    boite[change].draw(mMVPMatrix);
                    boite[other].setColor("black");
                    boite[other].draw(mMVPMatrix);
                    cartes[change].setColor("black");
                    cartes[change].draw(mMVPMatrix);
                    cartes[other].setColor("black");
                    cartes[other].draw(mMVPMatrix);
                }
                mAngle[other] = 0;
                mAngle[change] = 0;
                other =-1;
                change = -1;
                if (isFinish()){
                   cptFinDeJeu = 0;
                   onSurfaceCreated(g, configu);
                }
            }
        }
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    // Trouve la case qui correspond au touch
    public void changeToBlack(float y, float x) {
        int attla=0, attlo = 0, cptla=0, cptlo=0;
        while(attla < metrics.widthPixels){
            if (x > attla && x < attla+metrics.widthPixels/la){
                break;
            }
            attla += metrics.widthPixels/la;
            cptla++;
        }
        while(attlo < metrics.heightPixels){
            if (y > attlo && y < attlo+metrics.heightPixels/lo){
                break;
            }
            cptlo++;
            attlo += metrics.heightPixels/lo;
        }
        if (cptlo * la + cptla < taille) {
            if (isnotBlack(boite[cptlo * la + cptla].getColor())) {
                if (change == -1) {
                    change = cptlo * la + cptla;
                    mAngle[change] = 180;
                } else {
                    other = cptlo * la + cptla;
                    mAngle[other] = 180;
                }
            }
        }
    }

    private boolean isnotBlack(float[] color) {
        for (int i=0; i < color.length; i++){
            if (color[i] != 0.0f){
                return true;
            }
        }
        return false;
    }

    public boolean isFinish() {
        return cptFinDeJeu == taille;
    }
}
