package jade

import values.Constants

import kotlin.properties.Delegates

import org.lwjgl.Version
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL

class Window {
	private var glfwWindow by Delegates.notNull<Long>()

	fun run() {
		println("Hello LWJGL ${Version.getVersion()}!")
		init()
		loop()
	}

	private fun init() {
		// Set up an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set()

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		check(glfwInit()) { "Unable to initialize GLFW" }

		// Configure GLFW
		glfwDefaultWindowHints() // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

		// Create the window
		glfwWindow = glfwCreateWindow(
			Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, Constants.WINDOW_TITLE, NULL, NULL
		)
		if (glfwWindow == NULL) throw RuntimeException("Failed to create the GLFW window")

		// Set up a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(
			glfwWindow
		) { window: Long, key: Int, scanCode: Int, action: Int, mods: Int ->
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(
				window, true
			) // We will detect this in the rendering loop
		}

		// Get the thread stack and push a new frame
		stackPush().use { stack ->
			val pWidth = stack.mallocInt(1) // int*
			val pHeight = stack.mallocInt(1) // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(glfwWindow, pWidth, pHeight)

			// Get the resolution of the primary monitor
			val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())

			// Center the window
			vidMode?.let {
				glfwSetWindowPos(
					glfwWindow, (it.width() - pWidth[0]) / 2, (it.height() - pHeight[0]) / 2
				)
			}
		}

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

		// Set the clear color
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(glfwWindow)) {
			glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer
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
