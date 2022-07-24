package com.workflowfm.proter
package tutorial

import flows.*
import flows.given
import metrics.*
import schedule.ProterScheduler

import cats.effect.{ IO, IOApp, ExitCode }
import cats.effect.std.Random

class Pizzeria(waiters: Int, ovens: Int, chefs: Int) {
  val waiterResource: Resource = Resource("Waiter", waiters, 1)
  val ovenResource: Resource = Resource("Oven", ovens, 5)
  val chefResource: Resource = Resource("Chef", chefs, 1)

  val pizza = PizzaCase(waiterResource.name, ovenResource.name, chefResource.name)
  val garlicBread = GarlicCase(waiterResource.name, ovenResource.name)
}

object ProterTutorial extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    Random.scalaUtilRandom[IO].flatMap { r =>
      given Random[IO] = r
      val pizzeria = new Pizzeria(2, 3, 1)

      val simulator = Simulator[IO](ProterScheduler) withSubs (
        MetricsSubscriber[IO](
          MetricsPrinter(),
          CSVFile("output/", "Tutorial"),
          D3Timeline("output/", "Tutorial")
        )
      )

      val pizzaOrders: Seq[(String, Flow)] = 
        for (i <- 1 to 3) yield ("Pizza " + i, pizzeria.pizza.flow)
      val breadOrders: Seq[(String, Flow)] = 
        for (i <- 1 to 3) yield ("Garlic Bread " + i, pizzeria.garlicBread.flow)

      val scenario = Scenario[IO]("Pizza Tutorial")
        .withResource(pizzeria.waiterResource)
        .withResource(pizzeria.chefResource)
        .withResource(pizzeria.ovenResource)
        .withCases(pizzaOrders :_*)
        .withCases(breadOrders :_*)

      simulator.simulate(scenario).as(ExitCode(1))
    }
}

object ProterTutorialArrivals extends IOApp  {
  def run(args: List[String]): IO[ExitCode] =
    Random.scalaUtilRandom[IO].flatMap { r =>
      given Random[IO] = r
      val pizzeria = new Pizzeria(2, 3, 1)

      val simulator = Simulator[IO](ProterScheduler) withSubs (
        MetricsSubscriber[IO](
          D3Timeline("output/", "Tutorial-Arrivals", 1000)
        )
      )

      val scenario = Scenario[IO]("Pizza Tutorial")
        .withResource(pizzeria.waiterResource)
        .withResource(pizzeria.chefResource)
        .withResource(pizzeria.ovenResource)
        .withInfiniteArrival("Pizza", pizzeria.pizza, Exponential(30*60))
        // typo!!
        .withTimedInifiniteArrival("Garlic Bread", 40*60, pizzeria.garlicBread, Exponential(40*60))
        .withLimit(24*60*60)

      simulator.simulate(scenario).as(ExitCode(1))
    }
}
