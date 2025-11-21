package com.example;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class OpenGLCoreExample {

    private long window;

    // Vértices de um triângulo (X, Y, Z)
    private final float[] vertices = {
            -0.5f, -0.5f, 0.0f,  // inferior esquerdo
             0.5f, -0.5f, 0.0f,  // inferior direito
             0.0f,  0.5f, 0.0f   // superior
    };

    public void run() {
        init();
        loop();

        // Encerrar GLFW
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        // Inicializar GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Erro ao inicializar GLFW!");
        }

        // Configuração de janela moderna (OpenGL Core Profile 3.3)
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        window = glfwCreateWindow(800, 600, "LWJGL - Triângulo", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Falha ao criar janela GLFW!");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // V-Sync
        glfwShowWindow(window);

        // Inicializar OpenGL
        GL.createCapabilities();

        // 🟢 Preparar VBO
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        // Converter vertices para FloatBuffer direto
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // 🔴 Criar VAO (Vertex Array Object)
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // Conectar VBO -> localização 0 do shader
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // 🧠 Criar shaders
        int shaderProgram = createShaders();
        glUseProgram(shaderProgram);
    }

    private int createShaders() {
        // Vertex Shader
        String vertexShaderSource = """
            #version 330 core
            layout(location = 0) in vec3 aPos;

            void main() {
                gl_Position = vec4(aPos, 1.0);
            }
        """;

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Vertex Shader Erro:\n" + glGetShaderInfoLog(vertexShader));
        }

        // Fragment Shader
        String fragmentShaderSource = """
            #version 330 core
            out vec4 FragColor;

            void main() {
                FragColor = vec4(1.0, 0.0, 0.0, 1.0); // vermelho
            }
        """;

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Fragment Shader Erro:\n" + glGetShaderInfoLog(fragmentShader));
        }

        // Criar programa de shader
        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Erro ao linkar shader program:\n" + glGetProgramInfoLog(shaderProgram));
        }

        // Liberar shaders após link
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return shaderProgram;
    }

    private void loop() {
        // Cor de fundo (cinza escuro)
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);

            // 🟩 Desenhar triângulo
            glDrawArrays(GL_TRIANGLES, 0, 3);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new OpenGLCoreExample().run();
    }
}