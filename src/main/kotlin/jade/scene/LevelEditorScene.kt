package jade.scene

import values.Constants

import kotlin.properties.Delegates

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*

class LevelEditorScene : Scene() {
	private var vertexID by Delegates.notNull<Int>()
	private var fragmentID by Delegates.notNull<Int>()
	private var shaderProgram by Delegates.notNull<Int>()
	private var vaoID by Delegates.notNull<Int>()
	private var vboID by Delegates.notNull<Int>()
	private var eboID by Delegates.notNull<Int>()

	private val vertexArray = floatArrayOf(
		// position				// color
		0.5f, -0.5f, 0.0f,		1.0f, 0.0f, 0.0f, 1.0f,		// Bottom-right	0
		-0.5f, 0.5f, 0.0f,		0.0f, 1.0f, 0.0f, 1.0f,		// Top-left		1
		0.5f, 0.5f, 0.0f,		0.0f, 0.0f, 1.0f, 1.0f,		// Top-right	2
		-0.5f, -0.5f, 0.0f,		1.0f, 1.0f, 0.0f, 1.0f		// Bottom-left	3
	)

	// IMPORTANT: Must be in counter-clockwise order
	private val elementArray = intArrayOf(
		2, 1, 0, // Top-right triangle
		0, 1, 3 // Bottom-left triangle
	)

	override fun init() {
		compileAndLinkShaders(Constants.Shader.VERTEX)
		compileAndLinkShaders(Constants.Shader.FRAGMENT)

		shaderProgram = glCreateProgram().also {
			glAttachShader(it, vertexID)
			glAttachShader(it, fragmentID)
			glLinkProgram(it)

			val success = glGetProgrami(it, GL_LINK_STATUS)
			if (success == GL_FALSE) {
				val len = glGetProgrami(it, GL_INFO_LOG_LENGTH)
				println("ERROR: \"defaultShader.glsl\"\n\tLinking shaders failed.")
				println(glGetProgramInfoLog(it, len))
				assert(false) { "" }
			}
		}

		vaoID = glGenVertexArrays().also {
			glBindVertexArray(it)
		}

		val vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.size).apply {
			this.put(vertexArray).flip()
		}

		vboID = glGenBuffers()
		glBindBuffer(GL_ARRAY_BUFFER, vboID)
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

		val elementBuffer = BufferUtils.createIntBuffer(elementArray.size)
		elementBuffer.put(elementArray).flip()

		eboID = glGenBuffers()
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID)
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW)

		val positionsSize = 3
		val colourSize = 4
		val floatSizeBytes = 4
		val vertexSizeBytes = (positionsSize + colourSize) * floatSizeBytes

		glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0)
		glEnableVertexAttribArray(0)

		glVertexAttribPointer(
			1, colourSize, GL_FLOAT, false, vertexSizeBytes,
			(positionsSize * floatSizeBytes).toLong()
		)
		glEnableVertexAttribArray(1)
	}

	override fun update(dt: Float) {
		glUseProgram(shaderProgram)
		glBindVertexArray(vaoID)

		glEnableVertexAttribArray(0)
		glEnableVertexAttribArray(1)

		glDrawElements(GL_TRIANGLES, elementArray.size, GL_UNSIGNED_INT, 0)

		glDisableVertexAttribArray(0)
		glDisableVertexAttribArray(1)

		glBindVertexArray(0)

		glUseProgram(0)
	}

	private fun compileAndLinkShaders(shader: Constants.Shader) {

		// First: load and compile the fragment shader
		when (shader) {
			Constants.Shader.VERTEX -> {
				vertexID = glCreateShader(GL_VERTEX_SHADER)
				vertexID
			}
			Constants.Shader.FRAGMENT -> {
				fragmentID = glCreateShader(GL_FRAGMENT_SHADER)
				fragmentID
			}
		}.also {

			// Pass the shader source to the GPU
			glShaderSource(
				it, when (shader) {
					Constants.Shader.VERTEX -> vertexShaderSrc
					Constants.Shader.FRAGMENT -> fragmentShaderSrc
				}
			)
			glCompileShader(it)

			// Check for errors in compilation
			val success = glGetShaderi(it, GL_COMPILE_STATUS)
			if (success == GL_FALSE) {
				val len = glGetShaderi(it, GL_INFO_LOG_LENGTH)
				println("ERROR: \"defaultShader.glsl\"")
				println(
					"\t" + when (shader) {
						Constants.Shader.VERTEX -> "Vertex"
						Constants.Shader.FRAGMENT -> "Fragment"
					} + " shader compilation failed."
				)
				println(glGetShaderInfoLog(it, len))
				assert(false) { "" }
			}
		}
	}

	companion object {
		const val vertexShaderSrc =
			"#version 330 core\n" + "layout (location = 0) in vec3 aPos;\n" + "layout (location = 1) in vec4 aColor;\n" + "\n" + "out vec4 fColor;\n" + "\n" + "void main() {\n" + "\tfColor = aColor;\n" + "\tgl_Position = vec4(aPos, 1.0);\n" + "}"
		const val fragmentShaderSrc =
			"#version 330 core\n" + "\n" + "in vec4 fColor;\n" + "out vec4 color;\n" + "\n" + "void main() {\n" + "\tcolor = fColor;\n" + "}"
	}
}
