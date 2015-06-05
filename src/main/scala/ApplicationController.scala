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

    def item(id: Long) = Action {
        try {
            Ok(Application.getItem(id))
        } catch {
            case e: Exception => NotFound
        }
    }
    
    def upload = Action {
        Ok(Application.upload())
    }

}
