package comp557.a2.chara;

import javax.swing.JTextField;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import mintools.parameters.BooleanParameter;

public class CharacterMaker {

	static public String name = "/dev/null - Mike Gao AND 260915701";
	
	// TODO: Objective 8: change default of load from file to true once you start working with xml
	static BooleanParameter loadFromFile = new BooleanParameter( "Load from file (otherwise by procedure)", true );
	static JTextField baseFileName = new JTextField("data/character");
	
	/**
	 * Creates a character, either procedurally, or by loading from an xml file
	 * @return root node
	 */
	static public GraphNode create() {
		
		if ( loadFromFile.getValue() ) {
			// TODO: Objectives 6: create your character in the character.xml file 
			return CharacterFromXML.load( baseFileName.getText() + ".xml");
		} else {
			// TODO: Objective 3,4,5,6: test DAG nodes by creating a small DAG in the CharacterMaker.create() method 
						
			// Use this for testing, but ultimately it will be more interesting
			// to create your character with an xml description (see example).
			
			// Here we just return null, which will not be very interesting, so write
			// some code to create a test or partial character and return the root node.

			FreeJoint root = new FreeJoint("test");
			var rot = new RotaryJoint("RotaryJoint", 1.0, 1.0, -1.0, 0.0, 0.0, 0.1, 0.0, 180.0);
			root.add(rot);
			var sp = new SphericalJoint("SphericalJoint", 0.0, 0.0, -2.0, 0.0, 180.0, 0.0, 180.0, 0.0, 180.0);
			root.add(sp);
			var cube = new Geometry("Cube", "box", null, null, null, new Vector3f(0f, 0.5f, 1f), 1f);
			var sphere = new Geometry("Sphere", "sphere", new Vector3d(2.0, 2.0, 0.0), null, null, new Vector3f(0f, 0.5f, 1f), 1f);
			rot.add(cube);
			sp.add(sphere);
			return root;
		}
	}
}
