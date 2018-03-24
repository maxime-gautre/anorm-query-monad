package cinema.controllers

import play.api.libs.json.Json
import play.api.mvc._

class HomeController(cc: ControllerComponents) extends AbstractController(cc) {

  def index(path: String) = Action {
    val initData = Json.obj("helloWorld" -> path)
    Ok(Json.toJson(initData))
  }

  def about = Action {
    Ok(sbt.BuildInfo.toString)
  }
}
