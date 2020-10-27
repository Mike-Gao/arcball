package comp557.a2;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;

/**
 * Left Mouse Drag Arcball
 * @author Mike Gao - 260915701
 */
public class ArcBall {

    private DoubleParameter fit = new DoubleParameter( "Fit", 1, 0.5, 2 );
    private DoubleParameter gain = new DoubleParameter( "Gain", 1, 0.5, 2, true );

    /** The accumulated rotation of the arcball */
    Matrix4d R = new Matrix4d();

    Vector3d start = new Vector3d();
    Vector3d current = new Vector3d();

    public ArcBall() {
        R.setIdentity();
    }

    /**
     * Convert the x y position of the mouse event to a vector for your arcball computations
     * @param e
     * @param v
     */
    public void setVecFromMouseEvent( MouseEvent e, Vector3d v ) {
        Component c = e.getComponent();
        Dimension dim = c.getSize();
        double width = dim.getWidth();
        double height = dim.getHeight();
        int mouse_x = e.getX();
        int mouse_y = e.getY();


        // TODO: Objective 1: finish arcball vector helper function
        double center_x = width / 2.0;
        double center_y = height / 2.0;

        double radius =  0.5 * Math.min(width,height) / fit.getValue();
        double rx = (mouse_x - center_x) / radius;
        double ry = -(mouse_y - center_y) / radius;
        double r = rx * rx + ry * ry;
        double rz = 0.0;
        if (r > 1.0) {
            double s = 1.0/Math.sqrt(r);
            rx = s * rx;
            ry = s * ry;
        } else {
            rz = Math.sqrt( 1.0 - r );
        }
        v.set(rx,ry,rz);
        v.normalize();
    }

    public void attach( Component c ) {
        c.addMouseMotionListener( new MouseMotionListener() {
            @Override
            public void mouseMoved( MouseEvent e ) {}
            @Override
            public void mouseDragged( MouseEvent e ) {
                if ( (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 ) {
                   // TODO: Objective 1: Finish arcball rotation update on mouse drag when button 1 down!
                    setVecFromMouseEvent(e, current);
                    double ang = start.angle(current);
                    ang *= gain.getValue();
                    Vector3d axis = new Vector3d();
                    axis.cross(start,current);
                    Matrix4d rotMatrix = new Matrix4d();
                    rotMatrix.set(new AxisAngle4d(axis, ang));
                    R.mul(rotMatrix);
                }
            }
        });
        c.addMouseListener( new MouseListener() {
            @Override
            public void mouseReleased( MouseEvent e) {}
            @Override
            public void mousePressed( MouseEvent e) {
                // TODO: Objective 1
                setVecFromMouseEvent(e, start);
            }
            @Override
            public void mouseExited(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseClicked(MouseEvent e) {}
        });
    }

    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.setBorder( new TitledBorder("ArcBall Controls"));
        vfp.add( fit.getControls() );
        vfp.add( gain.getControls() );
        return vfp.getPanel();
    }

}
