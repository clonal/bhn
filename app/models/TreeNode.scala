package models

import play.api.libs.json.{Json, OFormat}

case class TreeNode(id: Int, name: String, order: Int, desc: String, var children: Seq[TreeNode]) {
  def addChild(child: TreeNode) = {
    children = children :+ child
  }

  def sort(): Unit = {
    children = children.sortBy(_.order)
    children.foreach(_.sort())
  }
}

object TreeNode {
  implicit val treeNodeFormat: OFormat[TreeNode] = Json.format[TreeNode]
}
