package com.appspot.tradr_seba;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.images.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

        Application.datastore.put(item);
        return "/item/" + Long.toString(item.getKey().getId());
    }

    public static Entity empty() {
        Entity item = new Entity("Item");
        item.setProperty("title", "Nothing");
        item.setProperty("condition", "");
        item.setProperty("age", "");
        item.setProperty("img_url", "images/icons/placeholder--small.png");
        return item;
    }

    public static String form() {
        String url = Application.blobstore.createUploadUrl("/submit/item");
        return html.upload.render(url).toString();
    }

    public static String get(long id) throws EntityNotFoundException {
        Entity item = Application.datastore.get(KeyFactory.createKey("Item", id));
        return html.item.render(item).toString();
    } 
}
