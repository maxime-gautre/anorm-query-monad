package core.controllers

import controllers.Assets.Asset
import controllers.AssetsMetadata
import play.api.{Environment, Mode}
import play.api.http.HttpErrorHandler
import play.api.mvc._

class XAssets(
    environment: Environment,
    httpErrorHandler: HttpErrorHandler,
    meta: AssetsMetadata,
    cc: ControllerComponents
) extends AbstractController(cc) {

  lazy val assets =
    new controllers.Assets(httpErrorHandler, meta: AssetsMetadata)
  lazy val externalAssets = new controllers.ExternalAssets(environment)(
    cc.executionContext,
    cc.fileMimeTypes)

  /**
    * Fixes dev mode assets being served as classloader resources and sometimes being outdated.
    */
  def versioned(path: String, file: Asset): Action[AnyContent] = {
    if (environment.mode == Mode.Prod)
      assets.versioned(path, file)
    else
      externalAssets.at(path.drop(1), file.name)
  }

  val favicon = assets.versioned(path = "/public", "favicon.ico")
}
