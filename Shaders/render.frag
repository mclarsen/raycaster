#version 330                                                                        
                                                                                    


struct PositionalLight{
  vec4 ambient;
  vec4 diffuse;
  vec4 specular;
  vec3 position;
};

struct Material{
 vec4 ambient;
 vec4 diffuse;
 vec4 specular;
 float shininess;
};


uniform sampler2D colorMap;
uniform PositionalLight light;  
uniform vec4 globalAmbient;                                                      
in vec2 TexCoord;  
in vec3 normal; 
in vec3 lightDir; //in eye-space
in vec3 vertex;// in eye-space

out vec4 fragColor;                                                                  


void main()                                                                         
{      
	

	vec3 textColor=vec3(texture2D(colorMap, TexCoord.st)); 
	vec4 color;
	color=vec4(textColor,1);

	vec4 matAmb=vec4(.5,.5,.5,1);
	vec4 matDiff=vec4(.5,.5,.5,1);
	vec4 matSpec=vec4(.5,.5,.5,1);
	float matShininess=.4;

	vec4 phongColor;
	vec4 lightAmb= light.ambient;
	vec4 lightDiff= light.diffuse;
	vec4 lightSpec= light.specular;

	//compute normailized lights, normal, and eye dir vecs
	vec3 L = normalize(lightDir);
	vec3 N = normalize(normal);
	vec3 V = normalize( vec3(0,0,0)- normalize(vertex) ); // 

	//get angle between light and normal surface
	float cosTheta= dot(L,N);

	//reflect light vec 
	vec3 R = normalize( reflect(-L,N) );

	//compute angle between the vector to the eye and reflected light dir
	float cosPhi = dot (V,R);

	//compute reflected color
	phongColor= globalAmbient * matAmb + lightAmb*matAmb
			 	+ lightDiff * matDiff * max( cosTheta, 0.0 )
			 	+ lightSpec*matSpec * pow ( max(cosPhi, 0.0), matShininess);  		
	
	                                                            
	fragColor= color*phongColor;

	
      
                                                                              
}