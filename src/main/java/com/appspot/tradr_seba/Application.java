package com.appspot.tradr_seba;

import com.google.appengine.api.blobstore.*;                                   
import com.google.appengine.api.datastore.*;                                   
import com.google.appengine.api.images.*; 
import java.util.List;
import scala.collection.immutable.*;                                           
import scala.collection.JavaConverters.*;
import twirl.api.Html;
//import Sorter;


public class Application {

    public static BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    public static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    public static ImagesService images = ImagesServiceFactory.getImagesService();

    public static String index() {
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

        return html.item.render(title, img_url, condition, age, purpose, description).toString();
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
        
        item.setProperty("title", request.getParameter("title"));
        item.setProperty("condition", request.getParameter("condition"));
        item.setProperty("age", age);
        item.setProperty("date_entered", dateEntered);
        item.setProperty("purpose", request.getParameter("purpose"));
        item.setProperty("description", request.getParameter("description"));
        item.setProperty("img_url", images.getServingUrl(imageOptions));
		item.setProperty("location", request.getParameter("location"));

        datastore.put(item);
        return "/item/" + Long.toString(item.getKey().getId());
    }

}
