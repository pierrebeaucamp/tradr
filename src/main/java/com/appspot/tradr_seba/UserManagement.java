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

public class UserManagement {
    
    private static BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static ImagesService images = ImagesServiceFactory.getImagesService();

    public static String index(){
    	return html.register.render("feng").toString();
    }

    public static String getUser(HttpServletRequest request) throws EntityNotFoundException {
    	
	String loginusername = request.getParameter("LoginUserName").toString();
	String loginpassword = request.getParameter("LoginPassword").toString();
    Entity user = datastore.get(KeyFactory.createKey("User", loginusername));

    String username = user.getProperty("username").toString();
    String password = user.getProperty("password").toString();
       	 
    if(loginpassword == loginusername)
    	
      return ("login successfully");
    
    //String img_url = user.getProperty("img_url").toString();
    //String sex = user.getProperty("sex").toString();
    //String password = user.getProperty("password").toString();

    else 
    	return (loginusername+".."+loginpassword+". "+username+". "+password);
       
}

    
    
   public static String addUser(HttpServletRequest request) {
       // Map<String, List<BlobKey>> blobs = blobstore.getUploads(request);
       // List<BlobKey> blobKeys = blobs.get("image");

       // if (blobKeys == null || blobKeys.isEmpty()) {
        //    return "/";
        //}
              
        Entity user = new Entity("User",request.getParameter("username").toString());
       // ServingUrlOptions imageOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0)).secureUrl(true); 

        Date dateEntered = new Date();
        
        String username = request.getParameter("username").toString();
        String password = request.getParameter("password").toString();
        String email = request.getParameter("email").toString();
        String phonenumer = request.getParameter("PhoneNo").toString();
        
        if (!username.substring(0,1).matches("[a-z]"))
        	return ("fail");
        if (password.length()>12||password.length()<6)
        	return ("also fail");
        
        
        user.setProperty("username", username);
        user.setProperty("sex", request.getParameter("sex"));
        user.setProperty("date_entered", dateEntered);
        user.setProperty("password", password);
        user.setProperty("email", email);
        user.setProperty("PhoneNo", phonenumer);
       // user.setProperty("img_url", images.getServingUrl(imageOptions));

        datastore.put(user);
        return "user created"; 
    }
   
   public static String showuser ()
   {
		Query query = new Query("User");
        FetchOptions options = FetchOptions.Builder.withLimit(25);
        List<com.google.appengine.api.datastore.Entity> entities = datastore.prepare(query).asList(options);
        
        scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> users = scala.collection.JavaConversions.asScalaIterable(entities).toList();
	    return html.alluser.render(users).toString();
   }
   
}