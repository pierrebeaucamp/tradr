package com.appspot.tradr_seba;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.images.*;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import twirl.api.Html;

public class Application {
    
    private static BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static ImagesService images = ImagesServiceFactory.getImagesService();

    public static String index() {
        return html.index.render().toString();
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

        if (blobKeys == null || blobKeys.isEmpty()) {
            return "/";
        } else {
            Entity item = new Entity("Item");
            ServingUrlOptions imageOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));                                                

            item.setProperty("title", request.getParameter("title"));
            item.setProperty("img_url", images.getServingUrl(imageOptions));

            datastore.put(item);
            return "/item/" + Long.toString(item.getKey().getId());
        }
    }
}
