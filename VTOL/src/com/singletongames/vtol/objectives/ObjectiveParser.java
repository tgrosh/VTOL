package com.singletongames.vtol.objectives;
 
import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.scene.Scene;
import org.andengine.util.SAXUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
import com.badlogic.gdx.math.Vector2;
import com.singletongames.vtol.LanderScene;
import com.singletongames.vtol.Resources;
 
public class ObjectiveParser extends DefaultHandler
{	
        private final StringBuilder mStringBuilder = new StringBuilder();
        
        List<Objective> objectives = new ArrayList<Objective>();
 
        boolean chapterFound, levelFound;
		int chapterID;
		int levelID;
		LanderScene scene;
		IObjectiveListener objectiveListener;
		ObjectiveManager manager;
		
        public ObjectiveParser(ObjectiveManager manager, LanderScene scene, int chapterID, int levelID, IObjectiveListener objectiveListener)
        {
        	this.scene = scene;
        	this.chapterID = chapterID;
        	this.levelID = levelID;
        	this.objectiveListener = objectiveListener;
        	this.manager = manager;
        }
              
        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================
       
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
        	if(localName.equals("game")){
        		
        	}
        	else if(localName.equals("objectives")){
        		
        	}
        	else if(localName.equals("chapter")){
        		int id = SAXUtils.getIntAttributeOrThrow(attributes, "id");
        		if (id == chapterID){
        			chapterFound = true;
        		}
        		else{
        			chapterFound = false;
        		}
        	}
        	else if(localName.equals("level") && chapterFound){
        		int id = SAXUtils.getIntAttributeOrThrow(attributes, "id");
        		if (id == levelID){
        			levelFound = true;
        		}
        		else{
        			levelFound = false;
        		}
        	}
        	else if(localName.equals("objective"))
            {
        		if (levelFound && chapterFound){
	                int id = SAXUtils.getIntAttributeOrThrow(attributes, "id");
	                String description = SAXUtils.getAttributeOrThrow(attributes, "description");
	                String type = SAXUtils.getAttributeOrThrow(attributes, "type");
	                int prerequisiteID = SAXUtils.getIntAttribute(attributes, "prerequisiteId", -1);
	                boolean hidden = SAXUtils.getBooleanAttribute(attributes, "hidden", false);
	                
	                if (type.equals("DeliverCargoObjective")){
	                	int cargoId = SAXUtils.getIntAttributeOrThrow(attributes, "cargoId");
	                	int cargoDropId = SAXUtils.getIntAttributeOrThrow(attributes, "cargoDropId");
		                objectives.add(new DeliverCargoObjective(manager, scene, id, description, cargoId, cargoDropId, hidden, prerequisiteID, objectiveListener));
	                }
	                else if (type.equals("ZoneObjective")){
	                	int objectiveZoneID = SAXUtils.getIntAttributeOrThrow(attributes, "objectiveZoneID");
		                objectives.add(new ZoneObjective(manager, scene, id, objectiveZoneID, description, hidden, prerequisiteID, objectiveListener));
	                }
	                else if (type.equals("WaypointObjective")){
	                	int objectiveZoneID = SAXUtils.getIntAttributeOrThrow(attributes, "objectiveZoneID");
	                	ObjectiveZone zone = Resources.mCurrentLevel.getObjectiveZone(objectiveZoneID);
	                	if (zone != null){
		                	float centerX = Resources.mCurrentLevel.getObjectiveZone(objectiveZoneID).getX() + Resources.mCurrentLevel.getObjectiveZone(objectiveZoneID).getWidth()/2; 
		                	float centerY = Resources.mCurrentLevel.getObjectiveZone(objectiveZoneID).getY() + Resources.mCurrentLevel.getObjectiveZone(objectiveZoneID).getHeight()/2;
			                objectives.add(new WaypointObjective(manager, scene, id, objectiveZoneID, description, prerequisiteID, new Vector2(centerX, centerY) , objectiveListener));
	                	}
	                }
	                else if (type.equals("SafeReturnObjective")){
	                	objectives.add(new SafeReturnObjective(manager, scene, id, description, hidden, prerequisiteID, objectiveListener));
	                }
	                else if (type.equals("TakeoffObjective")){
	                	objectives.add(new TakeoffObjective(manager, scene, id, description, hidden, prerequisiteID, objectiveListener));
	                }
	                else if (type.equals("LandingPadObjective")){
	                	int landingPadId = SAXUtils.getIntAttributeOrThrow(attributes, "landingPadId");
	                	objectives.add(new LandingPadObjective(manager, scene, id, landingPadId, description, hidden, prerequisiteID, objectiveListener));
	                }
        		}
            }
            else
            {
                    throw new SAXException("Unexpected start tag: '" + localName + "'.");
            }
        }
       
        @Override
        public void characters(final char[] pCharacters, final int pStart, final int pLength) throws SAXException
        {
            this.mStringBuilder.append(pCharacters, pStart, pLength);
        }

		public List<Objective> getObjectives() {
			return objectives;
		}
       
}