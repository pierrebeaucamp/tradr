package controllers

import com.appspot.tradr_seba._
import play.api.mvc._
import play.{Result, Controller}

object PlayController extends Controller {

    def addItem = Action { request =>
        Redirect(Application.addItem(request.req))
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
    
  def item(id: Long) = Action {
        try {
            Ok(Application.getItem(id))
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
    
    def upload = Action {
        Ok(Application.upload())
    }

   


}
