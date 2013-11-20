#version 330

in vec3 vertPos;
in vec4 vertColor;
uniform mat4 modelViewMatrix;
uniform mat4 projMatrix;

out vec4 varyingColor;

void main(void){
  varyingColor= vertColor; 
  vec4 vert = vec4(vertPos,1);
  vert = projMatrix*modelViewMatrix* vert;
  gl_Position= vert;
 }