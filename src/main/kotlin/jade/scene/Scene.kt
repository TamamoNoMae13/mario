package jade.scene

abstract class Scene {
	open fun init() {}
	abstract fun update(dt: Float)
}
