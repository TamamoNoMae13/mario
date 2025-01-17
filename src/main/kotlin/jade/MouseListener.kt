package jade

import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import kotlin.properties.Delegates

class MouseListener private constructor() {
    private var scrollX = 0.0
    private var scrollY = 0.0
    private var xPos = 0.0
    private var yPos = 0.0
    private var lastX = 0.0
    private var lastY = 0.0
    private val mouseButtonPressed = BooleanArray(3)
    private var isDragging by Delegates.notNull<Boolean>()

    companion object {
        private var instance: MouseListener? = null

        fun get(): MouseListener {
            if (instance == null) instance = MouseListener()
            return instance!!
        }

        fun mousePosCallback(window: Long, xPos: Double, yPos: Double) {
            get().lastX = get().xPos
            get().lastY = get().yPos
            get().xPos = xPos
            get().yPos = yPos
            get().isDragging =
                get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2]
        }

        fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
            if (button < get().mouseButtonPressed.size) {
                if (action == GLFW_PRESS) {
                    get().mouseButtonPressed[button] = true
                } else if (action == GLFW_RELEASE) {
                    get().mouseButtonPressed[button] = false
                    get().isDragging = false
                }
            }
        }

        fun mouseScrollCallback(window: Long, xOffset: Double, yOffset: Double) {
            get().scrollX = xOffset
            get().scrollY = yOffset
        }

        fun endFrame() {
            get().scrollX = 0.0
            get().scrollY = 0.0
            get().lastX = get().xPos
            get().lastY = get().yPos
        }

        val x: Float
            get() = get().xPos.toFloat()

        val y: Float
            get() = get().yPos.toFloat()

        val dx: Float
            get() = (get().lastX - get().xPos).toFloat()

        val dy: Float
            get() = (get().lastY - get().yPos).toFloat()

        val scrollX: Float
            get() = get().scrollX.toFloat()

        val scrollY: Float
            get() = get().scrollY.toFloat()

        val isDragging: Boolean
            get() = get().isDragging

        fun mouseButtonDown(button: Int) =
            if (button < get().mouseButtonPressed.size) get().mouseButtonPressed[button] else false
    }
}
