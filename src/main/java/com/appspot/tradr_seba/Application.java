package com.appspot.tradr_seba;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.images.*;
import java.util.List;
import java.util.Map;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import twirl.api.Html;
import scala.collection.immutable.*;
import scala.collection.JavaConverters.*;

public class Application {
    
    private static BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static ImagesService images = ImagesServiceFactory.getImagesService();

    public static String index() {

	//Filter allTheTitles = new FilterPredicate("title",FilterOperator.EQUAL,"stupid");
	//Query query = new Query("title").setFilter(allTheTitles);
	Query query = new Query("title");
	//query.addSort("date_entered",Query.SortDirection.ASCENDING);
	//FetchOptions options = FetchOptions.Builder.withLimit(25);

	//List<com.google.appengine.api.datastore.Entity> entities = datastore.prepare(query).asList(options);
	//scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> items = scalaList(entities);
        
	// Use PreparedQuery interface to retrieve results
	PreparedQuery pq = datastore.prepare(query);

	String title = "";
	int num = pq.countEntities();
	for (Entity result : pq.asIterable()) {
  		title = (String) result.getProperty("title");
		System.out.println("Entrada con titulo"+title);
	}
	return html.index.render(num).toString();
        //return html.index.render().toString();
    }

    public static <T> scala.collection.immutable.List<T> scalaList(List<T> javaList) {
        return scala.collection.JavaConversions.asScalaIterable(javaList).toList();
    }

    public static String getItem(long id) throws EntityNotFoundException {
        Entity item = datastore.get(KeyFactory.createKey("Item", id));

        String title = item.getProperty("title").toString();
        String img_url = item.getProperty("img_url").toString();

        return html.item.render(title, img_url).toString();
    }

    public static String upload() {
        String url = blobstore.createUploadUrl("/submit");
        return html.upload.render(url).toString();
    }

    public static String addItem(HttpServletRequest request) {
        Map<String, List<BlobKey>> blobs = blobstore.getUploads(request);
        List<BlobKey> blobKeys = blobs.get("image");
	Date currentDate = new Date();
        if (blobKeys == null || blobKeys.isEmpty()) {
            return "/";
        } else {
            Entity item = new Entity("Item");
            ServingUrlOptions imageOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));                                                

            item.setProperty("title", request.getParameter("title"));
	    item.setProperty("date_entered",currentDate);
            item.setProperty("img_url", images.getServingUrl(imageOptions));

            datastore.put(item);
            return "/item/" + Long.toString(item.getKey().getId());
        }
    }
}
