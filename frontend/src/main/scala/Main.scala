import org.scalajs.dom
import org.scalajs.dom.document


object Main  {

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  def main(args: Array[String]): Unit ={
    appendPar(document.body, "Hello World")
  }
}