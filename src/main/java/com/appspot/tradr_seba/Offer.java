package com.appspot.tradr_seba;

import com.google.appengine.api.datastore.*;  
import java.util.List;   
import javax.servlet.http.HttpServletRequest;
import scala.collection.immutable.*; 
import twirl.api.Html;    

public class Offer {

    public static String add(HttpServletRequest request) throws EntityNotFoundException {
        Entity offer = new Entity("Offer");
        Entity req = Application.datastore.get(KeyFactory.createKey("Item", Long.parseLong(request.getParameter("current_item"))));

        offer.setProperty("requested_item", request.getParameter("current_item"));
        offer.setProperty("requested_name", req.getProperty("title").toString());
        offer.setProperty("offered_item", -1);
        offer.setProperty("offered_name", "Nothing");
        offer.setProperty("send", false);
        offer.setProperty("accepted", false);

        Application.datastore.put(offer);
        return "/offer/" + Long.toString(offer.getKey().getId());
   }

    public static String all() {
        Query query = new Query("Offer");
        FetchOptions options = FetchOptions.Builder.withLimit(25);
        List<com.google.appengine.api.datastore.Entity> entities = Application.datastore.prepare(query).asList(options);

        scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> offers = Application.scalaList(entities);
        return html.offers.render(offers).toString();
    }

    public static String get(long id) throws EntityNotFoundException {
        Entity offer = Application.datastore.get(KeyFactory.createKey("Offer", id));
        Entity req = Application.datastore.get(KeyFactory.createKey("Item", Long.parseLong(offer.getProperty("requested_item").toString())));
       
        Entity offered;
        try {
            offered = Application.datastore.get(KeyFactory.createKey("Item", Long.parseLong(offer.getProperty("offered_item").toString())));
        } catch (Exception e) {
            offered = Item.empty();
        }

        Query query = new Query("Item");                                       
        FetchOptions options = FetchOptions.Builder.withLimit(25);
        List<com.google.appengine.api.datastore.Entity> entities = Application.datastore.prepare(query).asList(options);

        scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> items = Application.scalaList(entities);

        return html.offer.render(offer, req, offered, items).toString();
    }

    private static void delete(long id) {
        try {
            Application.datastore.delete(KeyFactory.createKey("Offer", id)); 
        } catch (Exception e) {
            // Die gracefully
        }
    }

    public static String update(HttpServletRequest request) throws EntityNotFoundException {
        if (request.getParameter("delete") != null) {
            delete(Long.parseLong(request.getParameter("current_offer")));
            return "/offers";
        }
        
        Entity offer = Application.datastore.get(KeyFactory.createKey("Offer", Long.parseLong(request.getParameter("current_offer"))));

        Entity offered;
        try {
            offered = Application.datastore.get(KeyFactory.createKey("Item", Long.parseLong(request.getParameter("offered_item"))));
        } catch (Exception e) {
            offered = Item.empty();
        }
        offer.setProperty("offered_item", offered.getKey().getId());
        offer.setProperty("offered_name", offered.getProperty("title"));
        offer.setProperty("send", true);
        offer.setProperty("accepted", false);

        Application.datastore.put(offer);
        return "/offer/" + Long.toString(offer.getKey().getId());
    }
}
