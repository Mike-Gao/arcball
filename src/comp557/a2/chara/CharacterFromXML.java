package comp557.a2.chara;
 		  	  				   
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import javax.vecmath.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Loads an articulated character hierarchy from an XML file.
 */
public class CharacterFromXML {

	public static GraphNode load( String filename ) {
		try {
			InputStream inputStream = new FileInputStream(new File(filename));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			return createScene( null, document.getDocumentElement() ); // we don't check the name of the document elemnet
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(filename);
			throw new RuntimeException("Failed to load simulation input file.", e);
		}
	}
	
	/**
	 * Load a subtree from a XML node.
	 * Returns the root on the call where the parent is null, but otherwise
	 * all children are added as they are created and all other deeper recursive
	 * calls will return null.
	 */
	public static GraphNode createScene( GraphNode parent, Node dataNode ) {
        NodeList nodeList = dataNode.getChildNodes();
        for ( int i = 0; i < nodeList.getLength(); i++ ) {
            Node n = nodeList.item(i);
            // skip all text, just process the ELEMENT_NODEs
            if ( n.getNodeType() != Node.ELEMENT_NODE ) continue;
            String nodeName = n.getNodeName();
            GraphNode node = null;
            if ( nodeName.equalsIgnoreCase( "node" ) ) {
            	node = CharacterFromXML.createJoint( n );
            } else if ( nodeName.equalsIgnoreCase( "geom" ) ) {        		
        		node = CharacterFromXML.createGeom( n ) ;            
            } else {
            	System.err.println("Unknown node " + nodeName );
            	continue;
            }
            if ( node == null ) continue;
            // recurse to load any children of this node
            createScene( node, n );
            if ( parent == null ) {
            	// if no parent, we can only have one root... ignore other nodes at root level
            	return node;
            } else {
            	parent.add( node );
            }
        }
        return null;
	}
	
	/**​‌​​​‌‌​​​‌‌​​​‌​​‌‌‌​​‌
	 * Create a joint
	 * 
	 * TODO: Objective 8: XML, Adapt commented code in createJoint() to create your joint nodes when loading from xml
	 */
	public static GraphNode createJoint( Node dataNode ) {
		String type = dataNode.getAttributes().getNamedItem("type").getNodeValue();
		String name = dataNode.getAttributes().getNamedItem("name").getNodeValue();
		Tuple3d pos, axis;
		Tuple2d xRange,yRange,zRange;
		Tuple2d angRange;
		if ( type.equals("freejoint") ) {
			FreeJoint joint = new FreeJoint( name );
			return joint;
		} else if ( type.equals("spherical") ) {
			// position is optional (ignored if missing) but should probably be a required attribute!​‌​​​‌‌​​​‌‌​​​‌​​‌‌‌​​‌
			// Could add optional attributes for limits (to all joints)
			pos = getTuple3dAttr(dataNode, "position");
			if (pos == null) {
				pos = new Vector3d(0,0,0);
			}
			xRange = getTuple2dAttr(dataNode,"xrange");
			if (xRange == null) {
				xRange = new Vector2d(0,180);
			}

			yRange = getTuple2dAttr(dataNode,"yrange");
			if (yRange == null) {
				yRange = new Vector2d(0,180);
			}
			zRange = getTuple2dAttr(dataNode,"zrange");
			if (zRange == null) {
				zRange = new Vector2d(0,180);
			}
			SphericalJoint jnt = new SphericalJoint(name, pos.x, pos.y, pos.z, xRange.x, xRange.y, yRange.x, yRange.y, zRange.x, zRange.y);
			return jnt;
		} else if ( type.equals("rotary") ) {
			// position and axis are required... passing null to set methods
			// likely to cause an execption (perhaps OK)
			pos = getTuple3dAttr(dataNode, "position");
			if (pos == null) {
				pos = new Vector3d(0,0,0);
			}
			axis = getTuple3dAttr(dataNode, "axis");
			if (axis == null) {
				axis = new Vector3d(0,0,1);
			}
			angRange = getTuple2dAttr(dataNode, "range");
			if (angRange == null) {
				angRange = new Vector2d(-180,180);
			}
			RotaryJoint jnt = new RotaryJoint(name, pos.x, pos.y, pos.z, axis.x, axis.y, axis.z, angRange.x, angRange.y);
			return jnt;
		} else {
			System.err.println("Unknown type " + type );
		}
		return null;
	}

	/**
	 * Creates a geometry DAG node 
	 * 
	 * TODO: Objective 5: Adapt commented code in greatGeom to create your geometry nodes when loading from xml
	 */
	public static GraphNode createGeom( Node dataNode ) {
		String type = dataNode.getAttributes().getNamedItem("type").getNodeValue();
		String name = dataNode.getAttributes().getNamedItem("name").getNodeValue();
		Tuple3d t, scale, rotation;
		Tuple3f color;
		float shininess = 1;
		if ( type != null) {
			t = getTuple3dAttr(dataNode, "center");
			if (t == null) {
				t = new Vector3d(0,0,0);
			}
			scale = getTuple3dAttr(dataNode, "scale");
			if (scale == null) {
				scale = new Vector3d(1,1,1);
			}
			color = getTuple3fAttr(dataNode, "color");
			if (color == null) {
				color = new Vector3f(1,1,1);
			}
			rotation = getTuple3dAttr(dataNode, "rotation");
			if (rotation == null) {
				rotation = new Vector3d(0,0,0);
			}
			Geometry geom = new Geometry(name, type, t, rotation, scale, color, shininess);
			return geom;
		} else {
			System.err.println("unknown type " + type );
		}
		return null;
	}

	/**
	 * Loads tuple3f attributes of the given name from the given node.
	 * @param dataNode
	 * @param attrName
	 * @return null if attribute not present
	 */
	public static Tuple3f getTuple3fAttr( Node dataNode, String attrName ) {
		Node attr = dataNode.getAttributes().getNamedItem( attrName);
		Vector3f tuple = null;
		if ( attr != null ) {
			Scanner s = new Scanner( attr.getNodeValue() );
			tuple = new Vector3f( s.nextFloat()/255, s.nextFloat()/255, s.nextFloat()/255 );
			s.close();
		}
		return tuple;
	}
	
	/**
	 * Loads tuple3d attributes of the given name from the given node.
	 * @param dataNode
	 * @param attrName
	 * @return null if attribute not present
	 */
	public static Tuple3d getTuple3dAttr( Node dataNode, String attrName ) {
		Node attr = dataNode.getAttributes().getNamedItem( attrName);
		Vector3d tuple = null;
		if ( attr != null ) {
			Scanner s = new Scanner( attr.getNodeValue() );
			tuple = new Vector3d( s.nextDouble(), s.nextDouble(), s.nextDouble() );
			s.close();
		}
		return tuple;
	}

	/**
	 * Loads tuple2d attributes of the given name from the given node.
	 * @param dataNode
	 * @param attrName
	 * @return null if attribute not present
	 */
	public static Tuple2d getTuple2dAttr( Node dataNode, String attrName ) {
		Node attr = dataNode.getAttributes().getNamedItem( attrName);
		Tuple2d tuple = null;
		if ( attr != null ) {
			Scanner s = new Scanner( attr.getNodeValue() );
			tuple = new Vector2d( s.nextDouble(), s.nextDouble());
			s.close();
		}
		return tuple;
	}

}