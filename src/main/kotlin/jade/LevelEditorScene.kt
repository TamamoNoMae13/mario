package jade

import java.awt.event.KeyEvent

class LevelEditorScene() : Scene() {
    private var changingScene = false
    private var timeToChangeScene = 2f

    init {
        println("Inside level editor scene")
    }

    override fun update(dt: Float) {
        println("${1f / dt}FPS")
        if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            changingScene = true
        }
        if (changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt
            Window.get().r -= dt * 5f
            Window.get().g -= dt * 5f
            Window.get().b -= dt * 5f
        } else if (changingScene) {
            Window.changeScene(1)
        }
    }
}
