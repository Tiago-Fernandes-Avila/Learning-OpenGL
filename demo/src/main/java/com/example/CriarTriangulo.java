package com.example;

import org.lwjgl.*;
import java.nio.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL33.*;




public class CriarTriangulo {
    private static int vao;
    private static int vbo;

    

    public static void criar(){
        //entrada de vertice etapa: 
       float vertices[] = {
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        0.0f,  0.5f, 0.0f
        };

    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
    vertexBuffer.put(vertices);
    vertexBuffer.flip(); // Important: call flip() to set the buffer's position to 0 and limit to the current position.

   
    int vboId = glGenBuffers(); // Generate a buffer ID
    glBindBuffer(GL_ARRAY_BUFFER, vboId); // Bind it to the GL_ARRAY_BUFFER target

    glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);  

    
    
    
    //shader de vertice etapa: 
      String fonteShaderDeVertice = """
    #version 330 core \n
    layout (location = 0) in vec3 aPos;\n
    void main()\n
    {\n
       gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n
    }\0;
    """;
    int shaderDeVertice = glCreateShader(GL_VERTEX_SHADER); 
    glShaderSource(shaderDeVertice, fonteShaderDeVertice);
    glCompileShader(shaderDeVertice);

    if (glGetShaderi(shaderDeVertice, GL_COMPILE_STATUS) == GL_FALSE) {
        throw new RuntimeException("Vertex Shader Erro:\n" + glGetShaderInfoLog(shaderDeVertice));
    }
    else{
        System.out.println("Compilação bem-sucedida!");
    }


    //etapa do shader de fragmento:
    String fonteShaderDerFragmento = """
            #version 330 core
            out vec4 FragColor;

            void main()
                    {
                        FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
                    }
            """;
            int shaderDeFragmento = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(shaderDeFragmento, fonteShaderDerFragmento);
            glCompileShader(shaderDeFragmento);
            if(glGetShaderi(shaderDeVertice, GL_COMPILE_STATUS) == 0){
                throw new RuntimeException("shader de fragmento: erro na compilação");
            }

        //etapa de vincular os programas de shader em um unico: 

         int shaderProgram = glCreateProgram();
         glAttachShader(shaderProgram, shaderDeVertice);
         glAttachShader(shaderProgram, shaderDeFragmento);
         glLinkProgram(shaderProgram);
           
        //verifica se a vinculacao foi bem sucedida
       if(glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE){
        throw new RuntimeException("Erro ao linkar shader program:\n" + glGetProgramInfoLog(shaderProgram));
       }
       
       //deleta os shaders que não serão mais usados
       glDeleteShader(shaderDeVertice);
       glDeleteShader(shaderDeFragmento);

       glUseProgram(shaderProgram);

       glVertexAttribPointer(vboId, shaderDeVertice, shaderDeFragmento, false, shaderProgram, vertexBuffer);
}


}

// primeiro passo para a pipeline do opengl é criar uma entrada de dados de vertices.


//ENTRADA de VERTICES:
//apos pegarmos o dados e armazenarmos no FloatBuffer do lwjgl que faz com que tenhamos uma memoria que seja acessivel pelo opengl. criamos um id especifico para este (buffer do OpenGL no caso GL_ARRAY_BUFFER que iremos mandar para a memoria da gpu).
//no final conectamos todas esses dados com o glBufferData que recebe como paramentros: o tipo do buffer para a memoria que conterá os dados de vertices no caso GL_ARRAY_BUFFER
//colocamos o buffer do lwjgl que contem um dado que está fora do controle da jvm 
// Em seguida, uma dica para o OpenGL sobre como o armazenamento de dados será acessado (modificado uma vez, usado várias vezes). Outras opções incluem GL_STREAM_DRAW, GL_DYNAMIC_DRAW, etc.

/*

a função original em c do glBindBuffer diz: (target ou seja que tipo de buffer iremos armazenar, o tamanho em bytes, o ponteiro para o array de dados no caso a matriz de vertices e por fim a dica de leitura para a memoria fazer otimizações) 
já em java não possuimos o luxo de poder passar ponteiros para que o opengl possa acessar no caso temos que usar 
a ferramenta do lwjgl para empacotar um buffer que terá o tamanho espefico definido pelo vertices.length e mandando direto os dados com o put para o buffer sendo no final apenas 3 parametros no java.

Até agora, armazenamos os dados dos vértices na memória da placa gráfica, gerenciada por um objeto de buffer de vértices chamado VBO . 
Em seguida, queremos criar um shader de vértices e um shader de fragmentos que processem esses dados; então, vamos começar a construí-lo

Shader de vértices e fragmento:
O shader de vértices é um dos shaders programáveis ​​por pessoas como nós. O OpenGL moderno exige que configuremos pelo menos um shader de vértices e um de fragmentos para realizar qualquer renderização
para isso vou utilizar a linguagem de shaders de glsl que é uma linguagem semelhante ao c. onde definimos em seu cabeçalho a sua versão

Vertex Shader	Processa cada vértice. Define posição, transformações (matrizes), pode passar dados pro fragment shader (como cor, textura…).

Fragment Shader	Processa cada fragmento (pixel) e define a cor final. Aqui você pode aplicar cor, textura, iluminação etc.

criamos os shaders e depois vinculamos eles a um programa de shader.

lembrando que na vinculaçao do shaders haverá erros caso não haja uma sincronia nas entradas e saidas dos dados. em outras palavras: ao vincular os shaders em um programa, as saídas de cada shader são conectadas às entradas do próximo. É aqui que você encontrará erros de vinculação se as saídas e entradas não corresponderem.


Etapa de VAO (Vertex Array Object):

 */
