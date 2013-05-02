package projector.rendering;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.Environment;
import android.util.Log;

public class AnimationFactory {
	
	public static final int X_LOC = 0;
	public static final int Y_LOC = 1;
	public static final int Z_LOC = 2;
	public static final int X_LOC_IN = 3;
	public static final int Y_LOC_IN = 4;
	public static final int Z_LOC_IN = 5;
	public static final int X_LOC_OUT = 6;
	public static final int Y_LOC_OUT = 7;
	public static final int Z_LOC_OUT = 8;
	public static final int X_LOC_INTAN = 9;
	public static final int Y_LOC_INTAN = 10;
	public static final int Z_LOC_INTAN = 11;
	public static final int X_LOC_OUTTAN = 12;
	public static final int Y_LOC_OUTTAN = 13;
	public static final int Z_LOC_OUTTAN = 14;
	
	public static final int X_ROT = 15;
	public static final int Y_ROT = 16;
	public static final int Z_ROT = 17;
	public static final int X_ROT_IN = 18;
	public static final int Y_ROT_IN = 19;
	public static final int Z_ROT_IN = 20;
	public static final int X_ROT_OUT = 21;
	public static final int Y_ROT_OUT = 22;
	public static final int Z_ROT_OUT = 23;
	public static final int X_ROT_INTAN = 24;
	public static final int Y_ROT_INTAN = 25;
	public static final int Z_ROT_INTAN = 26;
	public static final int X_ROT_OUTTAN = 27;
	public static final int Y_ROT_OUTTAN = 28;
	public static final int Z_ROT_OUTTAN = 29;
	
	
	String dir;
	Animation newAni;
	
	public AnimationFactory(String dir){
		this.dir = dir;
	}
	
	public Animation loadAnimation(String fileName) throws ParserConfigurationException, IOException, SAXException{

			newAni = new Animation();
		
			//File is .dae file (COLLADA)
			if(fileName.contains(".dae")){
				
				DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
				fac.setNamespaceAware(false);
				fac.setValidating(false);
				fac.setFeature("http://xml.org/sax/features/namespaces", false);
				fac.setFeature("http://xml.org/sax/features/validation", false);
				DocumentBuilder builder = fac.newDocumentBuilder();
				File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dir + "/" + fileName);
				String test = Environment.getExternalStorageDirectory().getAbsolutePath() + dir + "/" + fileName;
				Document doc = builder.parse(myFile);
				NodeList nl = doc.getElementsByTagName("animation");
					for(int i = 0; i < nl.getLength(); i++){
						Element el = (Element) nl.item(i);
						if(el.hasAttribute("id")){
							if(el.getAttribute("id").contains("location_X")){
								newAni.hasLocation = true;
								parseLocation(el, X_LOC);
							}
							if(el.getAttribute("id").contains("location_Y"))
								parseLocation(el, Y_LOC);
							if(el.getAttribute("id").contains("location_Z"))
								parseLocation(el, Z_LOC);
							if(el.getAttribute("id").contains("euler_X")){
								newAni.hasRotation = true;
								parseRotation(el, X_ROT);
							}
							if(el.getAttribute("id").contains("euler_Y"))
								parseRotation(el, Y_ROT);
							if(el.getAttribute("id").contains("euler_Z"))
								parseRotation(el, Z_ROT);
						}
					}
			}
		
		return newAni;
	}
	
	public void parseLocation(Element el, int type){
		NodeList data = el.getChildNodes();
		for(int i = 0; i < data.getLength(); i++){
				Node partial = data.item(i);
				if(partial.getNodeType() == Node.ELEMENT_NODE){
					Element source = (Element) partial;
					if(source.getAttribute("id").contains("input")){
						
						Element values = (Element) source.getElementsByTagName("float_array").item(0);
						String sCount = values.getAttribute("count");
						int count = Integer.parseInt(sCount);
						
						String floatString = values.getFirstChild().getNodeValue();
						
						if(type == X_LOC){
							newAni.frames = count;
							addToDataStructure(X_LOC_IN, count, floatString);
						}
						else if(type == Y_LOC)
							addToDataStructure(Y_LOC_IN, count, floatString);
						else if(type == Z_LOC)
							addToDataStructure(Z_LOC_IN, count, floatString);
					}
					if(source.getAttribute("id").contains("output")){
						
						Element values = (Element) source.getElementsByTagName("float_array").item(0);
						String sCount = values.getAttribute("count");
						int count = Integer.parseInt(sCount);
						
						String floatString = values.getFirstChild().getNodeValue();
						
						if(type == X_LOC)
							addToDataStructure(X_LOC_OUT, count, floatString);
						else if(type == Y_LOC)
							addToDataStructure(Y_LOC_OUT, count, floatString);
						else if(type == Z_LOC)
							addToDataStructure(Z_LOC_OUT, count, floatString);
					}
					if(source.getAttribute("id").contains("intangent")){
						
						Element values = (Element) source.getElementsByTagName("float_array").item(0);
						String sCount = values.getAttribute("count");
						int count = Integer.parseInt(sCount);
						
						String floatString = values.getFirstChild().getNodeValue();
						
						if(type == X_LOC)
							addToDataStructure(X_LOC_INTAN, count, floatString);
						else if(type == Y_LOC)
							addToDataStructure(Y_LOC_INTAN, count, floatString);
						else if(type == Z_LOC)
							addToDataStructure(Z_LOC_INTAN, count, floatString);
					}
					if(source.getAttribute("id").contains("outtangent")){
						
						Element values = (Element) source.getElementsByTagName("float_array").item(0);
						String sCount = values.getAttribute("count");
						int count = Integer.parseInt(sCount);
						
						String floatString = values.getFirstChild().getNodeValue();
						
						if(type == X_LOC)
							addToDataStructure(X_LOC_OUTTAN, count, floatString);
						else if(type == Y_LOC)
							addToDataStructure(Y_LOC_OUTTAN, count, floatString);
						else if(type == Z_LOC)
							addToDataStructure(Z_LOC_OUTTAN, count, floatString);
					}
				}
				

		}
	}
	
	public void parseRotation(Element el, int type){
		NodeList data = el.getChildNodes();
		for(int i = 0; i < data.getLength(); i++){
				Node partial = data.item(i);
				if(partial.getNodeType() == Node.ELEMENT_NODE){
					Element source = (Element) partial;
					if(source.getAttribute("id").contains("input")){
						
						Element values = (Element) source.getElementsByTagName("float_array").item(0);
						String sCount = values.getAttribute("count");
						int count = Integer.parseInt(sCount);
						
						String floatString = values.getFirstChild().getNodeValue();
						
						if(type == X_ROT)
							addToDataStructure(X_ROT_IN, count, floatString);
						else if(type == Y_ROT)
							addToDataStructure(Y_ROT_IN, count, floatString);
						else if(type == Z_ROT)
							addToDataStructure(Z_ROT_IN, count, floatString);
					}
					if(source.getAttribute("id").contains("output")){
						
						Element values = (Element) source.getElementsByTagName("float_array").item(0);
						String sCount = values.getAttribute("count");
						int count = Integer.parseInt(sCount);
						
						String floatString = values.getFirstChild().getNodeValue();
						
						if(type == X_ROT)
							addToDataStructure(X_ROT_OUT, count, floatString);
						else if(type == Y_ROT)
							addToDataStructure(Y_ROT_OUT, count, floatString);
						else if(type == Z_ROT)
							addToDataStructure(Z_ROT_OUT, count, floatString);
					}
					if(source.getAttribute("id").contains("intangent")){
						
						Element values = (Element) source.getElementsByTagName("float_array").item(0);
						String sCount = values.getAttribute("count");
						int count = Integer.parseInt(sCount);
						
						String floatString = values.getFirstChild().getNodeValue();
						
						if(type == X_ROT)
							addToDataStructure(X_ROT_INTAN, count, floatString);
						else if(type == Y_ROT)
							addToDataStructure(Y_ROT_INTAN, count, floatString);
						else if(type == Z_ROT)
							addToDataStructure(Z_ROT_INTAN, count, floatString);
					}
					if(source.getAttribute("id").contains("outtangent")){
						
						Element values = (Element) source.getElementsByTagName("float_array").item(0);
						String sCount = values.getAttribute("count");
						int count = Integer.parseInt(sCount);
						
						String floatString = values.getFirstChild().getNodeValue();
						
						if(type == X_ROT)
							addToDataStructure(X_ROT_OUTTAN, count, floatString);
						else if(type == Y_ROT)
							addToDataStructure(Y_ROT_OUTTAN, count, floatString);
						else if(type == Z_ROT)
							addToDataStructure(Z_ROT_OUTTAN, count, floatString);
					}
				}
				

		}
	}
	
	public void addToDataStructure(int type, int num, String values){
		if(type == X_LOC_IN){
			String[] daValues = values.split("\\s+");
			newAni.xLocIn = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.xLocIn[i++] = Float.parseFloat(s);
			}
		}
		if(type == X_LOC_OUT){
			String[] daValues = values.split("\\s+");
			newAni.xLocOut = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.xLocOut[i++] = Float.parseFloat(s);
			}
		}
		if(type == X_LOC_INTAN){
			String[] daValues = values.split("\\s+");
			newAni.xLocIntan = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.xLocIntan[i++] = Float.parseFloat(s);
			}
		}
		if(type == X_LOC_OUTTAN){
			String[] daValues = values.split("\\s+");
			newAni.xLocOuttan = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.xLocOuttan[i++] = Float.parseFloat(s);
			}
		}
		if(type == Y_LOC_IN){
			String[] daValues = values.split("\\s+");
			newAni.yLocIn = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.yLocIn[i++] = Float.parseFloat(s);
			}
		}
		if(type == Y_LOC_OUT){
			String[] daValues = values.split("\\s+");
			newAni.yLocOut = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.yLocOut[i++] = Float.parseFloat(s);
			}
		}
		if(type == Y_LOC_INTAN){
			String[] daValues = values.split("\\s+");
			newAni.yLocIntan = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.yLocIntan[i++] = Float.parseFloat(s);
			}
		}
		if(type == Y_LOC_OUTTAN){
			String[] daValues = values.split("\\s+");
			newAni.yLocOuttan = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.yLocOuttan[i++] = Float.parseFloat(s);
			}
		}
		if(type == Z_LOC_IN){
			String[] daValues = values.split("\\s+");
			newAni.zLocIn = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.zLocIn[i++] = Float.parseFloat(s);
			}
		}
		if(type == Z_LOC_OUT){
			String[] daValues = values.split("\\s+");
			newAni.zLocOut = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.zLocOut[i++] = Float.parseFloat(s);
			}
		}
		if(type == Z_LOC_INTAN){
			String[] daValues = values.split("\\s+");
			newAni.zLocIntan = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.zLocIntan[i++] = Float.parseFloat(s);
			}
		}
		if(type == Z_LOC_OUTTAN){
			String[] daValues = values.split("\\s+");
			newAni.zLocOuttan = new float[num];
			int i = 0;
			for(String s: daValues){
				newAni.zLocOuttan[i++] = Float.parseFloat(s);
			}
		}
	if(type == X_ROT_IN){
		String[] daValues = values.split("\\s+");
		newAni.xRotIn = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.xRotIn[i++] = Float.parseFloat(s);
		}
	}
	if(type == X_ROT_OUT){
		String[] daValues = values.split("\\s+");
		newAni.xRotOut = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.xRotOut[i++] = Float.parseFloat(s);
		}
	}
	if(type == X_ROT_INTAN){
		String[] daValues = values.split("\\s+");
		newAni.xRotIntan = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.xRotIntan[i++] = Float.parseFloat(s);
		}
	}
	if(type == X_ROT_OUTTAN){
		String[] daValues = values.split("\\s+");
		newAni.xRotOuttan = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.xRotOuttan[i++] = Float.parseFloat(s);
		}
	}
	if(type == Y_ROT_IN){
		String[] daValues = values.split("\\s+");
		newAni.yRotIn = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.yRotIn[i++] = Float.parseFloat(s);
		}
	}
	if(type == Y_ROT_OUT){
		String[] daValues = values.split("\\s+");
		newAni.yRotOut = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.yRotOut[i++] = Float.parseFloat(s);
		}
	}
	if(type == Y_ROT_INTAN){
		String[] daValues = values.split("\\s+");
		newAni.yRotIntan = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.yRotIntan[i++] = Float.parseFloat(s);
		}
	}
	if(type == Y_ROT_OUTTAN){
		String[] daValues = values.split("\\s+");
		newAni.yRotOuttan = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.yRotOuttan[i++] = Float.parseFloat(s);
		}
	}
	if(type == Z_ROT_IN){
		String[] daValues = values.split("\\s+");
		newAni.zRotIn = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.zRotIn[i++] = Float.parseFloat(s);
		}
	}
	if(type == Z_ROT_OUT){
		String[] daValues = values.split("\\s+");
		newAni.zRotOut = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.zRotOut[i++] = Float.parseFloat(s);
		}
	}
	if(type == Z_ROT_INTAN){
		String[] daValues = values.split("\\s+");
		newAni.zRotIntan = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.zRotIntan[i++] = Float.parseFloat(s);
		}
	}
	if(type == Z_ROT_OUTTAN){
		String[] daValues = values.split("\\s+");
		newAni.zRotOuttan = new float[num];
		int i = 0;
		for(String s: daValues){
			newAni.zRotOuttan[i++] = Float.parseFloat(s);
		}
	}
}

	
}
