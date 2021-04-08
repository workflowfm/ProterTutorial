package com.workflowfm.proter.tutorial

import com.workflowfm.proter._
import com.workflowfm.proter.flows._

class PizzaOrderGenerator(waiters: Seq[TaskResource], chefs: Seq[TaskResource]) extends SimulationGenerator {
  val r = scala.util.Random

  def rand[T](s: Seq[T]): T = s(r.nextInt(s.size))

  override def build(manager: Manager, count: Int): Simulation = {
    val name = "Pizza " + count.toString()

    val waiter: String = rand(waiters).name
    val chef: String = rand(chefs).name

    val takeOrder: FlowTask = new FlowTask( Task("Take Order", Uniform(5, 10)) withResources(Seq(waiter)) )
    val prepare: FlowTask = new FlowTask( Task("Prepare", Uniform(5, 10)) withResources(Seq(chef)) withCost 6 )
    val bake: FlowTask = new FlowTask( Task("Bake", Uniform(20, 30)) withResources(Seq("Oven")) )
    val serve: FlowTask = new FlowTask( Task("Serve", Uniform(1, 3)) withResources(Seq(waiter)) withPriority Task.High )

    val flow: Flow = takeOrder > prepare > bake > serve

    new FlowSimulation(name, manager, flow.copy())
  }
}

class GarlicBreadOrderGenerator(waiters: Seq[TaskResource]) extends SimulationGenerator {
  val r = scala.util.Random

  def rand[T](s: Seq[T]): T = s(r.nextInt(s.size))

  override def build(manager: Manager, count: Int): Simulation = {
    val name = "Garlic Bread " + count.toString()

    val waiter: String = rand(waiters).name

    val takeOrder: FlowTask = new FlowTask( Task("Take Order", Uniform(5, 10)) withResources(Seq(waiter)) )
    val bake: FlowTask = new FlowTask( Task("Bake", Uniform(5, 10)) withResources(Seq("Oven")) )
    val serve: FlowTask = new FlowTask( Task("Serve", Uniform(1, 3)) withResources(Seq(waiter)) withPriority Task.High )

    val flow: Flow = takeOrder > bake > serve

    new FlowSimulation(name, manager, flow.copy())
  }
}
