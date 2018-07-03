import com.apollographql.scalajs.{ApolloBoostClient, UpVote}
import org.scalajs.dom.raw.{Event, HTMLInputElement}
import org.scalajs.dom.{document, html}
import model.Task
import slinky.core._
import slinky.core.annotations.react
import slinky.web.ReactDOM
import slinky.web.html._

import scala.scalajs.js
import scala.scalajs.js.Date
import com.apollographql.scalajs.react.{ApolloProvider, Query}
import slinky.core.facade.ReactElement
object Main  {


  @react
  class TodoApp extends Component {
    type Props = Unit
    case class State(items: Seq[Task], text: String)

    override def initialState = State(Seq.empty, "")

    def handleChange(e: Event): Unit = {
      val eventValue =
        e.target.asInstanceOf[HTMLInputElement].value
      setState(_.copy(text = eventValue))
    }

    def handleSubmit(e: Event): Unit = {
      e.preventDefault()

      if (state.text.nonEmpty) {
        val newItem = Task(
          name = state.text
        )

        setState(prevState => {
          State(
            items = prevState.items :+ newItem,
            text = ""
          )
        })
      }
    }

    override def render() = {
      div(
        h3("TODO"),
        TodoList(items = state.items),
        form(onSubmit := handleSubmit _)(
          input(
            onChange := handleChange _,
            value := state.text
          ),
          button(s"Add #${state.items.size + 1}")
        )
      )
    }
  }

  @react
  class TodoList extends StatelessComponent {
    case class Props()



    def render(): ReactElement = {
      Query(QueryT) { res =>
        res.data.map { d =>

          ul(
            d.items.map { item =>
              li(key := item.id.toString)(item.name)
            }
          )


      }
    }

    override def render2() = {
      ul(
        props.items.map { item =>
          li(key := item.id.toString)(item.name)
        }
      )
    }
  }


  def main(args: Array[String]): Unit ={

    if (js.isUndefined(js.Dynamic.global.reactContainer)) {
      js.Dynamic.global.reactContainer = document.createElement("div")
      document.body.appendChild(js.Dynamic.global.reactContainer.asInstanceOf[html.Element])
    }

    ReactDOM.render(
      TodoApp(),
      js.Dynamic.global.reactContainer.asInstanceOf[html.Element]
    )
  }

  val client = ApolloBoostClient(
    uri = "https://1jzxrj179.lp.gql.zone/graphql"
  )
}