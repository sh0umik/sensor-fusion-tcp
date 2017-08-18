package org.hitlabnz.sensor_fusion_demo;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.hitlabnz.sensor_fusion_demo.orientationProvider.OrientationProvider;
import org.hitlabnz.sensor_fusion_demo.representation.Quaternion;

import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class that implements the rendering of a cube with the current rotation of the device that is provided by a
 * OrientationProvider
 * 
 * @author Alexander Pacha
 * 
 */
public class CubeRenderer implements GLSurfaceView.Renderer {
    /**
     * The colour-cube that is drawn repeatedly
     */
    private Cube mCube;

    /**
     * The current provider of the device orientation.
     */
    private OrientationProvider orientationProvider = null;
    private Quaternion quaternion = new Quaternion();

    /**
     * Initialises a new CubeRenderer
     */

    private Socket socket;

    public static String w, x , y, z;

    public CubeRenderer() {
        mCube = new Cube();
    }

    /**
     * Sets the orientationProvider of this renderer. Use this method to change which sensor fusion should be currently
     * used for rendering the cube. Simply exchange it with another orientationProvider and the cube will be rendered
     * with another approach.
     * 
     * @param orientationProvider The new orientation provider that delivers the current orientation of the device
     */
    public void setOrientationProvider(OrientationProvider orientationProvider) {
        this.orientationProvider = orientationProvider;
    }

    /**
     * Perform the actual rendering of the cube for each frame
     * 
     * @param gl The surface on which the cube should be rendered
     */


    public void onDrawFrame(GL10 gl) {
        // clear screen
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // set-up modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        if (showCubeInsideOut) {
            float dist = 3;
            gl.glTranslatef(0, 0, -dist);

            if (orientationProvider != null) {
                // All Orientation providers deliver Quaternion as well as rotation matrix.
                // Use your favourite representation:

                // Get the rotation from the current orientationProvider as rotation matrix
                //gl.glMultMatrixf(orientationProvider.getRotationMatrix().getMatrix(), 0);

                // Get the rotation from the current orientationProvider as quaternion
                orientationProvider.getQuaternion(quaternion);
                gl.glRotatef((float) (2.0f * Math.acos(quaternion.getW()) * 180.0f / Math.PI), quaternion.getX(), quaternion.getY(), quaternion.getZ());

                w = String.valueOf(quaternion.getW());
                x = String.valueOf(quaternion.getX());
                y = String.valueOf(quaternion.getY());
                z = String.valueOf(quaternion.getZ());

                // send data to server

                // I need the socClient here , socClient must be initialized once , the cube renderer initialized every time the view
                // changed so cant initialize here ...
                //  socClient is initialized in a thread in SensorSelectionActivity main class onCreate method

                /*
                try {

                    DataOutputStream responseStream = new DataOutputStream(socClient.getOutputStream());
                    String data = w + "|" + x + "|" + y + "|" + z + "\n";
                    System.out.println("Sending : " + data);
                    responseStream.writeBytes(data);
                    responseStream.flush();

                    //mySocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */



            }

            // draw our object
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            mCube.draw(gl);
        } else {

            if (orientationProvider != null) {
                // All Orientation providers deliver Quaternion as well as rotation matrix.
                // Use your favourite representation:

                // Get the rotation from the current orientationProvider as rotation matrix
                //gl.glMultMatrixf(orientationProvider.getRotationMatrix().getMatrix(), 0);

                // Get the rotation from the current orientationProvider as quaternion
                orientationProvider.getQuaternion(quaternion);
                gl.glRotatef((float) (2.0f * Math.acos(quaternion.getW()) * 180.0f / Math.PI), quaternion.getX(), quaternion.getY(), quaternion.getZ());
            }

            float dist = 3;
            drawTranslatedCube(gl, 0, 0, -dist);
            drawTranslatedCube(gl, 0, 0, dist);
            drawTranslatedCube(gl, 0, -dist, 0);
            drawTranslatedCube(gl, 0, dist, 0);
            drawTranslatedCube(gl, -dist, 0, 0);
            drawTranslatedCube(gl, dist, 0, 0);
        }

        // draw our object
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        mCube.draw(gl);
    }

    /**
     * Draws a translated cube
     * 
     * @param gl the surface
     * @param translateX x-translation
     * @param translateY y-translation
     * @param translateZ z-translation
     */
    private void drawTranslatedCube(GL10 gl, float translateX, float translateY, float translateZ) {
        gl.glPushMatrix();
        gl.glTranslatef(translateX, translateY, translateZ);

        // draw our object
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        mCube.draw(gl);
        gl.glPopMatrix();
    }

    /**
     * Update view-port with the new surface
     * 
     * @param gl the surface
     * @param width new width
     * @param height new height
     */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // set view-port
        gl.glViewport(0, 0, width, height);
        // set projection matrix
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // dither is enabled by default, we don't need it
        gl.glDisable(GL10.GL_DITHER);
        // clear screen in black
        gl.glClearColor(0, 0, 0, 1);
    }

    /**
     * Flag indicating whether you want to view inside out, or outside in
     */
    private boolean showCubeInsideOut = true;

    /**
     * Toggles whether the cube will be shown inside-out or outside in.
     */
    public void toggleShowCubeInsideOut() {
        this.showCubeInsideOut = !showCubeInsideOut;
    }

}
