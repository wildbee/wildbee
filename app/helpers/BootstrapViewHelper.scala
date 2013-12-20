package helpers
import views.html.helper.FieldConstructor

object BootstrapViewHelper {
  implicit val myFields = FieldConstructor(views.html.templates.form_gen.f)
}
