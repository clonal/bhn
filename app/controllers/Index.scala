package controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}

class Index @Inject() (components: ControllerComponents)
    extends AbstractController(components) {

    def catchAll(path: String) = Action { implicit request =>
        Redirect("/")
    }
}
