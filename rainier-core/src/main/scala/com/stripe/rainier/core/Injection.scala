package com.stripe.rainier.core

import com.stripe.rainier.compute._
import com.stripe.rainier.unused

trait Injection { self =>

  def forwards(x: Real): Real
  def backwards(y: Real): Real
  def isDefinedAt(@unused y: Real): Real = Real.one
  def requirements: Set[Real]

  /*
    See https://en.wikipedia.org/wiki/Probability_density_function#Dependent_variables_and_change_of_variables
    This function should be log(d/dy backwards(y)), where y = forwards(x).
   */
  def logJacobian(y: Real): Real

  def transform(dist: Continuous): Continuous = new Continuous {
    def realLogDensity(real: Real) =
      If(isDefinedAt(real),
         dist.realLogDensity(backwards(real)) +
           logJacobian(real),
         Real.zero.log)

    val generator = Generator.require(self.requirements) { (r, n) =>
      n.toDouble(forwards(dist.generator.get(r, n)))
    }

    val param = dist.param.map(forwards)
  }
}

case class Scale(a: Real) extends Injection {
  private val lj = a.log * -1
  def forwards(x: Real) = x * a
  def backwards(y: Real) = y / a
  def logJacobian(y: Real) = lj
  val requirements = Set(a)
}

case class Translate(b: Real) extends Injection {
  def forwards(x: Real) = x + b
  def backwards(y: Real) = y - b
  def logJacobian(y: Real) = Real.zero
  val requirements = Set(b)
}

object Exp extends Injection {
  def forwards(x: Real) = x.exp
  def backwards(y: Real) = y.log

  //this is rarely important because it depends solely on y, which is usually data and not parameters
  def logJacobian(y: Real) = y.log * -1

  override def isDefinedAt(y: Real) = y > 0
  val requirements = Set.empty
}