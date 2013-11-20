#version 330                                                                        

struct PositionalLight{
  vec4 ambient;
  vec4 diffuse;
  vec4 specular;
  vec3 position;
};   
                                                                                    
layout(points) in;                                                                  
in float pType[];
layout(triangle_strip) out;                                                         
layout(max_vertices = 24) out;
                                                       
                                                                                    
uniform mat4 mviewMat;
uniform mat4 projMat;  
uniform mat4 normalMat;                                                          
uniform vec3 camPos;  
uniform vec3 camUp;                                                          
uniform float particleSize;  
uniform PositionalLight light;

                                                  
                                                                                    
out vec2 TexCoord;
out vec3 normal; 
out vec3 lightDir; //in eye-space
out vec3 vertex;// in eye-space


#define PARTICLE_TYPE_LAUNCHER 0.0f                                                 
#define PARTICLE_TYPE_SHELL 1.0f                                                    
#define PARTICLE_TYPE_SECONDARY_SHELL 2.0f                                                         
                    
const vec3[24] cubeCoords= vec3[] /* (   vec3(.5f,.5f,.5f),    vec3(.5f, -.5f,.5f),    vec3(-.5f,.5f,.5f),    vec3(-.5f,-.5f,.5f),     // front face
                                     vec3(.5f,.5f,.5f),    vec3(.5f,.5f,-.5f),    vec3(-.5f,.5f,.5f),     vec3(-.5f,.5f,-.5f), //top face
                                     vec3(.5f,.5f,-.5f),   vec3(-.5f,.5f,-.5f),   vec3(.5f,-.5f,-.5f), vec3(-.5f,-.5f,-.5f),                   // back face
                                     vec3(.5f,-.5f,.5f),   vec3(-.5f,-.5f,.5f),   vec3(.5f,-.5f,-.5f),    vec3(-.5f,-.5f,-.5f),      // bottom face
                                     vec3(.5f,.5f,-.5f),   vec3(.5f,.5f,.5f),     vec3(.5f,-.5f,-.5f),    vec3(.5f,-.5f,.5f),  //right face
                                     vec3(-.5f,.5f,.5f),   vec3(-.5f,.5f,-.5f),   vec3(-.5f,-.5f,.5f), vec3(-.5f,-.5f,-.5f));
 */                               
 (   vec3(-.5f,-.5f,.5f),    vec3(.5f, -.5f,.5f),    vec3(-.5f,.5f,.5f),    vec3(.5f,.5f,.5f),   //front
     vec3(.5f,-.5f,.5f),    vec3(.5f,-.5f,-.5f),    vec3(.5f,.5f,.5f),     vec3(.5f,.5f,-.5f), //right
     vec3(.5f,-.5f,-.5f),   vec3(-.5f,-.5f,-.5f),   vec3(.5f,.5f,-.5f), vec3(-.5f,.5f,-.5f),     //back

     vec3(-.5f,-.5f,-.5f),   vec3(-.5f,-.5f,.5f),   vec3(-.5f,.5f,-.5f),    vec3(-.5f,.5f,.5f),    //left
     vec3(-.5f,-.5f,-.5f),   vec3(.5f,-.5f,-.5f),     vec3(-.5f,-.5f,.5f),    vec3(.5f,-.5f,.5f),  //bottom
     vec3(-.5f,.5f,.5f),   vec3(.5f,.5f,.5f),   vec3(-.5f,.5f,-.5f), vec3(.5f,.5f,-.5f));          //top

const vec3[24] cubeNormals= vec3[]( vec3(0,0,1),   vec3(0,0,1),    vec3(0,0,1),   vec3(0,0,1),
                                    vec3(1,0,0),   vec3(1,0,0),    vec3(1,0,0),   vec3(1,0,0),
                                    vec3(0,0,-1),  vec3(0,0,-1),   vec3(0,0,-1),  vec3(0,0,-1),
                                    vec3(-1,0,0),  vec3(-1,0,0),   vec3(-1,0,0),  vec3(-1,0,0),
                                    vec3(0,-1,0),  vec3(0,-1,0),   vec3(0,-1,0),  vec3(0,-1,0),
                                    vec3(0,1,0),   vec3(0,1,0),    vec3(0,1,0),   vec3(0,1,0));





void main()                                                                         
{   //vars for lighing calculation
    vec4 lightPositionInEyeCoords=vec4(light.position,1);          // already in eye space 

    vec3 Pos = gl_in[0].gl_Position.xyz;                                            
    vec3 toCamera = normalize( Pos-camPos );       // this is reverse for the example(
    											   // I think the vertexes were in clockwise order(cull face)                             
    vec3 up = camUp;     
    float pSize=particleSize;
    if (pType[0]==PARTICLE_TYPE_SECONDARY_SHELL){                                             
    	pSize=particleSize/2;
    }
                            
   
    for (int i=0;i<24;i+=4){


      
        vec3 vertPos=vec3(cubeCoords[i]*pSize+Pos);                                                             
        gl_Position = projMat*mviewMat *vec4(vertPos,1);  
        normal=cubeNormals[i];                         
        normal= (normalMat*vec4(normal,0)).xyz;                  
        TexCoord = vec2(0.0, 0.0);   
        vec4 vertPosInEyeCoords= mviewMat*vec4(vertPos,1);
        vertex= vertPosInEyeCoords.xyz;                
        lightDir= (lightPositionInEyeCoords-vertPosInEyeCoords).xyz;                                                 
        EmitVertex();    

        vertPos=vec3(cubeCoords[i+1]*pSize+Pos);
        gl_Position = projMat*mviewMat *vec4(vertPos,1);   
        normal=cubeNormals[i+1];                                                                                      
        normal= (normalMat*vec4(normal,0)).xyz;
        TexCoord = vec2(0.0, 1.0);    
        vertPosInEyeCoords= mviewMat*vec4(vertPos,1);
        vertex= vertPosInEyeCoords.xyz;                
        lightDir= (lightPositionInEyeCoords-vertPosInEyeCoords).xyz;                                                  
        EmitVertex();                                                                
                                                                                        
                                                                          
        vertPos=vec3(cubeCoords[i+2]*pSize+Pos);                                                                                                                                                  
        gl_Position = projMat*mviewMat *vec4(vertPos,1);
        normal=cubeNormals[i+2];
        normal= (normalMat*vec4(normal,0)).xyz;
        TexCoord = vec2(1.0, 0.0);  
        vertPosInEyeCoords= mviewMat*vec4(vertPos,1);
        vertex= vertPosInEyeCoords.xyz;                
        lightDir= (lightPositionInEyeCoords-vertPosInEyeCoords).xyz;                                                    
        EmitVertex();  


        vertPos=vec3(cubeCoords[i+3]*pSize+Pos);
        gl_Position = projMat*mviewMat  *vec4(vertPos,1);
        normal=cubeNormals[i+3];
        normal= (normalMat*vec4(normal,0)).xyz;
        TexCoord = vec2(1.0, 1.0);
        vertPosInEyeCoords= mviewMat*vec4(vertPos,1);
        vertex= vertPosInEyeCoords.xyz;                
        lightDir= (lightPositionInEyeCoords-vertPosInEyeCoords).xyz;                                                      
        EmitVertex();                                                                  
                                                                                                                                               
         

    }                                                 
}  

