package jade

import kotlin.math.max
import kotlin.properties.Delegates
import org.lwjgl.*
import org.lwjgl.glfw.*
import org.lwjgl.glfw.Callbacks.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.*

class Window private constructor() {
    private val width = 1920
    private val height = 1080
    private val title = "Mario"
    private var glfwWindow by Delegates.notNull<Long>()
    private var r = 1f
    private var g = 1f
    private var b = 1f
    private var a = 1f
    private var fadeToBlack = false

    fun run() {
        println("Hello LWJGL ${Version.getVersion()}!")

        init()
        loop()

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(glfwWindow)
        glfwDestroyWindow(glfwWindow)

        // Terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null)!!.free()
    }

    private fun init() {
        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        // Configure GLFW
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE)

        // Create the window
        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL)
        if (glfwWindow == NULL) throw RuntimeException("Failed to create the GLFW window")

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback)
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback)
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback)
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback)

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(glfwWindow)
    }

    private fun loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(glfwWindow)) {
            // Set the clear color
            glClearColor(r, g, b, a)

            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer

            if (fadeToBlack) {
                r = max(r - 0.01f, 0f)
                g = max(g - 0.01f, 0f)
                b = max(b - 0.01f, 0f)
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
                fadeToBlack = true
            }

            glfwSwapBuffers(glfwWindow) // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents()

        }
    }

    companion object {
        private var window: Window? = null

        fun get(): Window {
            if (window == null) window = Window()
            return window!!
        }
    }
}
