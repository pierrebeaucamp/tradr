package controllers

import com.appspot.tradr_seba._
import play.api.mvc._
import play.{Result, Controller}

object PlayController extends Controller {

    def addItem = Action { request =>
        Redirect(Item.add(request.req))
    }      

    def addOffer = Action { request => 
        try {
            Redirect(Offer.add(request.req))
        } catch {
            case e: Exception => InternalServerError
        }
    } 

    def index = Action {
        Ok(Application.index())
    }
    
    def register = Action { request =>
        Ok(UserManagement.index(request.req))
    }
       
    def afterregister = Action { request =>
        Ok(UserManagement.addUser(request.req))
    }
    
    def afterlogin = Action { request =>
       Ok(UserManagement.getUser(request.req))
    }

    def alluser = Action { 
        Ok(UserManagement.showuser())
    }
     
    def afterchange = Action { request =>    
        Ok(UserManagement.ChangePassword(request.req))
    }

    def changepassword = Action {request =>
        Ok(UserManagement.change(request.req))
    }

    def findpassword = Action{ request =>
        Ok(UserManagement.findpasswordback(request.req))
    }
    
    def afterrequestpassword = Action { request =>
       Ok(UserManagement.FindPassword(request.req))
    }
       
    def user = Action{ request =>
       Ok(UserManagement.user(request.req))
    }
    def logout = Action{ request =>
       Ok(UserManagement.Logout(request.req))
       }
    
    def item(id: Long) = Action {
        try {
            Ok(Item.get(id))
        } catch {
            case e: Exception => NotFound
        }
  } 
    
    def offer(id: Long) = Action {
        try {
            Ok(Offer.get(id))
        } catch {
            case e: Exception => NotFound
        }
    }

    def offers = Action {
        Ok(Offer.all())
    }

    def updateOffer = Action { request =>
        try {
            Redirect(Offer.update(request.req))
        } catch {
            case e: Exception => InternalServerError
        }
    }
    def searchTag = Action { request => 
        try {
            Ok(Item.searchTag(request.req))
        } catch {
            case e: Exception => InternalServerError
        }
    }

    def upload = Action {
        Ok(Item.form())
    }
}
