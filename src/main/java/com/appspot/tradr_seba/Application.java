package com.appspot.tradr_seba;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.images.*;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import java.util.Date;
import com.google.appengine.api.blobstore.*;                                   
import com.google.appengine.api.datastore.*;                                   
import com.google.appengine.api.images.*; 
import java.util.List;
import scala.collection.immutable.*;                                           
import scala.collection.JavaConverters.*;
import twirl.api.Html;

public class Application {

    public static BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    public static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    public static ImagesService images = ImagesServiceFactory.getImagesService();

    public static String index() {
        //String url = "/searchTag";
        Query query = new Query("Item");
        FetchOptions options = FetchOptions.Builder.withLimit(25);
        List<com.google.appengine.api.datastore.Entity> entities = Application.datastore.prepare(query).asList(options);

        scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> items = Application.scalaList(entities);
        return html.index.render(items).toString();
    }

    public static <T> scala.collection.immutable.List<T> scalaList(List<T> javaList) {
        return scala.collection.JavaConversions.asScalaIterable(javaList).toList();
    }

   public static String getItem(long id) throws EntityNotFoundException {

        Entity item = datastore.get(KeyFactory.createKey("Item", id));

        String title = item.getProperty("title").toString();
        String img_url = item.getProperty("img_url").toString();
        String condition = item.getProperty("condition").toString();
        String age = item.getProperty("age").toString();
        String description = item.getProperty("description").toString();
        String purpose = item.getProperty("purpose").toString();
        //return html.item.render(title, img_url, condition, age, purpose, description).toString();
        return html.item.render(item).toString();
    } 

    public static String upload() {
        String url = blobstore.createUploadUrl("/submit");
        return html.upload.render(url).toString();
    }
    
    public static String addItem(HttpServletRequest request) {

        Map<String, List<BlobKey>> blobs = blobstore.getUploads(request);
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
	// Store into DB        
        item.setProperty("title", request.getParameter("title"));
        item.setProperty("condition", request.getParameter("condition"));
        item.setProperty("age", age);
        item.setProperty("date_entered", dateEntered);
        item.setProperty("purpose", request.getParameter("purpose"));
        item.setProperty("description", request.getParameter("description"));
        item.setProperty("img_url", images.getServingUrl(imageOptions));

        datastore.put(item);
	String tags = request.getParameter("tags");
	storeTags(Long.toString(item.getKey().getId()),tags);
	System.out.println("StoreTags:"+tags.toString());
	System.out.println("ItemId:"+Long.toString(item.getKey().getId()));

        return "/item/" + Long.toString(item.getKey().getId());
    } 

    public static void storeTags(String id, String tags) {
	System.out.println("StoreTags:"+tags);
	
	String[] splitedTags = tags.split("\\s+");
  	for(String strTag: splitedTags){
			System.out.println("Tag found[" + id + "]:" + strTag);
			Entity tag = new Entity("Tag");	
			tag.setProperty("name",strTag);
			tag.setProperty("itemId",id);
			datastore.put(tag);
	}
     }
   
     public static String searchTag (String tag) throws EntityNotFoundException {
        String url = "/searchTag";
	System.out.println("SearchTag:" + tag);
	String responseHTML;
	Filter tagFilter = new FilterPredicate("name",
                      FilterOperator.EQUAL,
                      "musica");
	Query query = new Query("Tag");
	query.setFilter(tagFilter);   
   
        FetchOptions options = FetchOptions.Builder.withLimit(25);
        List<com.google.appengine.api.datastore.Entity> entities = datastore.prepare(query).asList(options);
	scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> items = null;
	for (com.google.appengine.api.datastore.Entity fetchedTag : entities){
		String idStr = fetchedTag.getProperty("itemId").toString();
		Long id = Long.valueOf(idStr).longValue();
		try {
			System.out.println("Item ID:"+id);						
			Entity item = datastore.get(KeyFactory.createKey("Item",id));
			items.$colon$colon((com.google.appengine.api.datastore.Entity) item);
		}catch (Exception e){
			System.out.println("Tag:" + fetchedTag.getProperty("name").toString() + " - NO ITEM");						
		}
	} 
	//System.out.println("Finished SearchTag:"+items.size());						
        return html.search.render(items).toString();
	
   }

}
