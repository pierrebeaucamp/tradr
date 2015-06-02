package controllers

import com.appspot.tradr_seba._
import play.api.mvc._
import play.{Result, Controller}

object PlayController extends Controller {

  def index = Action {
    Ok(Application.index())
  }
  
}