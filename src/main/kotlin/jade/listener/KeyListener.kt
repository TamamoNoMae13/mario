package jade.listener

import org.lwjgl.glfw.GLFW.*

class KeyListener private constructor() {
	private val keyPressed = BooleanArray(350)

	companion object {
		private var instance: KeyListener? = null

		fun get(): KeyListener {
			if (instance == null) instance = KeyListener()
			return instance!!
		}

		fun keyCallback(window: Long, key: Int, scanCode: Int, action: Int, mods: Int) {
			when (action) {
				GLFW_PRESS -> get().keyPressed[key] = true
				GLFW_RELEASE -> get().keyPressed[key] = false
			}
		}

		fun isKeyPressed(keyCode: Int): Boolean {
			return get().keyPressed[keyCode]
		}
	}
}
