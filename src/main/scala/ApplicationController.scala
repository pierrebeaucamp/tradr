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
    
    def register = Action {
        Ok(UserManagement.index())
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

    def changepassword = Action {
        Ok(UserManagement.change())
    }

    def findpassword = Action{
        Ok(UserManagement.findpasswordback())
    }
    
    def afterrequestpassword = Action { request =>
       Ok(UserManagement.FindPassword(request.req))
    }
       
    def user = Action{
       Ok(UserManagement.user())
    }
    
    def item(id: Long) = Action {
        try {
            Ok(Item.get(id))
        } catch {
            case e: Exception => NotFound
        }
  } 
  def searchTag(tag: String) = Action {
        try {
            Ok(Application.searchTag(tag))
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
        Ok(Offer.update(request.req))
    }

    def upload = Action {
        Ok(Item.form())
    }
}
