#version 330                                                                        
                                                                                    
layout (location = 0) in float type;                                                
layout (location = 1) in vec3 position;                                       
                            
out float pType;
                                                        
void main()                                                                         
{    
	pType=type;                                                                               
    gl_Position = vec4(position, 1.0);     
                                        
}                                                                                   
