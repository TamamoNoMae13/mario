package utils

object Time {
	var timeStarted = System.nanoTime().toFloat()

	val time: Float
		get() = ((System.nanoTime() - timeStarted) * 1E-9).toFloat()
}
