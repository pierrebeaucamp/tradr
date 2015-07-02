package com.appspot.tradr_seba;

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
        Query query = new Query("Item");
        FetchOptions options = FetchOptions.Builder.withLimit(25);
        List<com.google.appengine.api.datastore.Entity> entities = Application.datastore.prepare(query).asList(options);

        scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> items = scalaList(entities);
        return html.index.render(items).toString();
    }

    public static <T> scala.collection.immutable.List<T> scalaList(List<T> javaList) {
        return scala.collection.JavaConversions.asScalaIterable(javaList).toList();
    }
}
