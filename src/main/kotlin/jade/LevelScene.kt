package jade

class LevelScene() : Scene() {
    init {
        println("Inside level scene")
        Window.get().r = 1f
        Window.get().g = 1f
        Window.get().b = 1f
    }

    override fun update(dt: Float) {
    }
}
