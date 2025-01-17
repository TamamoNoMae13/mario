package util

class Time {
    companion object {
        var timeStarted = System.nanoTime().toFloat()

        fun getTime(): Float = ((System.nanoTime() - timeStarted) * 1E-9).toFloat()
    }
}
