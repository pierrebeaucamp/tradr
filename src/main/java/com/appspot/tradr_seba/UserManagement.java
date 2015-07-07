package com.appspot.tradr_seba;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.images.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import scala.collection.immutable.*;
import scala.collection.JavaConverters.*;
import twirl.api.Html;

public class UserManagement {
    
    private static BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static ImagesService images = ImagesServiceFactory.getImagesService();
	
	
    public static String index(HttpServletRequest request){
    	HttpSession session =request.getSession(false);
        if(session !=null){
    	String username = session.getAttribute("username").toString();
    	return html.register.render(username,"","","","").toString();
        }
        else
        return html.register.render("","","","","").toString();
    }
    public static String change(HttpServletRequest request){
    	HttpSession session =request.getSession(false);
        if (session !=null){
    	String username = session.getAttribute("username").toString();
    	return html.changepassword.render(username,"initail").toString();
        }
        else
        	return html.changepassword.render("","initial").toString();

    }
    public static String findpasswordback(HttpServletRequest request){
    	HttpSession session = request.getSession(false);
        if(session !=null){
    	String username = session.getAttribute("username").toString();
    	return html.findpassword.render(username).toString();
        }
        else
        	return html.findpassword.render("").toString();

    }
    public static String user(HttpServletRequest request){
    	HttpSession session = request.getSession(false);
    	if(session !=null){
            String username = session.getAttribute("username").toString();
    		return html.user.render("yes",username).toString();
    	}
    	else
    	    return html.user.render("not","").toString();
    }

    public static String getUser(HttpServletRequest request) throws EntityNotFoundException {
    	
	String loginusername = request.getParameter("LoginUserName").toString();
	String loginpassword = request.getParameter("LoginPassword").toString();
	Entity user = null;
    try {user = datastore.get(KeyFactory.createKey("User", loginusername));}
    catch (EntityNotFoundException e) {return html.user.render("usernot","").toString();}

    String username = user.getProperty("username").toString();
    String password = user.getProperty("password").toString();
    
    if(loginpassword.equals(password)){
    	HttpSession session = request.getSession();
    	session.setAttribute("username",username);
        return html.user.render("yes",username).toString();
    }

    else {
    	return html.user.render("passwordnot","").toString();
    }
       
}
    public static String Logout(HttpServletRequest request){
    	HttpSession session = request.getSession(false);
    	if(session !=null)
    		session.setMaxInactiveInterval(1);
    	return html.logout.render().toString();
    }

    
    
   public static String addUser(HttpServletRequest request){
   	HttpSession session =request.getSession(false);
   	String US ="";
   	if(session !=null){
       US = session.getAttribute("username").toString();}
	   
        Entity user = new Entity("User",request.getParameter("username").toString());

        Date dateEntered = new Date();
        
        String username = request.getParameter("username").toString();
        String password = request.getParameter("password").toString();
        String email = request.getParameter("email").toString();
        String repeatpassword = request.getParameter("repeatpassword").toString();
        
        if (!username.substring(0,1).matches("[a-zA-Z]"))
           return html.register.render(US,"userfail","","","").toString();
        if (password.length()>12||password.length()<6)      	
        	return html.register.render(US,"","passwordfail","","").toString();
        if( ! password.equals(repeatpassword))
        	return html.register.render(US,"","","passwordnotthesame","").toString();
        if(! email.contains("@"))
        	return html.register.render(US,"","","","emailfail").toString();
         
     	Entity USER = null;
        try {USER = datastore.get(KeyFactory.createKey("User", username));}
         catch (EntityNotFoundException e){
        user.setProperty("username", username);
        user.setProperty("sex", request.getParameter("sex"));
        user.setProperty("date_entered", dateEntered);
        user.setProperty("password", password);
        user.setProperty("email", email);
        user.setProperty("phonenumber", request.getParameter("PhoneNo"));


        datastore.put(user);
        String welcome = "Welcome!";
        String newaccount = "You have successfully created a new account!";
        String enjoy = "Enjoy your tradr!";
        return html.afterregister.render(US,welcome,newaccount,enjoy).toString(); 
         }
        
        return html.register.render(US,"existing","","","").toString();
    }
   
   public static String showuser () {
		Query query = new Query("User");
        FetchOptions options = FetchOptions.Builder.withLimit(25);
        List<com.google.appengine.api.datastore.Entity> entities = datastore.prepare(query).asList(options);
        
        scala.collection.immutable.List<com.google.appengine.api.datastore.Entity> users = scala.collection.JavaConversions.asScalaIterable(entities).toList();
	    return html.alluser.render(users).toString();
   }
   
   public static String ChangePassword(HttpServletRequest request)  throws EntityNotFoundException {
   	HttpSession session =request.getSession(false);
   	String US ="";
   	if(session !=null)
        US = session.getAttribute("username").toString();
	   	   
	  String usernameinchange = request.getParameter("usernameinchange").toString();
	  String oldpassword = request.getParameter("oldpassword").toString();
	  String newpassword = request.getParameter("newpassword").toString();
	  String repeatnewpassword = request.getParameter("repeatnewpassword").toString();
	  Entity user = null;
	  try{ user = datastore.get(KeyFactory.createKey("User", usernameinchange));}
	  catch (EntityNotFoundException e) { return html.changepassword.render(US,"userfail").toString(); }
	  String username = user.getProperty("username").toString();
	  String password = user.getProperty("password").toString();	
	  if (! oldpassword.equals(password))
	      return html.changepassword.render(US,"oldpasswordwrong").toString();
      if (newpassword.length()>12||newpassword.length()<6)
    	  return html.changepassword.render(US,"newpasswordwrong").toString();
      if (! newpassword.equals(repeatnewpassword))
    	  return html.changepassword.render(US,"newpasswordnotmatch").toString();
		
      if (password.equals(newpassword)){			  
			  return html.changepassword.render(US,"passwordfail").toString();
		  }
		  else{
			  user.setProperty("password",newpassword);
			  datastore.put(user);
			  return html.afterchange.render(US,"","You have changed your password!","").toString();
		  }
   }
   
   public static String FindPassword(HttpServletRequest request) throws EntityNotFoundException{
   	HttpSession session =request.getSession(false);
   	String US= "";
   	if(session !=null)
     US = session.getAttribute("username").toString();
	   
	   String usernameinback = request.getParameter("usernameinback").toString();
	   String emailinback = request.getParameter("emailinback").toString();	   
	   Entity user = datastore.get(KeyFactory.createKey("User", usernameinback));
	   String password = user.getProperty("password").toString();
	   return html.afterrequestpassword.render(US,"","Your Password is "+password+" !","").toString();
   }
}
