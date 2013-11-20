#version 330                                                                        
                                                                                    
layout(points) in;                                                                  
layout(points) out;                                                                 
layout(max_vertices = 30) out;                                                      
                                                                                    
in float type0[];                                                                   
in vec3 position0[];                                                                
in vec3 velocity0[];                                                                
in float age0[];                                                                    
                                                                                    
out float type1;                                                                    
out vec3 position1;                                                                 
out vec3 velocity1;                                                                 
out float age1;                                                                     
                                                                                    
uniform float timePassed;                                                     
uniform float time;                                                                
uniform sampler1D randomTexture;                                                   
uniform float launcherLifetime;                                                    
uniform float shellLifetime;                                                       
uniform float secondaryShellLifetime;                                              
                                                                                    
#define PARTICLE_TYPE_LAUNCHER 0.0f                                                 
#define PARTICLE_TYPE_SHELL 1.0f                                                    
#define PARTICLE_TYPE_SECONDARY_SHELL 2.0f                                          
                                                                                    
vec3 GetRandomDir(float TexCoord)                                                   
{                                                                                   
     vec3 Dir = texture(randomTexture, TexCoord).xyz;                              
     Dir -= vec3(0.5, 0.5, 0.5);                                                    
     return Dir;                                                                    
}                                                                                   
                                                                                    
void main()                                                                         
{                                                                                   
    float Age = age0[0] + timePassed;                                         
                                                                                    
    if (type0[0] == PARTICLE_TYPE_LAUNCHER) {                                       
        if (Age >= launcherLifetime) {         
        for(int i=0;i<1;i++){                                    
            type1 = PARTICLE_TYPE_SHELL;                                            
            position1 = position0[0];                                               
            vec3 Dir = GetRandomDir(time/1000.0);                                  
            Dir.y = max(Dir.y, 0.5);                                                
            velocity1 = normalize(Dir) / 20.0;                                      
            age1 = 0.0;                                                             
            EmitVertex();                                                           
            EndPrimitive();
            }                                                         
            Age = 0.0;                                                              
        }                                                                           
                                                                                    
        type1 = PARTICLE_TYPE_LAUNCHER;                                             
        position1 = position0[0];                                                   
        velocity1 = velocity0[0];                                                   
        age1 = Age;                                                                 
        EmitVertex();                                                               
        EndPrimitive();                                                             
    }                                                                               
    else {                                                                          
        float DeltaTimeSecs = timePassed / 1000.0f;                           
        float t1 = age0[0] / 1000.0;                                                
        float t2 = Age / 1000.0;                                                    
        vec3 DeltaP = DeltaTimeSecs * velocity0[0];                                 
        vec3 DeltaV = vec3(DeltaTimeSecs) * vec3(0.0, -.00981, 0.0); //**                      
                                                                                    
        if (type0[0] == PARTICLE_TYPE_SHELL)  {                                     
	        if (Age < shellLifetime) {                                             
	            type1 = PARTICLE_TYPE_SHELL;                                        
	            position1 = position0[0] + DeltaP;                                  
	            velocity1 = velocity0[0] + DeltaV;                                  
	            age1 = Age;                                                         
	            EmitVertex();                                                       
	            EndPrimitive();                                                     
	        }                                                                       
            else {                                                                  
                for (int i = 0 ; i < 10 ; i++) {                                    
                     type1 = PARTICLE_TYPE_SECONDARY_SHELL;                         
                     position1 = position0[0];                                      
                     vec3 Dir = GetRandomDir((time + i)/1000.0);                   
                     velocity1 = normalize(Dir) / 20.0;                             
                     age1 = 0.0f;                                                   
                     EmitVertex();                                                  
                     EndPrimitive();                                                
                }                                                                   
            }                                                                       
        }                                                                           
        else {                                                                      
            if (Age < secondaryShellLifetime) {                                    
                type1 = PARTICLE_TYPE_SECONDARY_SHELL;                              
                position1 = position0[0] + DeltaP;                                  
                velocity1 = velocity0[0] + DeltaV;                                  
                age1 = Age;                                                         
                EmitVertex();                                                       
                EndPrimitive();                                                     
            }                                                                       
        }                                                                           
    }                                                                               
} 