package com.workflowfm.proter
package tutorial

import flows.*
import flows.given
import metrics.*
import schedule.ProterScheduler

import cats.effect.{ IO, IOApp, ExitCode }
import cats.effect.std.Random

trait Pizzeria {
  val waiter1: Resource = Resource("Waiter 1", 1, 1)
  val waiter2: Resource = Resource("Waiter 2", 1, 1)

  val oven: Resource = Resource("Oven", 1, 5)

  val chef1: Resource = Resource("Chef 1", 1, 1)
  val chef2: Resource = Resource("Chef 2", 1, 1)

  val waiters: Seq[Resource] = Seq(waiter1, waiter2)
  val chefs: Seq[Resource] = Seq(chef1, chef2)

  val pizzas: PizzaPlace = PizzaPlace(waiters, chefs)
  val breads: GarlicPlace = GarlicPlace(waiters)
}

object ProterTutorial extends IOApp with Pizzeria {
  def run(args: List[String]): IO[ExitCode] =
    Random.scalaUtilRandom[IO].flatMap { r =>
      given Random[IO] = r
      val simulator = Simulator[IO](ProterScheduler) withSubs (
        MetricsSubscriber[IO](
          MetricsPrinter(),
          CSVFile("output/", "Tutorial"),
          D3Timeline("output/", "Tutorial")
        )
      )

      val pizzaOrders: Seq[(String, Flow)] = 
        for (i <- 1 to 3) yield ("Pizza " + i, PizzaOrder(waiter1.name, chef1.name))
      val breadOrders: Seq[(String, Flow)] = 
        for (i <- 1 to 3) yield ("Garlic Bread " + i, GarlicBreadOrder(waiter1.name))

      val scenario = Scenario[IO]("Pizza Tutorial")
        .withResources(waiters)
        .withResources(chefs)
        .withResource(oven)
        .withCases(pizzaOrders :_*)
        .withCases(breadOrders :_*)

      simulator.simulate(scenario).as(ExitCode(1))
    }
}

object ProterTutorialArrivals extends IOApp with Pizzeria {
  def run(args: List[String]): IO[ExitCode] =
    Random.scalaUtilRandom[IO].flatMap { r =>
      given Random[IO] = r
      val simulator = Simulator[IO](ProterScheduler) withSubs (
        MetricsSubscriber[IO](
          D3Timeline("output/", "Tutorial-Arrivals", 60000)
        )
      )

      val scenario = Scenario[IO]("Pizza Tutorial")
        .withResources(waiters)
        .withResources(chefs)
        .withResource(oven)
        .withInfiniteArrival("Pizza", PizzaPlace(waiters, chefs), Exponential(30))
        // typo!!
        .withTimedInifiniteArrival("Garlic Bread", 45, GarlicPlace(waiters), Exponential(45))
        .withLimit(24*60)

      simulator.simulate(scenario).as(ExitCode(1))
    }
}
