package com.appspot.tradr_seba;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.images.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import scala.collection.immutable.*; 
import javax.servlet.http.HttpServletRequest;
import twirl.api.Html;


public class Item {
    
    public static String add(HttpServletRequest request) {
        Map<String, List<BlobKey>> blobs = Application.blobstore.getUploads(request);
        List<BlobKey> blobKeys = blobs.get("image");

        if (blobKeys == null || blobKeys.isEmpty()) {
            return "/";
        }

        Entity item = new Entity("Item");
        ServingUrlOptions imageOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0)).secureUrl(true); 

        Date dateEntered = new Date();
        String ageValue = request.getParameter("age-value");
        String ageUnit = request.getParameter("age-unit");
        String age = ageValue + " " + ageUnit;
        
        if (ageValue.equals("")) {
            age = "";
        }

        
        item.setProperty("title", request.getParameter("title"));
        item.setProperty("condition", request.getParameter("condition"));
        item.setProperty("age", age);
        item.setProperty("date_entered", dateEntered);
        item.setProperty("purpose", request.getParameter("purpose"));
        item.setProperty("description", request.getParameter("description"));
        item.setProperty("img_url", Application.images.getServingUrl(imageOptions));
        item.setProperty("location", request.getParameter("location"));

        Key itemKey = Application.datastore.put(item);

        String tags = request.getParameter("tags");
        storeTags(Long.toString(item.getKey().getId()),tags);
        //storeTags(itemKey.toString(),tags);

        return "/item/" + Long.toString(item.getKey().getId());
    }

    public static Entity empty() {
        Entity item = new Entity("Item");
        item.setProperty("title", "Nothing");
        item.setProperty("condition", "");
        item.setProperty("age", "");
        item.setProperty("img_url", "/images/icons/placeholder--small.png");
        return item;
    }

    public static String form() {
        String url = Application.blobstore.createUploadUrl("/submit/item");
        return html.upload.render(url).toString();
    }

    public static String get(long id) throws EntityNotFoundException {
        Entity item = Application.datastore.get(KeyFactory.createKey("Item", id));
        System.out.println("Item ID:"+id);
        return html.item.render(item).toString();
    }

     public static String searchTag (HttpServletRequest request) throws EntityNotFoundException {
        String tag = request.getParameter("tag");
        System.out.println("SearchTag:" + tag);
        // query all tag entities with name = tag
        Filter tagFilter = new FilterPredicate("name",
                      FilterOperator.EQUAL,
                      tag);
        Query query = new Query("Tag");
        query.setFilter(tagFilter);
        FetchOptions options = FetchOptions.Builder.withLimit(25);
        List<com.google.appengine.api.datastore.Entity> entities = Application.datastore.prepare(query).asList(options);
        List<com.google.appengine.api.datastore.Entity> items = new ArrayList<com.google.appengine.api.datastore.Entity>();
        // look for items with that tag
        for (com.google.appengine.api.datastore.Entity fetchedTag : entities){
                String idStr = fetchedTag.getProperty("item_idname").toString();
                Long id = Long.valueOf(idStr).longValue();
                System.out.println("Item ID:"+id);
                com.google.appengine.api.datastore.Entity item = null;
		try {
                        item = Application.datastore.get(KeyFactory.createKey("Item",id));
                }catch (Exception e){
                        System.out.println("Tag:" + fetchedTag.getProperty("name").toString() + " - NO ITEM");                                
                }
        	System.out.println("Item found:"+item.getProperty("title").toString());
        	items.add(item);
	}

        System.out.println("Finished SearchTag:"+items.size());
        scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> scalaItems = Application.scalaList(items);	
        return html.search.render(scalaItems).toString();

   }

    private static void storeTags(String id, String tags) {
        String[] splitedTags = tags.split("\\s+");
        for(String strTag: splitedTags){
	    //if ((strTag != "") || (strTag != " ")){
            	Entity tag = new Entity("Tag");
            	tag.setProperty("name",strTag);
            	tag.setProperty("item_idname",id);  
            	Application.datastore.put(tag);
            //}	
	}
    }
}
