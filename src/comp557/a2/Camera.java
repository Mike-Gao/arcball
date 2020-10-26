package comp557.a2;

import javax.swing.JPanel;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import mintools.parameters.DoubleParameter;
import mintools.parameters.Parameter;
import mintools.parameters.ParameterListener;
import mintools.parameters.Vec3Parameter;
import mintools.swing.VerticalFlowPanel;

/**
 * Camera class to be used both for viewing the scene, but also to draw the scene from 
 * a point light.
 */
public class Camera {

    Vec3Parameter position = new Vec3Parameter("position", 0, 0, 10 );
    Vec3Parameter lookat = new Vec3Parameter("look at", 0, 0, 0 );
    Vec3Parameter up = new Vec3Parameter("up", 0, 1, 0 );

    DoubleParameter near = new DoubleParameter( "near plane", 1, 0.1, 10 );
    DoubleParameter far = new DoubleParameter( "far plane" , 40, 1, 100 );
    DoubleParameter fovy = new DoubleParameter( "fovy degrees" , 27, 14, 67 );

    /** Viewing matrix to be used by the pipeline */
    Matrix4d V = new Matrix4d();
    /** Projection matrix to be used by the pipeline */
    Matrix4d P = new Matrix4d();

    public Camera() {
        near.addParameterListener( new ParameterListener<Double>() {
            @Override
            public void parameterChanged(Parameter<Double> parameter) {
                // Let's keep near and far from crossing!
                if ( near.getValue() >= far.getValue() ) {
                    far.setValue( near.getValue() + 0.1 );
                }
            }
        });
        far.addParameterListener( new ParameterListener<Double>() {
            @Override
            public void parameterChanged(Parameter<Double> parameter) {
                // Let's keep near and far from crossing!
                if ( far.getValue() <= near.getValue() ) {
                    near.setValue( far.getValue() - 0.1 );
                }
            }
        });
    }

    /**
     * Update the projection and viewing matrices
     * We'll do this every time we draw, though we could choose to more efficiently do this only when parameters change.
     * @param width of display window (for aspect ratio)
     * @param height of display window (for aspect ratio)
     */
    public void updateMatrix( double width, double height ) {

        // TODO: Objective 2: Replace the default viewing matrix with one constructed from the parameters available in this class!

        Vector3d eye = new Vector3d(position.x, position.y, position.z);
        Vector3d x = new Vector3d();
        Vector3d y = new Vector3d(up.x, up.y, up.z);
        Vector3d w = new Vector3d(position.x - lookat.x, position.y - lookat.y, position.z - lookat.z);
        x.normalize();
        y.normalize();
        w.normalize();

        Vector3d u = new Vector3d();
        u.cross(w, y);
        u.normalize();

        Vector3d v = new Vector3d();
        v.cross(w, u);

        Matrix4d viewMatrix = new Matrix4d(new double[] {
                u.x, v.x, w.x, eye.x,
                u.y, v.y, w.y, eye.y,
                u.z, v.z, w.z, eye.z,
                0, 0, 0, 1,
        });
        V.set(viewMatrix);
        V.invert();

        // TODO: Objective 3: Replace the default projection matrix with one constructed from the parameters available in this class!
        double aspectRatio = width / height;
        double n = this.near.getValue();
        double f = this.far.getValue();
        double tan = Math.tan((fovy.getValue() * 0.5) * Math.PI / 180.0);
        double top = n * tan;
        double bottom = -top;
        double right = top * aspectRatio;
        double left = -right;

        P.set( new double[] {
                2* n / (right - left),  0,  (right + left) / (right - left),  0,
                0,  2 * n / (top - bottom),  (top + bottom) / (top - bottom),  0,
                0,  0, (f + n) / (n - f), (2 * n * f) / (n - f),
                0,  0, -1,  0,
        } );

    }

    /**
     * @return controls for the camera
     */
    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.add( position );
        vfp.add( lookat );
        vfp.add( up );
        vfp.add( near.getControls() );
        vfp.add( far.getControls() );
        vfp.add( fovy.getControls() );
        return vfp.getPanel();
    }

}
