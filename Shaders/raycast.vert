#version 330

in vec3 vertPos;
in vec4 vertColor;
uniform mat4 modelViewMatrix;
uniform mat4 projMatrix;

out vec4 varyingColor;
out vec4 varyingVert;

void main(void){

	    

  //output the vertex and the color
  varyingColor= vertColor; 
  vec4 vert = vec4(vertPos,1);
  
  vert = projMatrix*modelViewMatrix* vert;
  varyingVert=projMatrix*modelViewMatrix* vert;
  gl_Position= vert;								//give this to satisfy the opengl Gods. Will never be used.
  
  
 }