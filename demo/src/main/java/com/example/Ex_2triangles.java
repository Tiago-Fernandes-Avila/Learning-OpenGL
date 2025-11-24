package com.example;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_BUFFER_ACCESS;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_MAX_VERTEX_ATTRIBS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import com.*;

public class Ex_2triangles {

    private final String vertexSrc = ReadShader.read("/home/devtiago/Área de trabalho/Workspace/programas c++/LWJGL-OpenGL-/demo/src/main/java/com/example/shaders/vertexShaders/vertexShader1.glsl");

    private final String fragmentSrc1 = ReadShader.read("/home/devtiago/Área de trabalho/Workspace/programas c++/LWJGL-OpenGL-/demo/src/main/java/com/example/shaders/FragmentShader/fragS1.glsl");

    private final String fragmentSrc2 = ReadShader.read("/home/devtiago/Área de trabalho/Workspace/programas c++/LWJGL-OpenGL-/demo/src/main/java/com/example/shaders/FragmentShader/fragS2.glsl");

    private long window;

    private int VAO1;
    private int VBO1;

    private int VAO2;
    private int VBO2;

    private int shaderProgram1;
    private int shaderProgrma2;

    public static void main(String[] args) {
        new Ex_2triangles().run();

    }

    public void run() {
        init();
        setupGeometry();
        loop();

        glfwTerminate();
        glfwDestroyWindow(window);
    }

    public void loop() {

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);

            glBindVertexArray(VAO2);
            glUseProgram(shaderProgrma2);
            int glVertexColorLocation = glGetUniformLocation(shaderProgrma2, "colorFromCpu");
            glUniform4f(glVertexColorLocation,0.0f, 0.0f, 2 * (float) Math.abs(Math.sin((float)glfwGetTime())) / 2.0f, 1.0f);
            glDrawArrays(GL_TRIANGLES, 0, 3);


            glBindVertexArray(VAO1);
            glUseProgram(shaderProgram1);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            glfwPollEvents();
            glfwSwapBuffers(window);
        }
    }

    public void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(640, 480, "ex_01", 0, 0);

        if (window == 0) {
            throw new RuntimeException("Não foi possivel criar a janela");
        }

        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        IntBuffer attrNum = BufferUtils.createIntBuffer(1);
        glGetIntegerv(GL_MAX_VERTEX_ATTRIBS, attrNum);
        System.out.println("Quantidade de atributos: " + attrNum.get(0));

        glfwShowWindow(window);
    }

    public void setupGeometry() {

        VAO1 = glGenVertexArrays();
        VAO2 = glGenVertexArrays();

        float[] triangle1 = {
                -1.0f, 0.0f, 0.0f,
                -0.5f, 0.8f, 0.0f,
                0.0f, 0.0f, 0.0f,
        };

        float[] triangle2 = {
                //position        colors
                1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, 0.8f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f 
        };

        VBO1 = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO1);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(triangle1.length);
        buffer.put(triangle1);
        buffer.flip();

        glBindVertexArray(VAO1);

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        

        VBO2 = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO2);
        FloatBuffer buffer2 = BufferUtils.createFloatBuffer(triangle2.length);
        buffer2.put(triangle2);
        buffer2.flip();

        glBindVertexArray(VAO2);

        glBufferData(GL_ARRAY_BUFFER, buffer2, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES); 
        glEnableVertexAttribArray(1);

        setShaderProgram();

    }

    public void setShaderProgram() {
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexSrc);
        glCompileShader(vertexShader);

        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Não foi possivel compilar o shader 1");
        }

        int fragmentShader1 = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader1, fragmentSrc1);
        glCompileShader(fragmentShader1);

        int fragmentShader2 = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader2, fragmentSrc2);
        glCompileShader(fragmentShader2);

        if (glGetShaderi(fragmentShader1, GL_COMPILE_STATUS) == GL_FALSE
                || glGetShaderi(fragmentShader2, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Não foi possivel compilar o shader 2");
        }

        shaderProgram1 = glCreateProgram();
        glAttachShader(shaderProgram1, vertexShader);
        glAttachShader(shaderProgram1, fragmentShader1);
        glLinkProgram(shaderProgram1);

        if (glGetProgrami(shaderProgram1, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Erro ao linkar shader program:\n" + glGetProgramInfoLog(shaderProgram1));
        }

        shaderProgrma2 = glCreateProgram();
        glAttachShader(shaderProgrma2, fragmentShader2);
        glAttachShader(shaderProgrma2, vertexShader);
        glLinkProgram(shaderProgrma2);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader1);
        glDeleteShader(fragmentShader2);

    }

}


