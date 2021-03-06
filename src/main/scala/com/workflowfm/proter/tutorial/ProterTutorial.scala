package com.workflowfm.proter.tutorial

import com.workflowfm.proter._
import com.workflowfm.proter.schedule.ProterScheduler
import com.workflowfm.proter.metrics.{ SimMetricsHandler, SimMetricsPrinter }

import scala.concurrent.duration._
import scala.concurrent.Await
import com.workflowfm.proter.events.PrintEventHandler
import com.workflowfm.proter.metrics.SimCSVFileOutput
import com.workflowfm.proter.metrics.SimD3Timeline

trait Pizzeria {
  val waiter1: TaskResource = new TaskResource("Waiter 1", 1)
  val waiter2: TaskResource = new TaskResource("Waiter 2", 1)

  val oven: TaskResource = new TaskResource("Oven", 5)

  val chef1: TaskResource = new TaskResource("Chef 1", 1)
  val chef2: TaskResource = new TaskResource("Chef 2", 1)

  val waiters: Seq[TaskResource] = Seq(waiter1, waiter2)
  val chefs: Seq[TaskResource] = Seq(chef1, chef2)
}

object ProterTutorial extends App with Pizzeria {

  val coordinator: Coordinator = new Coordinator(new ProterScheduler)

  coordinator.subscribe(new SimMetricsHandler(
    new SimMetricsPrinter
      and new SimCSVFileOutput("output/", "Tutorial")
      and new SimD3Timeline("output/", "Tutorial")
  ))
  //coordinator.subscribe(new PrintEventHandler)
 
  coordinator.addResources(waiters)
  coordinator.addResources(chefs)
  coordinator.addResource(oven)

  val pizzaOrders: Seq[Simulation] = for (i <- 1 to 3) yield new PizzaOrder("Pizza " + i, waiter1.name, chef1.name, coordinator)
  val breadOrders: Seq[Simulation] = for (i <- 1 to 3) yield new GarlicBreadOrder("Garlic Bread " + i, waiter1.name, coordinator)

  coordinator.addSimulationsNow(pizzaOrders)
  coordinator.addSimulationsNow(breadOrders)

  Await.result(coordinator.start(), 1.hour)
}

object ProterTutorialArrivals extends App with Pizzeria {
  val coordinator: Coordinator = new Coordinator(new ProterScheduler)

  coordinator.subscribe(new SimMetricsHandler(
    new SimD3Timeline("output/", "Tutorial-Arrivals", 60000)
  ))
  //coordinator.subscribe(new PrintEventHandler)
 
  coordinator.addResources(waiters)
  coordinator.addResources(chefs)
  coordinator.addResource(oven)

  val pizzaOrders: SimulationGenerator = new PizzaOrderGenerator(waiters, chefs)
  val breadOrders: SimulationGenerator = new GarlicBreadOrderGenerator(waiters)

  coordinator.addInfiniteArrivalNow(Exponential(30), pizzaOrders)
  coordinator.addInfiniteArrivalNext(Exponential(45), breadOrders)

  coordinator.limit(24*60)

  Await.result(coordinator.start(), 1.hour)
}
