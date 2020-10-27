package comp557.a2.chara;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a2.ShadowPipeline;
import mintools.parameters.DoubleParameter;

public class FreeJoint extends GraphNode {

	DoubleParameter tx;
	DoubleParameter ty;
	DoubleParameter tz;
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
		
	public FreeJoint( String name ) {
		super(name);
		dofs.add( tx = new DoubleParameter( name+" tx", 0, -2, 2 ) );		
		dofs.add( ty = new DoubleParameter( name+" ty", 0, -2, 2 ) );
		dofs.add( tz = new DoubleParameter( name+" tz", 0, -2, 2 ) );
		dofs.add( rx = new DoubleParameter( name+" rx", 0, -180, 180 ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, -180, 180 ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, -180, 180 ) );
	}
	
	@Override
	public void display( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
		pipeline.push();
		
		// TODO: Objective 3: Freejoint, transformations must be applied before drawing children
		pipeline.translate(drawable, tx.getValue(), ty.getValue(), tz.getValue());
		pipeline.rotate(drawable, Math.toRadians(rx.getValue()), 1, 0, 0);
		pipeline.rotate(drawable, Math.toRadians(ry.getValue()), 0, 1, 0);
		pipeline.rotate(drawable, Math.toRadians(rz.getValue()), 0, 0, 1);
		super.display( drawable, pipeline );		
		pipeline.pop(drawable);
	}
	
}
