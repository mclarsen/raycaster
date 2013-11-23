#version 330                                                                        
                                                                                    




uniform sampler2D backFace;
uniform sampler3D volume;                                                     
uniform float stepSize;


in vec4 varyingColor;//this is where the ray penetrates the volume
in vec4 varyingVert;   
out vec4 fragColor;                                                                  


void main()                                                                         
{   float step=.005;

	//vec3 camPos= vec3(0,0,10);
	//vec3 theDir=varyingVert.xyz-camPos;
	
	
	//calculate the geometry
	vec2 backCoord=((varyingVert.xy/varyingVert.w+1)/2);//get the screenspace coordinates to lookup the right backface position
	vec4 outLoc=texture(backFace,backCoord);
	vec3 rayDir= vec3(0,0,0);
	rayDir.x=outLoc.x-varyingColor.x;
	rayDir.y=outLoc.y-varyingColor.y;
	rayDir.z=outLoc.z-varyingColor.z;
	
	float rayLength=length(rayDir.xyz);
	rayDir=normalize(rayDir);
	
	vec3 rayStep=step*rayDir;

	float stepLength=length(rayStep);
	vec3 currentPosition=varyingColor.xyz;
	float currentLength=0;
	float alphaChannel=0;
	vec3 currentColor=vec3(0,0,0);
	float sampleAlpha=0;
	vec4 sampleColor= vec4(0,0,0,0);
	

	for(int i=0; i<500;i++){
		sampleColor=texture(volume, currentPosition.xyz);
		sampleAlpha=sampleColor.a*step; //make sure we don't take the full alpha
//		currentColor+=(1-alphaChannel)*sampleColor.rgb*sampleColor.a*3;//no idea about the 3
		currentColor+=(1-alphaChannel)*sampleColor.rgb;
//		alphaChannel+=sampleAlpha;
		alphaChannel+=(1-alphaChannel)*sampleAlpha;
		
		//advance then check for termination
		currentPosition+=rayStep;
		currentLength+=stepLength;
		if(currentLength>=rayLength || alphaChannel>=1) break;
		if(i>300) {currentColor=
		 vec3(0,1,0);
		 alphaChannel=1;
		break;		
		}
	}
	
	//if(rayLength>0.5) fragColor=vec4(1,0,0,1);
	//else fragColor=vec4(0,1,0,1);
	//fragColor= outLoc;
	fragColor= vec4(currentColor, alphaChannel);
	//fragColor=texture(volume, varyingColor.xyz);
	
	
      
                                                                              
}