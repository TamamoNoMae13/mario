package jade.scene

import jade.listener.KeyListener
import jade.Window
import values.Constants

import java.awt.event.KeyEvent

class LevelEditorScene : Scene() {
	private var changingScene = false
	private var timeToChangeScene = 2f

	init {
		println("Inside level editor scene!")
	}

	override fun update(dt: Float) {
		println("FPS: ${(1f / dt)}")
		if (KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) changingScene = true
		if (changingScene && timeToChangeScene > 0f) {
			timeToChangeScene -= dt
			Window.get().r -= dt * 5f
			Window.get().g -= dt * 5f
			Window.get().b -= dt * 5f
		} else if (changingScene) Window.changeScene(Constants.Scene.LEVEL_SCENE)
	}
}
