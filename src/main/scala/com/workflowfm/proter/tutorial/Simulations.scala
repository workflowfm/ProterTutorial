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

object PizzaOrder {
  def apply(waiter: String, chef: String): Flow = {
    val takeOrder: FlowTask = Task("Take Order", Uniform(5, 10)) withResources(Seq(waiter))
    val prepare: FlowTask = Task("Prepare", Uniform(5, 10)) withResources(Seq(chef)) withCost 6
    val bake: FlowTask = Task("Bake", Uniform(20, 30)) withResources(Seq("Oven"))
    val serve: FlowTask = Task("Serve", Uniform(1, 3)) withResources(Seq(waiter)) withPriority Task.High

    takeOrder > prepare > bake > serve
  }
}

final case class PizzaPlace(waiters: Seq[Resource], chefs: Seq[Resource])

given [F[_]](using Monad[F], UUIDGen[F], Random[F]): Case[F, PizzaPlace] with {
  override def init(name: String, count: Int, time: Long, p: PizzaPlace): F[CaseRef[F]] = for {
    wi <- Uniform(0, p.waiters.size).getLong
    ci <- Uniform(0, p.chefs.size).getLong

    waiter: String = p.waiters(wi.toInt).name
    chef: String = p.chefs(ci.toInt).name
    
    caseRef <- summon[Case[F, Flow]].init(name, count, time, PizzaOrder(waiter, chef)) 
  } yield caseRef
}

object GarlicBreadOrder {
  def apply(waiter: String): Flow = {
    val takeOrder: FlowTask = Task("Take Order", Uniform(5, 10)) withResources(Seq(waiter))
    val bake: FlowTask = Task("Bake", Uniform(5, 10)) withResources(Seq("Oven"))
    val serve: FlowTask = Task("Serve", Uniform(1, 3)) withResources(Seq(waiter)) withPriority Task.High

    takeOrder > bake > serve
  }
}

final case class GarlicPlace(waiters: Seq[Resource])

given [F[_]](using Monad[F], UUIDGen[F], Random[F]): Case[F, GarlicPlace] with {
  override def init(name: String, count: Int, time: Long, g: GarlicPlace): F[CaseRef[F]] = for {
    wi <- Uniform(0, g.waiters.size).getLong

    waiter: String = g.waiters(wi.toInt).name
    caseRef <- summon[Case[F, Flow]].init(name, count, time, GarlicBreadOrder(waiter))
  } yield caseRef

}
