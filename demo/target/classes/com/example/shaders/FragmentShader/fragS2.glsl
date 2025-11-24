#version 330 core
out vec4 FragColor1;
in vec4 vertexColor;
uniform vec4 colorFromCpu;
void main() {
    FragColor1 = vertexColor;
}