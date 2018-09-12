package com.stripe.rainier.compute

object Kahan {

  def sum(array: Array[Double]): Double = {
    var partialSum = 0.0
    var compensation = 0.0
    array.foreach { x =>
      val nextSummand = x - compensation
      val newPartialSum = partialSum + nextSummand
      compensation = (newPartialSum - partialSum) - nextSummand
      partialSum = newPartialSum
    }
    partialSum
  }

  /**
    * Neumaier sum
    */
  def nSum(array: Array[Double]): Double = {
    var partialSum = array(0)
    var compensation = 0.0
    array.drop(1).foreach { x =>
      val newPartialSum = partialSum + x
      if (math.abs(partialSum) > math.abs(x)) {
        compensation += (partialSum - newPartialSum) + x
      } else {
        compensation += (x - newPartialSum) + partialSum
      }
      partialSum = newPartialSum
    }
    partialSum + compensation
  }
}
