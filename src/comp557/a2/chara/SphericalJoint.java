package comp557.a2.chara;

import javax.vecmath.Vector3d;

import com.jogamp.opengl.GLAutoDrawable;
import comp557.a2.ShadowPipeline;
import mintools.parameters.DoubleParameter;

public class SphericalJoint extends GraphNode {
    private DoubleParameter rx,ry,rz;
    private Double tx,ty,tz,rxmin,rymin,rzmin,rxmax,rymax,rzmax;
    public SphericalJoint(String name, Double tx, Double ty, Double tz, Double rxmin, Double rxmax, Double rymin, Double rymax, Double rzmin, Double rzmax) {
        super(name);
        dofs.add(rx = new DoubleParameter("x", (rxmin+rxmax)/2, rxmin, rxmax));
        dofs.add(ry = new DoubleParameter("y", (rymin+rymax)/2, rymin, rymax));
        dofs.add(rz = new DoubleParameter("z", (rzmin+rzmax)/2, rzmin, rzmax));
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }
    @Override
    public void display(GLAutoDrawable drawable, ShadowPipeline pipeline) {
        pipeline.push();
        pipeline.translate(drawable, tx, ty, tz);
        pipeline.rotate(drawable, Math.toRadians(rx.getValue()), 1, 0, 0);
        pipeline.rotate(drawable, Math.toRadians(ry.getValue()), 0, 1, 0);
        pipeline.rotate(drawable, Math.toRadians(rz.getValue()), 0, 0, 1);
        super.display(drawable, pipeline);
        pipeline.pop(drawable);
    }
}
