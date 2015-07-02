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
    	return html.register.render().toString();
    }
    public static String change(){
    	return html.changepassword.render("initail").toString();
    }
    public static String findpasswordback(){
    	return html.findpassword.render().toString();
    }
    public static String user(){
    	return html.user.render("not").toString();
    }

    public static String getUser(HttpServletRequest request) throws EntityNotFoundException {
    	
	String loginusername = request.getParameter("LoginUserName").toString();
	String loginpassword = request.getParameter("LoginPassword").toString();
    Entity user = datastore.get(KeyFactory.createKey("User", loginusername));

    String username = user.getProperty("username").toString();
    String password = user.getProperty("password").toString();
    
    if(loginpassword.equals(password)){
       // HttpSession session = request.getSession();
       // session.setAttribute("Uname", username);
        return html.user.render("yes").toString();
    }

    else {
    	return html.user.render("not").toString();
    }
       
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
        
        if (!username.substring(0,1).matches("[a-zA-Z]"))
           return ("fail");// JOptionPane.showMessageDialog(frame,"fail"); //return("fail");
        if (password.length()>12||password.length()<6)
        	return ("also fail");
     /*   isValidEmailAddress Em = new isValidEmailAddress(email);
        if(!Em)
        	return("fail");*/
                
        user.setProperty("username", username);
        user.setProperty("sex", request.getParameter("sex"));
        user.setProperty("date_entered", dateEntered);
        user.setProperty("password", password);
        user.setProperty("email", email);
        user.setProperty("phonenumber", request.getParameter("PhoneNo"));
       // user.setProperty("img_url", images.getServingUrl(imageOptions));

        datastore.put(user);
        String welcome = "Welcome!";
        String newaccount = "You have successfully created a new account!";
        String enjoy = "Enjoy your tradr!";
        return html.afterregister.render(welcome,newaccount,enjoy).toString(); 
    }
 /*  public boolean isValidEmailAddress(String email){
	   String ePattern ="^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
	   jave.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
	   java.util.regexMatcher m = p.matcher(email);
	    return m.matches();
   }*/
   
   public static String showuser () {
		Query query = new Query("User");
        FetchOptions options = FetchOptions.Builder.withLimit(25);
        List<com.google.appengine.api.datastore.Entity> entities = datastore.prepare(query).asList(options);
        
        scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> users = scala.collection.JavaConversions.asScalaIterable(entities).toList();
	    return html.alluser.render(users).toString();
   }
   
   public static String ChangePassword(HttpServletRequest request)  throws EntityNotFoundException {
	   	   
	  String usernameinchange = request.getParameter("usernameinchange").toString();
	  String oldpassword = request.getParameter("oldpassword").toString();
	  String newpassword = request.getParameter("newpassword").toString();
	  Entity user = datastore.get(KeyFactory.createKey("User", usernameinchange));
	  //try{ Entity user = datastore.get(KeyFactory.createKey("User", usernameinchange));}
	  //catch (EntityNotFoundException e) { return html.changepassword.render("userfail").toString(); }
	  String username = user.getProperty("username").toString();
	  String password = user.getProperty("password").toString();	

		  if (password.equals(newpassword)){			  
			  return html.changepassword.render("passwordfail").toString();
		  }
		  else{
			  user.setProperty("password",newpassword);
			  datastore.put(user);
			  return html.afterchange.render("","You have changed your password!","").toString();
		  }
   }
   
   public static String FindPassword(HttpServletRequest request) throws EntityNotFoundException{
	   
	   String usernameinback = request.getParameter("usernameinback").toString();
	   String emailinback = request.getParameter("emailinback").toString();	   
	   Entity user = datastore.get(KeyFactory.createKey("User", usernameinback));
	   String password = user.getProperty("password").toString();
	   return html.afterrequestpassword.render("","Your Password is "+password+" !","").toString();
   }
}
