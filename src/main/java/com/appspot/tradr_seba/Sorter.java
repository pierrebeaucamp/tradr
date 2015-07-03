package com.appspot.tradr_seba;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.images.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import scala.collection.immutable.*;
import scala.collection.JavaConverters.*;
import twirl.api.Html;

/**
*	This is a sorter class that sorts a List<com.google.appengine.api.datastore.Entity>
*   in increasing order, based on the distance from the current user position and returns this list.
*   This list is then passed to the index, that populates the grid, thus sorting the items the user
*	sees according to their proximity.
*/
public class Sorter {
	
    Position current_user_position;
    final Comparator<com.google.appengine.api.datastore.Entity> distanceComparator= new Comparator<com.google.appengine.api.datastore.Entity>() {
			@Override
			public int compare(com.google.appengine.api.datastore.Entity e1, com.google.appengine.api.datastore.Entity e2){
				return getDistance(extractPosition(e1.getProperty("location").toString()), current_user_position).compareTo(getDistance(extractPosition(e2.getProperty("location").toString()), current_user_position));
			}
		};
	
	public Sorter(String coordinates){
		this.current_user_position = extractPosition(coordinates);

	}
		
	static class Position {
		double lat;
		double lng;
	}
		
	private Position extractPosition(String coord){
		String [] pos = coord.replaceAll("\\(","").replaceAll("\\)", "").split(", ");
		Position position = new Position();
		position.lat = Double.parseDouble(pos[0]);
		position.lng = Double.parseDouble(pos[1]);
		return position;
	} 
	
	private  double getRad(double x){
		return x*Math.PI/180;
	}

	private  Double getDistance(Position p1, Position p2) {
		double R = 6378137;
		double dLat = getRad(p2.lat - p1.lat);
		double dLong = getRad(p2.lng - p1.lng);
		double a =  Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.cos(getRad(p1.lat)) * Math.cos(getRad(p2.lat)) *
				Math.sin(dLong / 2) * Math.sin(dLong / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R*c;
		return d;
	}
	
	public List<com.google.appengine.api.datastore.Entity> sortByDistance(List<com.google.appengine.api.datastore.Entity> entities){
		Collections.sort(entities, distanceComparator);
		return entities;
	}

}
