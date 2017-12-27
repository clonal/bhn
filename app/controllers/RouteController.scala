package controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}

class RouteController @Inject()(components: ControllerComponents) extends AbstractController(components){
//  val set = Set.empty[IJavaScriptReverseRouterProvider]

/*  def scriptRoutes() = Action { implicit request =>
//    val jsRoutesClasses = Seq(classOf[auth.routes.javascript]) // TODO add your own packages
    val jsRoutesClasses = Seq(classOf[routes.javascript], classOf[auth.routes.javascript]) // TODO add your own packages
    val l = jsRoutesClasses.flatMap { jsRoutesClass =>
      val controllers = jsRoutesClass.getFields.map(_.get(null))
      controllers.flatMap { controller =>
        controller.getClass.getDeclaredMethods.filter(_.getName != "_defaultPrefix").map { action =>
          action.invoke(controller).asInstanceOf[play.api.routing.JavaScriptReverseRoute]
        }
      }
    }
    Ok(JavaScriptReverseRouter("jsRoutes")(l: _*)).as(JAVASCRIPT)
  }*/

  def activateRoute(token: String) = Action {Ok("")}

  def resetPasswordRoute(token: String) = Action {Ok("")}
}
