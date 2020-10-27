package comp557.a2.chara;

import com.jogamp.opengl.GLAutoDrawable;
import comp557.a2.ShadowPipeline;
import mintools.parameters.DoubleParameter;

public class RotaryJoint extends GraphNode {
    Double rx, ry, rz, tx, ty, tz;
    DoubleParameter angle;
    public RotaryJoint(String name, Double tx, Double ty, Double tz, Double rx, Double ry, Double rz, Double angMin, Double angMax) {
        super(name);
        dofs.add(angle = new DoubleParameter("angle", (angMin+angMax)/2, angMin, angMax));
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }
    @Override
    public void display (GLAutoDrawable drawable, ShadowPipeline pipeline) {
        pipeline.push();
        pipeline.translate(drawable, tx,ty,tz);
        pipeline.rotate(drawable, Math.toRadians(angle.getValue()),rx,ry,rz);
        super.display(drawable,pipeline);
        pipeline.pop(drawable);
    }
}
