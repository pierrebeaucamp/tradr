package com.appspot.tradr_seba;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.images.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import scala.collection.immutable.*;
import scala.collection.JavaConverters.*;
import twirl.api.Html;


public class Application {
    
    private static BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static ImagesService images = ImagesServiceFactory.getImagesService();

    public static String index() {
        Query query = new Query("Item");
        
        //query.addSort("date_entered",Query.SortDirection.ASCENDING);
        
        FetchOptions options = FetchOptions.Builder.withLimit(25);
        List<com.google.appengine.api.datastore.Entity> entities = datastore.prepare(query).asList(options);
        
        scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> items = scalaList(entities);
        return html.index.render(items).toString();
    }

    private static <T> scala.collection.immutable.List<T> scalaList(List<T> javaList) {
        return scala.collection.JavaConversions.asScalaIterable(javaList).toList();
    }

    public static String getItem(long id) throws EntityNotFoundException {
        Entity item = datastore.get(KeyFactory.createKey("Item", id));

        String title = item.getProperty("title").toString();
        String img_url = item.getProperty("img_url").toString();
		String condition = item.getProperty("condition").toString();
		String age = item.getProperty("age").toString();
		String description = item.getProperty("description").toString();
		
        return html.item.render(title, img_url, condition, age, description).toString();
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
        } else {
            Entity item = new Entity("Item");
            ServingUrlOptions imageOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));                                                
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
			item.setProperty("description", request.getParameter("description"));
            item.setProperty("img_url", images.getServingUrl(imageOptions));

            datastore.put(item);
            return "/item/" + Long.toString(item.getKey().getId());
        }
    }
}
