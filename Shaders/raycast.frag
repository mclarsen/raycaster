#version 330                                                                        
                                                                                    




uniform sampler2D backFace;
//uniform PositionalLight light;  
//uniform vec4 globalAmbient;                                                      



in vec4 varyingColor;
out vec4 fragColor;                                                                  


void main()                                                                         
{      
	

	
	
	vec3 textColor=vec3(texture2D(backFace, TexCoord.ts));                   
	fragColor= varyingColor;

	
      
                                                                              
}