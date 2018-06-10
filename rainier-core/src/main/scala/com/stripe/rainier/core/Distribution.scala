package com.stripe.rainier.core

import com.stripe.rainier.compute.Real

/**
  * Basic probability distribution trait
  */
trait Distribution[T] extends Likelihood[T] { self =>
  def logDensity(t: T): Real
  def logDensities(list: Seq[T]): Real = Real.sum(list.map(logDensity))

  def generator: Generator[T]

  def fit(t: T): RandomVariable[Generator[T]] =
    RandomVariable(generator, logDensity(t))
  override def fit(list: Seq[T]): RandomVariable[Generator[Seq[T]]] =
    RandomVariable(generator.repeat(list.size), logDensities(list))
}

/**
  * Combinatoric functions required for log density calculations. Note that they all return the log of the function described.
  */
object Combinatrics {
  def gamma(z: Real): Real = {
    // This is Gergő Nemes' approximation to the log Gamma function, plus a trick taken from Boost's lgamma function:
    // since the Nemes approximation isn't very accurate for small z, we instead calculate LogGamma(z + 1) - Log(z).
    // See https://en.wikipedia.org/wiki/Stirling%27s_approximation and
    // https://www.boost.org/doc/libs/1_50_0/libs/math/doc/sf_and_dist/html/math_toolkit/special/sf_gamma/lgamma.html.
    val v = z + 1
    val w = v + (Real.one / ((12 * v) - (Real.one / (10 * v))))
    (Real(Math.PI * 2).log / 2) - (v.log / 2) + (v * (w.log - 1)) - z.log
  }

  def beta(a: Real, b: Real): Real =
    gamma(a) + gamma(b) - gamma(a + b)

  def factorial(k: Int): Real = gamma(Real(k + 1))

  def choose(n: Int, k: Int): Real =
    factorial(n) - factorial(k) - factorial(n - k)
}
