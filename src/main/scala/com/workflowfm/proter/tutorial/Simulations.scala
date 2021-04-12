package com.workflowfm.proter.tutorial

import com.workflowfm.proter._
import com.workflowfm.proter.flows._

class PizzaOrderGenerator(waiters: Seq[TaskResource], chefs: Seq[TaskResource]) extends SimulationGenerator {
  val rWaiter = Uniform(0, waiters.size)
  val rChef = Uniform(0, chefs.size)

  override def build(manager: Manager, count: Int): Simulation = {
    val name = "Pizza " + count.toString()

    val waiter: String = waiters(rWaiter.getLong.toInt).name
    val chef: String = chefs(rChef.getLong.toInt).name

    val takeOrder: FlowTask = Task("Take Order", Uniform(5, 10)) withResources(Seq(waiter))
    val prepare: FlowTask = Task("Prepare", Uniform(5, 10)) withResources(Seq(chef)) withCost 6
    val bake: FlowTask = Task("Bake", Uniform(20, 30)) withResources(Seq("Oven")) 
    val serve: FlowTask = Task("Serve", Uniform(1, 3)) withResources(Seq(waiter)) withPriority Task.High 

    val flow: Flow = takeOrder > prepare > bake > serve

    new FlowSimulation(name, manager, flow.copy())
  }
}

class GarlicBreadOrderGenerator(waiters: Seq[TaskResource]) extends SimulationGenerator {
  val rWaiter = Uniform(0, waiters.size)

  override def build(manager: Manager, count: Int): Simulation = {
    val name = "Garlic Bread " + count.toString()

    val waiter: String = waiters(rWaiter.getLong.toInt).name

    val takeOrder: FlowTask = Task("Take Order", Uniform(5, 10)) withResources(Seq(waiter)) 
    val bake: FlowTask = Task("Bake", Uniform(5, 10)) withResources(Seq("Oven")) 
    val serve: FlowTask = Task("Serve", Uniform(1, 3)) withResources(Seq(waiter)) withPriority Task.High 

    val flow: Flow = takeOrder > bake > serve

    new FlowSimulation(name, manager, flow.copy())
  }
}
