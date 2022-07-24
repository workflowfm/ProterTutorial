package com.workflowfm.proter
package tutorial

import flows.*
import flows.given
import cases.*

import cats.Monad
import cats.implicits.*
import cats.effect.std.{ Random, UUIDGen }
import cats.effect.implicits.*

import scala.language.implicitConversions

final case class PizzaCase(waiterResource: String, ovenResource: String, chefResource: String) {
    val flow: Flow = {
      val takeOrder: FlowTask = Task("Take Order", Uniform(5, 10)) withResources(Seq(waiterResource))
      val prepare: FlowTask = Task("Prepare", Uniform(5, 10)) withResources(Seq(chefResource)) withCost 6
      val bake: FlowTask = Task("Bake", Uniform(20, 30)) withResources(Seq(ovenResource))
      val serve: FlowTask = Task("Serve", Uniform(1, 3)) withResources(Seq(waiterResource)) withPriority Task.High

      takeOrder > prepare > bake > serve
    }
}

given [F[_]](using m: Monad[F], u: UUIDGen[F], r: Random[F], caseFlow: Case[F, Flow]): Case[F, PizzaCase] with {
  override def init(name: String, count: Int, time: Long, p: PizzaCase): F[CaseRef[F]] = 
    caseFlow.init(name, count, time, p.flow)
}


final case class GarlicCase(waiterResource: String, ovenResource: String) {
  val flow: Flow = {
    val takeOrder: FlowTask = Task("Take Order", Uniform(5, 10)) withResources(Seq(waiterResource))
    val bake: FlowTask = Task("Bake", Uniform(5, 10)) withResources(Seq(ovenResource))
    val serve: FlowTask = Task("Serve", Uniform(1, 3)) withResources(Seq(waiterResource)) withPriority Task.High

    takeOrder > bake > serve
  }
}

given [F[_]](using m: Monad[F], u: UUIDGen[F], r: Random[F], caseFlow: Case[F, Flow]): Case[F, GarlicCase] with {
  override def init(name: String, count: Int, time: Long, g: GarlicCase): F[CaseRef[F]] = 
    caseFlow.init(name, count, time, g.flow)
}
